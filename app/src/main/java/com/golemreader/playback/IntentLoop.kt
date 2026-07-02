package com.golemreader.playback

import com.golemreader.text.SentenceIndex

enum class PlayState {
    Playing,
    Paused,
    Stopped,
}

data class PlaybackIntent(
    val desiredSentenceIndex: SentenceIndex?,
    val desiredPlayState: PlayState,
    val hasTargetChange: Boolean,
)

class IntentLoop(
    private val debounceMillis: Long,
) {
    private var desiredPlayState: PlayState = PlayState.Playing
    private var pendingTarget: SentenceIndex? = null
    private var pendingTargetUpdatedAtMillis: Long = Long.MIN_VALUE

    fun seekTo(
        sentenceIndex: SentenceIndex,
        nowMillis: Long,
    ) {
        desiredPlayState = PlayState.Playing
        pendingTarget = sentenceIndex
        pendingTargetUpdatedAtMillis = nowMillis
    }

    fun pause() {
        desiredPlayState = PlayState.Paused
    }

    fun resume() {
        desiredPlayState = PlayState.Playing
    }

    fun stop() {
        desiredPlayState = PlayState.Stopped
    }

    fun consumeReadyIntent(nowMillis: Long): PlaybackIntent {
        val target = pendingTarget
        val targetReady = target != null && nowMillis - pendingTargetUpdatedAtMillis > debounceMillis
        if (targetReady) {
            pendingTarget = null
            return PlaybackIntent(
                desiredSentenceIndex = target,
                desiredPlayState = desiredPlayState,
                hasTargetChange = true,
            )
        }

        return PlaybackIntent(
            desiredSentenceIndex = null,
            desiredPlayState = desiredPlayState,
            hasTargetChange = false,
        )
    }
}
