package com.golemreader.playback

import com.golemreader.text.SentenceIndex
import com.golemreader.voice.SynthesizedAudio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

class StreamingBufferTest {
    @Test
    fun depthUsesRenderedSecondsFromSamplesAndSampleRate() {
        val buffer = StreamingBuffer()

        buffer.enqueue(audio(chapter = 5, sentence = 0, sampleCount = 24_000, sampleRateHz = 24_000))
        buffer.enqueue(audio(chapter = 5, sentence = 1, sampleCount = 11_025, sampleRateHz = 22_050))

        assertEquals(1.5, buffer.depthSeconds(), 0.0001)
    }

    @Test
    fun bufferStoresCopiesAndFlushesWithoutMutatingOriginalAudio() {
        val buffer = StreamingBuffer()
        val original = audio(chapter = 5, sentence = 0, sampleCount = 4, sampleRateHz = 2)

        buffer.enqueue(original)
        original.samples[0] = 99f

        val first = buffer.poll()

        assertEquals(0.1f, first?.audio?.samples?.get(0) ?: -1f, 0.0001f)
        assertNotSame(original.samples, first?.audio?.samples)
        assertEquals(0.0, buffer.depthSeconds(), 0.0001)

        buffer.enqueue(original)
        buffer.flush()

        assertTrue(buffer.isEmpty())
        assertEquals(0.0, buffer.depthSeconds(), 0.0001)
    }

    private fun audio(
        chapter: Int,
        sentence: Int,
        sampleCount: Int,
        sampleRateHz: Int,
    ) = SynthesizedAudio(
        sentenceIndex = SentenceIndex(bookHash = "book", chapterOrdinal = chapter, sentenceOrdinal = sentence),
        samples = FloatArray(sampleCount) { 0.1f },
        sampleRateHz = sampleRateHz,
        synthesizeToFirstSampleMillis = 1,
    )
}
