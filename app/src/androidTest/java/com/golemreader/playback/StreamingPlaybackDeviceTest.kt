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
import com.golemreader.text.SentenceSegmenter
import com.golemreader.text.TextPipeline
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
class StreamingPlaybackDeviceTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun tomSawyerStreamsAcrossRealChapterBoundaryWithoutManualIntervention() {
        val book = File(context.externalMediaDirs.first(), "fixtures/text/tom-sawyer.epub")
        assertTrue(
            "Push app/src/test/resources/fixtures/text/tom-sawyer.epub to ${book.absolutePath} before running this test.",
            book.exists(),
        )
        val bookHash = BookIdentityHasher().hash(book)
        val readAhead = pipeline().processChapterWithReadAhead(
            book = book,
            bookHash = bookHash,
            chapterOrdinal = 5,
            lookAheadChapterCount = 1,
        )
        val playbackSentences = readAhead.orderedSentences
        val buffer = StreamingBuffer()
        val engine = ToneVoiceEngine()
        val producer = PlaybackProducer(
            sentences = playbackSentences,
            harness = SynthesisHarness(),
            engine = engine,
            buffer = buffer,
            maxLookAheadSeconds = 12.0,
            smallFirstCount = 1,
        )
        val starvation = StarvationState()
        val sink = SynthesisHarness().createAudioSink()
        val consumer = PlaybackConsumer(
            buffer = buffer,
            audioSink = sink,
            starvationState = starvation,
            interSentenceGapMillis = 0,
        )

        val rendered = producer.renderLookAheadFrom(playbackSentences.first().index)
        repeat(rendered) {
            assertTrue("Consumer starved before the chapter boundary stream completed.", consumer.playNext())
        }

        assertFalse(starvation.isBuffering)
        assertEquals(playbackSentences.size, rendered)
        assertTrue(engine.requests.any { it.chapterOrdinal == 5 })
        assertTrue(engine.requests.any { it.chapterOrdinal == 6 })
        assertTrue(engine.requests.first().chapterOrdinal == 5)
        assertTrue(engine.requests.last().chapterOrdinal == 6)
        assertTrue(readAhead.readAheadEvidence.contains("processed chapter 6 ahead of boundary 5"))
        Log.i(TAG, "S6 streamed ${engine.requests.size} Tom Sawyer chapter-5/6 sentences start-to-finish.")
    }

    private class ToneVoiceEngine : VoiceEngine {
        val requests = mutableListOf<SentenceIndex>()

        override fun load(context: Context, modelRoot: File) = Unit

        override fun speak(request: VoiceSynthesisRequest): SynthesizedAudio {
            requests += request.sentenceIndex
            val sampleRateHz = 24_000
            val samples = FloatArray(sampleRateHz / 120) { index ->
                (sin(index.toDouble() / 8.0) * 0.12).toFloat()
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
            sampleRateHz = 24_000,
        )
    }

    private fun pipeline() = TextPipeline(
        extractor = EpubTextExtractor(EpubStructuralReader()),
        preCleanStage = PreCleanStage(),
        segmenter = SentenceSegmenter(maxSegmentCharacters = 800),
    )

    private companion object {
        const val TAG = "StreamingPlaybackDevice"
    }
}
