package com.golemreader.highlight

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.golemreader.audio.SynthesisHarness
import com.golemreader.identity.BookIdentityHasher
import com.golemreader.identity.EpubStructuralReader
import com.golemreader.playback.PlaybackConsumer
import com.golemreader.playback.PlaybackProducer
import com.golemreader.playback.StarvationState
import com.golemreader.playback.StreamingBuffer
import com.golemreader.text.EpubTextExtractor
import com.golemreader.text.PreCleanStage
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import com.golemreader.text.SentenceSegmenter
import com.golemreader.text.TextPipeline
import com.golemreader.voice.KokoroVoiceEngine
import com.golemreader.voice.PiperVoiceEngine
import com.golemreader.voice.SynthesizedAudio
import com.golemreader.voice.VoiceEngine
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToLong
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HighlightSyncDeviceTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun kokoroAndPiperEmitSameCrossChapterHighlightTrackOnSampleBoundaries() {
        val passage = crossChapterPassage()
        val kokoro = runHighlightTrack(
            engine = KokoroVoiceEngine(),
            modelRoot = File(context.externalMediaDirs.first(), "test-voices/kokoro"),
            requiredModelFile = "model.onnx",
            passage = passage,
        )
        val piper = runHighlightTrack(
            engine = PiperVoiceEngine(),
            modelRoot = File(context.externalMediaDirs.first(), "test-voices/piper"),
            requiredModelFile = "en_US-lessac-medium.onnx",
            passage = passage,
        )

        assertEquals(passage.map { it.index }, kokoro.map { it.index })
        assertEquals(passage.map { it.index }, piper.map { it.index })
        assertEquals(kokoro.map { it.index }, piper.map { it.index })
    }

    private fun runHighlightTrack(
        engine: VoiceEngine,
        modelRoot: File,
        requiredModelFile: String,
        passage: List<SentenceRecord>,
    ): List<Signal> {
        assertTrue(
            "Push ${engine.reportCapabilities().engineName} model files to ${modelRoot.absolutePath} before running this test.",
            modelRoot.resolve(requiredModelFile).exists(),
        )
        val buffer = StreamingBuffer()
        val harness = SynthesisHarness()
        val mapper = HighlightIndexMapper(passage)
        val emitter = HighlightStateEmitter()
        val clock = HighlightClock(nowMillis = { SystemClock.elapsedRealtime() })
        val signals = mutableListOf<Signal>()
        val playedAudio = mutableListOf<SynthesizedAudio>()

        try {
            engine.load(context, modelRoot)
            PlaybackProducer(
                sentences = passage,
                harness = harness,
                engine = engine,
                buffer = buffer,
                maxLookAheadSeconds = 120.0,
                smallFirstCount = 1,
            ).renderLookAheadFrom(passage.first().index)

            val consumer = PlaybackConsumer(
                buffer = buffer,
                audioSink = harness.createAudioSink(),
                starvationState = StarvationState(),
                onSegmentStarted = { audio ->
                    val timing = clock.segmentStarted(audio)
                    val target = requireNotNull(mapper.targetFor(audio.sentenceIndex))
                    emitter.emit(target, timing)
                    signals += Signal(
                        engineName = engine.reportCapabilities().engineName,
                        index = audio.sentenceIndex,
                        startedAtMillis = timing.startedAtMillis,
                        durationMillis = (timing.durationSeconds * 1_000.0).roundToLong(),
                    )
                    playedAudio += audio
                    Log.i(
                        TAG,
                        "engine=${engine.reportCapabilities().engineName} highlight=${audio.sentenceIndex.chapterOrdinal}:${audio.sentenceIndex.sentenceOrdinal} start=${timing.startedAtMillis} durationMs=${(timing.durationSeconds * 1_000.0).roundToLong()}",
                    )
                },
            )

            repeat(passage.size) {
                assertTrue("Consumer starved before ${engine.reportCapabilities().engineName} completed S7 passage.", consumer.playNext())
            }
        } finally {
            engine.release()
        }

        assertEquals(passage.size, signals.size)
        assertEquals(passage.size, playedAudio.size)
        assertTrue("S7 passage must cross a chapter boundary.", signals.map { it.index.chapterOrdinal }.distinct().size > 1)
        assertSampleBoundaryOffsets(signals)
        return signals
    }

    private fun assertSampleBoundaryOffsets(signals: List<Signal>) {
        val firstStartedAt = signals.first().startedAtMillis
        var expectedOffset = 0L
        signals.forEachIndexed { signalIndex, signal ->
            val actualOffset = signal.startedAtMillis - firstStartedAt
            val drift = abs(actualOffset - expectedOffset)
            assertTrue(
                "Highlight boundary drift for ${signal.engineName} signal $signalIndex was ${drift}ms, expected <= ${BOUNDARY_TOLERANCE_MILLIS}ms.",
                drift <= BOUNDARY_TOLERANCE_MILLIS,
            )
            expectedOffset += signal.durationMillis
        }
    }

    private fun crossChapterPassage(): List<SentenceRecord> {
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
        val chapterFiveLast = readAhead.chapters.first { it.chapterOrdinal == 5 }.sentences.last()
        val chapterSixFirst = readAhead.chapters.first { it.chapterOrdinal == 6 }.sentences.first()
        return listOf(chapterFiveLast, chapterSixFirst)
    }

    private fun pipeline() = TextPipeline(
        extractor = EpubTextExtractor(EpubStructuralReader()),
        preCleanStage = PreCleanStage(),
        segmenter = SentenceSegmenter(maxSegmentCharacters = 800),
    )

    private data class Signal(
        val engineName: String,
        val index: SentenceIndex,
        val startedAtMillis: Long,
        val durationMillis: Long,
    )

    private companion object {
        const val TAG = "HighlightSyncDevice"
        const val BOUNDARY_TOLERANCE_MILLIS = 250L
    }
}
