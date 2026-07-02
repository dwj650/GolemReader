package com.golemreader.playback

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.golemreader.audio.SynthesisHarness
import com.golemreader.identity.BookIdentityHasher
import com.golemreader.identity.EpubStructuralReader
import com.golemreader.text.EpubTextExtractor
import com.golemreader.text.PreCleanStage
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import com.golemreader.text.SentenceSegmenter
import com.golemreader.text.TextPipeline
import com.golemreader.transport.TransportCommands
import com.golemreader.transport.TransportHub
import com.golemreader.voice.SynthesizedAudio
import com.golemreader.voice.VoiceEngine
import com.golemreader.voice.VoiceEngineCapabilities
import com.golemreader.voice.VoiceSynthesisRequest
import java.io.File
import kotlin.math.sin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaybackSessionDeviceTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun tomSawyerSessionRespondsToTransportCommandsOverWallClockTime() {
        val passage = tomSawyerPassage()
        val nextIndexes = passage.zipWithNext { current, next -> current.index to next.index }.toMap()
        val buffer = StreamingBuffer()
        val engine = ToneVoiceEngine()
        val producer = PlaybackProducer(
            sentences = passage,
            harness = SynthesisHarness(),
            engine = engine,
            buffer = buffer,
            maxLookAheadSeconds = SEGMENT_SECONDS,
            smallFirstCount = 1,
        )
        val sink = RecordingAudioSink()
        val consumer = PlaybackConsumer(
            buffer = buffer,
            audioSink = sink,
            starvationState = StarvationState(),
        )
        var target = passage.first().index
        val abort = AbortController(
            stopProducer = producer::stop,
            flushBuffer = buffer::flush,
            flushAudioSink = sink::flush,
            setTarget = { target = it },
            rerenderSmallFirst = { producer.renderSmallFirstFrom(target) },
        )
        val session = PlaybackSession(
            intentLoop = IntentLoop(debounceMillis = 20),
            producer = producer.asDriver(),
            consumer = consumer.asDriver(),
            abortController = abort.asDriver(),
            starvationState = StarvationState(),
            initialTarget = passage.first().index,
            flushBuffer = buffer::flush,
            nextAfter = { nextIndexes[it] },
            tickMillis = 5,
        )
        val hub = TransportHub.attach(session)
        val commands = TransportCommands(hub)

        session.start()
        assertTrue(await { sink.playedCount() >= 2 })

        commands.pause()
        Thread.sleep(SEGMENT_MILLIS * 3)
        val pausedCount = sink.playedCount()
        Thread.sleep(SEGMENT_MILLIS * 3)
        assertEquals(pausedCount, sink.playedCount())
        assertTrue(session.isRunning)

        commands.resume()
        assertTrue(await { sink.playedCount() > pausedCount })

        val seekTarget = passage.last().index
        hub.seekTo(seekTarget)
        assertTrue(await { sink.contains(seekTarget) })

        commands.stop()
        session.join(timeoutMillis = 2_000)
        assertFalse(session.isRunning)
        assertTrue(sink.flushCount > 0)
        Log.i(TAG, "S8 session played=${sink.playedSnapshot().map { "${it.chapterOrdinal}:${it.sentenceOrdinal}" }} flushes=${sink.flushCount}")
    }

    private fun tomSawyerPassage(): List<SentenceRecord> {
        val book = File(context.externalMediaDirs.first(), "fixtures/text/tom-sawyer.epub")
        assertTrue(
            "Push app/src/test/resources/fixtures/text/tom-sawyer.epub to ${book.absolutePath} before running this test.",
            book.exists(),
        )
        val readAhead = pipeline().processChapterWithReadAhead(
            book = book,
            bookHash = BookIdentityHasher().hash(book),
            chapterOrdinal = 5,
            lookAheadChapterCount = 1,
        )
        val chapterFiveTail = readAhead.chapters.first { it.chapterOrdinal == 5 }.sentences.takeLast(3)
        val chapterSixHead = readAhead.chapters.first { it.chapterOrdinal == 6 }.sentences.take(3)
        return chapterFiveTail + chapterSixHead
    }

    private fun pipeline() = TextPipeline(
        extractor = EpubTextExtractor(EpubStructuralReader()),
        preCleanStage = PreCleanStage(),
        segmenter = SentenceSegmenter(maxSegmentCharacters = 800),
    )

    private class RecordingAudioSink : AudioSink {
        val played = mutableListOf<SentenceIndex>()
        var flushCount = 0
            private set

        @Synchronized
        override fun play(audio: SynthesizedAudio) {
            played += audio.sentenceIndex
            Thread.sleep(SEGMENT_MILLIS)
        }

        @Synchronized
        override fun flush() {
            flushCount += 1
        }

        @Synchronized
        fun playedCount(): Int = played.size

        @Synchronized
        fun contains(index: SentenceIndex): Boolean = played.contains(index)

        @Synchronized
        fun playedSnapshot(): List<SentenceIndex> = played.toList()
    }

    private class ToneVoiceEngine : VoiceEngine {
        override fun load(context: Context, modelRoot: File) = Unit

        override fun speak(request: VoiceSynthesisRequest): SynthesizedAudio {
            val sampleRateHz = 1_000
            val samples = FloatArray(SEGMENT_MILLIS.toInt()) { index ->
                (sin(index.toDouble() / 4.0) * 0.12).toFloat()
            }
            return SynthesizedAudio(
                sentenceIndex = request.sentenceIndex,
                samples = samples,
                sampleRateHz = sampleRateHz,
                synthesizeToFirstSampleMillis = 1,
            )
        }

        override fun stop() = Unit

        override fun release() = Unit

        override fun reportCapabilities() = VoiceEngineCapabilities(
            engineName = "tone",
            supportsAbort = true,
            supportsMultipleSpeakers = false,
            supportsSpeed = false,
            sampleRateHz = 1_000,
        )
    }

    private fun await(condition: () -> Boolean): Boolean {
        val deadline = System.currentTimeMillis() + 3_000
        while (System.currentTimeMillis() < deadline) {
            if (condition()) return true
            Thread.sleep(10)
        }
        return condition()
    }

    private companion object {
        const val TAG = "PlaybackSessionDevice"
        const val SEGMENT_MILLIS = 40L
        const val SEGMENT_SECONDS = 0.04
    }
}
