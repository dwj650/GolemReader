package com.golemreader.ui

import android.content.Context
import android.util.Log
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.golemreader.audio.SynthesisHarness
import com.golemreader.highlight.HighlightClock
import com.golemreader.highlight.HighlightIndexMapper
import com.golemreader.highlight.HighlightStateEmitter
import com.golemreader.identity.BookIdentityHasher
import com.golemreader.identity.EpubStructuralReader
import com.golemreader.playback.AbortController
import com.golemreader.playback.AudioSink
import com.golemreader.playback.IntentLoop
import com.golemreader.playback.PlaybackConsumer
import com.golemreader.playback.PlaybackProducer
import com.golemreader.playback.PlaybackSession
import com.golemreader.playback.StarvationState
import com.golemreader.playback.StreamingBuffer
import com.golemreader.playback.asDriver
import com.golemreader.text.EpubTextExtractor
import com.golemreader.text.PreCleanStage
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import com.golemreader.text.SentenceSegmenter
import com.golemreader.text.TextPipeline
import com.golemreader.transport.TransportCommands
import com.golemreader.transport.TransportHub
import com.golemreader.ui.nowplaying.NowPlayingTransportControls
import com.golemreader.ui.nowplaying.bufferingStatusText
import com.golemreader.ui.reading.highlightedSentenceIndex
import com.golemreader.ui.reading.readingRows
import com.golemreader.ui.reading.ReadingViewScreen
import com.golemreader.voice.SynthesizedAudio
import com.golemreader.voice.VoiceEngine
import com.golemreader.voice.VoiceEngineCapabilities
import com.golemreader.voice.VoiceSynthesisRequest
import java.io.File
import kotlin.math.sin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReadingAndNowPlayingDeviceTest {
    @get:Rule
    val compose = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun tomSawyerReadAlongFeedsReadingAndNowPlayingUiState() {
        val passage = tomSawyerPassage()
        val nextIndexes = passage.zipWithNext { current, next -> current.index to next.index }.toMap()
        val buffer = StreamingBuffer()
        val starvation = StarvationState()
        val mapper = HighlightIndexMapper(passage)
        val emitter = HighlightStateEmitter()
        val clock = HighlightClock()
        val producer = PlaybackProducer(
            sentences = passage,
            harness = SynthesisHarness(),
            engine = ToneVoiceEngine(),
            buffer = buffer,
            maxLookAheadSeconds = SEGMENT_SECONDS,
            smallFirstCount = 1,
        )
        val sink = RecordingAudioSink()
        val consumer = PlaybackConsumer(
            buffer = buffer,
            audioSink = sink,
            starvationState = starvation,
            onSegmentStarted = { audio ->
                emitter.emit(
                    target = requireNotNull(mapper.targetFor(audio.sentenceIndex)),
                    timing = clock.segmentStarted(audio),
                )
            },
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
            starvationState = starvation,
            initialTarget = passage.first().index,
            flushBuffer = buffer::flush,
            nextAfter = { nextIndexes[it] },
            tickMillis = 5,
        )
        val controls = NowPlayingTransportControls(
            TransportCommands(TransportHub.attach(session)),
        )

        session.start()
        assertTrue(await { sink.playedCount() >= 2 })

        val rows = readingRows(passage, requireNotNull(emitter.currentState()))
        assertNotNull(highlightedSentenceIndex(rows))
        assertTrue(rows.any { it.isHighlighted && it.text == passage.first { sentence -> sentence.index == it.index }.display })
        assertReadingHighlightRenderedAndAdvances(
            passage = passage,
            emitter = emitter,
            firstVisibleIndex = requireNotNull(highlightedSentenceIndex(rows)),
        )

        controls.pause()
        val pausedCount = sink.playedCount()
        Thread.sleep(SEGMENT_MILLIS * 3)
        assertEquals(pausedCount, sink.playedCount())

        controls.resume()
        assertTrue(await { sink.playedCount() > pausedCount })

        starvation.onConsumerOutrunsProducer()
        assertEquals("Catching up...", bufferingStatusText(starvation.isBuffering))
        starvation.onAudioRefilled()
        assertEquals(null, bufferingStatusText(starvation.isBuffering))

        controls.stop()
        session.join(timeoutMillis = 2_000)
        Log.i(TAG, "S9 highlighted=${highlightedSentenceIndex(rows)} played=${sink.playedSnapshot().size}")
    }

    private fun assertReadingHighlightRenderedAndAdvances(
        passage: List<SentenceRecord>,
        emitter: HighlightStateEmitter,
        firstVisibleIndex: SentenceIndex,
    ) {
        compose.setContent {
            ReadingViewScreen(
                bookTitle = "Tom Sawyer",
                sentences = passage,
                highlightEmitter = emitter,
                pollingIntervalMillis = 10L,
            )
        }

        val firstText = requireNotNull(passage.firstOrNull { it.index == firstVisibleIndex }).display
        compose.waitUntil(timeoutMillis = 2_000) {
            compose.onAllNodesWithTag("reading-highlight").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("reading-highlight").assertIsDisplayed()
        compose.onNodeWithText(firstText).assertIsDisplayed()

        val later = passage.last()
        emitter.emit(requireNotNull(HighlightIndexMapper(passage).targetFor(later.index)))
        compose.waitUntil(timeoutMillis = 2_000) {
            compose.onAllNodesWithTag("reading-highlight").fetchSemanticsNodes().isNotEmpty()
        }
        compose.waitUntil(timeoutMillis = 2_000) {
            compose.onAllNodesWithText(later.display).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("reading-highlight").assertIsDisplayed()
        compose.onNodeWithText(later.display).assertIsDisplayed()
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
        return readAhead.chapters.first { it.chapterOrdinal == 5 }.sentences.takeLast(5)
    }

    private fun pipeline() = TextPipeline(
        extractor = EpubTextExtractor(EpubStructuralReader()),
        preCleanStage = PreCleanStage(),
        segmenter = SentenceSegmenter(maxSegmentCharacters = 800),
    )

    private class RecordingAudioSink : AudioSink {
        private val played = mutableListOf<SentenceIndex>()

        @Synchronized
        override fun play(audio: SynthesizedAudio) {
            played += audio.sentenceIndex
            Thread.sleep(SEGMENT_MILLIS)
        }

        override fun flush() = Unit

        @Synchronized
        fun playedCount(): Int = played.size

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
        const val TAG = "ReadingNowPlayingDevice"
        const val SEGMENT_MILLIS = 40L
        const val SEGMENT_SECONDS = 0.04
    }
}
