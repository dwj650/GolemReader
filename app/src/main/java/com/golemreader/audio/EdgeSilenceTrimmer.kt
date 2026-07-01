package com.golemreader.audio

import com.golemreader.voice.SynthesizedAudio
import kotlin.math.abs
import kotlin.math.max

data class EdgeTrimDefaults(
    val threshold: Float = 0.003f,
    val speechFloorMillis: Int = 15,
)

class EdgeSilenceTrimmer(
    private val defaults: EdgeTrimDefaults = EdgeTrimDefaults(),
) {
    fun trim(audio: SynthesizedAudio): SynthesizedAudio {
        val samples = audio.samples
        if (samples.isEmpty()) {
            return audio.copy(samples = samples.copyOf())
        }

        val firstSpeech = samples.indexOfFirst { abs(it) >= defaults.threshold }
        if (firstSpeech == -1) {
            return audio.copy(samples = FloatArray(0))
        }

        val lastSpeech = samples.indexOfLast { abs(it) >= defaults.threshold }
        val floorSamples = max(0, (audio.sampleRateHz * defaults.speechFloorMillis) / 1000)
        val start = max(0, firstSpeech - floorSamples)
        val endExclusive = (lastSpeech + floorSamples + 1).coerceAtMost(samples.size)
        return audio.copy(samples = samples.copyOfRange(start, endExclusive))
    }
}
