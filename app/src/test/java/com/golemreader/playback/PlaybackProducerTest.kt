package com.golemreader.playback

import android.content.Context
import com.golemreader.audio.SynthesisHarness
import com.golemreader.text.ClauseTag
import com.golemreader.text.SegmentType
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import com.golemreader.voice.SynthesizedAudio
import com.golemreader.voice.VoiceEngine
import com.golemreader.voice.VoiceEngineCapabilities
import com.golemreader.voice.VoiceSynthesisRequest
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackProducerTest {
    @Test
    fun smallFirstRendersOnlyInitialSegmentAfterSeek() {
        val buffer = StreamingBuffer()
        val engine = FakeVoiceEngine(sampleCount = 1_000, sampleRateHz = 1_000)
        val producer = PlaybackProducer(
            sentences = listOf(sentence(0), sentence(1), sentence(2)),
            harness = SynthesisHarness(),
            engine = engine,
            buffer = buffer,
            maxLookAheadSeconds = 10.0,
            smallFirstCount = 1,
        )

        val rendered = producer.renderSmallFirstFrom(index(1))

        assertEquals(1, rendered)
        assertEquals(listOf(index(1)), engine.requests)
        assertEquals(1.0, buffer.depthSeconds(), 0.0001)
    }

    @Test
    fun lookAheadStopsAtBoundedRenderedSeconds() {
        val buffer = StreamingBuffer()
        val engine = FakeVoiceEngine(sampleCount = 1_000, sampleRateHz = 1_000)
        val producer = PlaybackProducer(
            sentences = listOf(sentence(0), sentence(1), sentence(2), sentence(3), sentence(4)),
            harness = SynthesisHarness(),
            engine = engine,
            buffer = buffer,
            maxLookAheadSeconds = 2.0,
            smallFirstCount = 1,
        )

        val rendered = producer.renderLookAheadFrom(index(0))

        assertEquals(2, rendered)
        assertEquals(listOf(index(0), index(1)), engine.requests)
        assertEquals(2.0, buffer.depthSeconds(), 0.0001)
    }

    private class FakeVoiceEngine(
        private val sampleCount: Int,
        private val sampleRateHz: Int,
    ) : VoiceEngine {
        val requests = mutableListOf<SentenceIndex>()

        override fun load(context: Context, modelRoot: File) = Unit

        override fun speak(request: VoiceSynthesisRequest): SynthesizedAudio {
            requests += request.sentenceIndex
            return SynthesizedAudio(
                sentenceIndex = request.sentenceIndex,
                samples = FloatArray(sampleCount) { 0.2f },
                sampleRateHz = sampleRateHz,
                synthesizeToFirstSampleMillis = 1,
            )
        }

        override fun stop() = Unit

        override fun release() = Unit

        override fun reportCapabilities() = VoiceEngineCapabilities(
            engineName = "fake",
            supportsAbort = true,
            supportsMultipleSpeakers = false,
            supportsSpeed = false,
        )
    }

    private fun sentence(sentence: Int) = SentenceRecord(
        index = index(sentence),
        display = "display $sentence",
        spoken = "spoken $sentence",
        segmentType = SegmentType.SentenceTerminal,
        clauseTag = ClauseTag(parentSentenceOrdinal = sentence, clauseOrdinal = 0),
    )

    private fun index(sentence: Int) =
        SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = sentence)
}
