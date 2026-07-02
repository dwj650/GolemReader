package com.golemreader.highlight

import com.golemreader.text.ClauseTag
import com.golemreader.text.SentenceIndex

data class HighlightGlowParameters(
    val size: Float = 1.0f,
    val contrast: Float = 1.0f,
    val fadeMillis: Long = 120L,
)

data class HighlightState(
    val sentenceIndex: SentenceIndex,
    val clauseTag: ClauseTag,
    val glowParameters: HighlightGlowParameters,
    val startedAtMillis: Long?,
    val durationSeconds: Double?,
    val expectedEndMillis: Long?,
)

class HighlightStateEmitter(
    initialGlowParameters: HighlightGlowParameters = HighlightGlowParameters(),
) {
    @Volatile
    private var glowParameters = initialGlowParameters

    @Volatile
    private var currentState: HighlightState? = null

    fun emit(
        target: HighlightTarget,
        timing: HighlightTiming? = null,
    ) {
        currentState = HighlightState(
            sentenceIndex = target.sentenceIndex,
            clauseTag = target.clauseTag,
            glowParameters = glowParameters,
            startedAtMillis = timing?.startedAtMillis,
            durationSeconds = timing?.durationSeconds,
            expectedEndMillis = timing?.expectedEndMillis,
        )
    }

    fun currentState(): HighlightState? = currentState

    fun updateGlowParameters(parameters: HighlightGlowParameters) {
        glowParameters = parameters
    }
}
