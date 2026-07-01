package com.golemreader.voice

import com.golemreader.text.SentenceIndex

data class VoiceEngineCapabilities(
    val engineName: String,
    val supportsAbort: Boolean,
    val supportsMultipleSpeakers: Boolean,
    val supportsSpeed: Boolean,
    val sampleRateHz: Int? = null,
    val speakerCount: Int? = null,
)

data class VoiceSynthesisRequest(
    val sentenceIndex: SentenceIndex,
    val text: String,
    val speakerId: Int = 0,
    val speed: Float = 1.0f,
    val shouldAbort: () -> Boolean = { false },
)

data class SynthesizedAudio(
    val sentenceIndex: SentenceIndex,
    val samples: FloatArray,
    val sampleRateHz: Int,
    val synthesizeToFirstSampleMillis: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SynthesizedAudio) return false
        return sentenceIndex == other.sentenceIndex &&
            samples.contentEquals(other.samples) &&
            sampleRateHz == other.sampleRateHz &&
            synthesizeToFirstSampleMillis == other.synthesizeToFirstSampleMillis
    }

    override fun hashCode(): Int {
        var result = sentenceIndex.hashCode()
        result = 31 * result + samples.contentHashCode()
        result = 31 * result + sampleRateHz
        result = 31 * result + synthesizeToFirstSampleMillis.hashCode()
        return result
    }
}
