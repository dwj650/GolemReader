package com.golemreader.transport

import com.golemreader.text.SentenceIndex

interface TransportIntentWriter {
    fun play()
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(sentenceIndex: SentenceIndex)
}

class TransportHub private constructor() {
    @Volatile
    private var writer: TransportIntentWriter? = null

    fun play() {
        requireWriter().play()
    }

    fun pause() {
        requireWriter().pause()
    }

    fun resume() {
        requireWriter().resume()
    }

    fun stop() {
        requireWriter().stop()
    }

    fun seekTo(sentenceIndex: SentenceIndex) {
        requireWriter().seekTo(sentenceIndex)
    }

    private fun attachWriter(writer: TransportIntentWriter): TransportHub {
        this.writer = writer
        return this
    }

    private fun requireWriter(): TransportIntentWriter =
        requireNotNull(writer) { "TransportHub has no attached playback session." }

    companion object {
        private val singleton = TransportHub()

        fun attach(writer: TransportIntentWriter): TransportHub =
            singleton.attachWriter(writer)

        fun instance(): TransportHub = singleton
    }
}
