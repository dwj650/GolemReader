package com.golemreader.audio

import com.golemreader.text.SentenceIndex
import com.golemreader.voice.SynthesizedAudio
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class EdgeSilenceTrimmerTest {
    @Test
    fun trimsLeadingAndTrailingNearSilenceToZeroBaseline() {
        val index = SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = 7)
        val audio = SynthesizedAudio(
            sentenceIndex = index,
            samples = floatArrayOf(0f, 0.001f, 0.004f, 0.2f, -0.004f, 0.002f, 0f),
            sampleRateHz = 1000,
            synthesizeToFirstSampleMillis = 12,
        )

        val trimmed = EdgeSilenceTrimmer(defaults = EdgeTrimDefaults(threshold = 0.003f, speechFloorMillis = 0))
            .trim(audio)

        assertArrayEquals(floatArrayOf(0.004f, 0.2f, -0.004f), trimmed.samples, 0.0001f)
        assertEquals(index, trimmed.sentenceIndex)
        assertEquals(1000, trimmed.sampleRateHz)
        assertEquals(12, trimmed.synthesizeToFirstSampleMillis)
        assertNotSame(audio.samples, trimmed.samples)
    }

    @Test
    fun speechProtectingFloorKeepsNearSpeechContextAtBothEdges() {
        val audio = SynthesizedAudio(
            sentenceIndex = SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = 8),
            samples = floatArrayOf(0f, 0.001f, 0.004f, 0.2f, -0.004f, 0.002f, 0f),
            sampleRateHz = 1000,
            synthesizeToFirstSampleMillis = 12,
        )

        val trimmed = EdgeSilenceTrimmer(defaults = EdgeTrimDefaults(threshold = 0.003f, speechFloorMillis = 2))
            .trim(audio)

        assertArrayEquals(floatArrayOf(0f, 0.001f, 0.004f, 0.2f, -0.004f, 0.002f, 0f), trimmed.samples, 0.0001f)
    }

    @Test
    fun zeroLengthSegmentKeepsSentenceIndexAnchored() {
        val index = SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = 9)
        val audio = SynthesizedAudio(
            sentenceIndex = index,
            samples = floatArrayOf(),
            sampleRateHz = 22050,
            synthesizeToFirstSampleMillis = 0,
        )

        val trimmed = EdgeSilenceTrimmer().trim(audio)

        assertEquals(index, trimmed.sentenceIndex)
        assertEquals(0, trimmed.samples.size)
        assertNotSame(audio.samples, trimmed.samples)
    }

    @Test
    fun allSilenceSegmentReturnsEmptyWithoutCrash() {
        val index = SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = 10)
        val audio = SynthesizedAudio(
            sentenceIndex = index,
            samples = floatArrayOf(0f, 0.001f, -0.002f),
            sampleRateHz = 22050,
            synthesizeToFirstSampleMillis = 0,
        )

        val trimmed = EdgeSilenceTrimmer().trim(audio)

        assertEquals(index, trimmed.sentenceIndex)
        assertEquals(0, trimmed.samples.size)
    }
}
