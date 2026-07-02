package com.golemreader.playback

import com.golemreader.text.SentenceIndex

class AbortController(
    private val stopProducer: () -> Unit,
    private val flushBuffer: () -> Unit,
    private val flushAudioSink: () -> Unit,
    private val setTarget: (SentenceIndex) -> Unit,
    private val rerenderSmallFirst: () -> Unit,
    private val onTargetChanged: (SentenceIndex) -> Unit = {},
) {
    fun changeTarget(target: SentenceIndex) {
        onTargetChanged(target)
        stopProducer()
        flushBuffer()
        flushAudioSink()
        setTarget(target)
        rerenderSmallFirst()
    }
}
