package com.golemreader.playback

import com.golemreader.text.SentenceIndex

class AbortController(
    private val stopProducer: () -> Unit,
    private val flushBuffer: () -> Unit,
    private val flushAudioSink: () -> Unit,
    private val setTarget: (SentenceIndex) -> Unit,
    private val rerenderSmallFirst: () -> Unit,
) {
    fun changeTarget(target: SentenceIndex) {
        stopProducer()
        flushBuffer()
        flushAudioSink()
        setTarget(target)
        rerenderSmallFirst()
    }
}
