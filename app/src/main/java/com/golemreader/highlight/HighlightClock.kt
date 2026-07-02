package com.golemreader.highlight

import com.golemreader.text.SentenceIndex
import com.golemreader.voice.SynthesizedAudio
import kotlin.math.roundToLong

data class HighlightTiming(
    val sentenceIndex: SentenceIndex,
    val startedAtMillis: Long,
    val durationSeconds: Double,
    val expectedEndMillis: Long,
)

class HighlightClock(
    private val nowMillis: () -> Long = { System.currentTimeMillis() },
) {
    fun segmentStarted(audio: SynthesizedAudio): HighlightTiming {
        val startedAt = nowMillis()
        val durationSeconds = if (audio.sampleRateHz <= 0) {
            0.0
        } else {
            audio.samples.size.toDouble() / audio.sampleRateHz.toDouble()
        }
        return HighlightTiming(
            sentenceIndex = audio.sentenceIndex,
            startedAtMillis = startedAt,
            durationSeconds = durationSeconds,
            expectedEndMillis = startedAt + (durationSeconds * 1_000.0).roundToLong(),
        )
    }
}
