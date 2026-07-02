package com.golemreader.playback

import com.golemreader.text.SentenceIndex
import com.golemreader.transport.TransportIntentWriter

interface PlaybackProducerDriver {
    fun renderLookAheadFrom(target: SentenceIndex): Int
    fun stop()
}

interface PlaybackConsumerDriver {
    fun playNext(): Boolean
    fun flushSink()
}

interface AbortDriver {
    fun changeTarget(target: SentenceIndex)
}

class PlaybackSession(
    private val intentLoop: IntentLoop,
    private val producer: PlaybackProducerDriver,
    private val consumer: PlaybackConsumerDriver,
    private val abortController: AbortDriver,
    private val starvationState: StarvationState,
    initialTarget: SentenceIndex,
    private val flushBuffer: () -> Unit = {},
    private val nextAfter: (SentenceIndex) -> SentenceIndex? = { index ->
        index.copy(sentenceOrdinal = index.sentenceOrdinal + 1)
    },
    private val isEndOfBook: (SentenceIndex) -> Boolean = { false },
    private val tickMillis: Long = 10L,
    private val nowMillis: () -> Long = { System.currentTimeMillis() },
    private val sleepMillis: (Long) -> Unit = { Thread.sleep(it) },
) : TransportIntentWriter {
    @Volatile
    var isRunning: Boolean = true
        private set

    @Volatile
    private var loopThread: Thread? = null

    private var playbackCursor: SentenceIndex = initialTarget
    private var renderCursor: SentenceIndex = initialTarget
    private var hasRenderedEndOfBook: Boolean = false

    fun start() {
        if (loopThread != null) return
        loopThread = Thread(
            {
                while (isRunning) {
                    runOneIteration()
                }
            },
            "GolemPlaybackSession",
        ).also { it.start() }
    }

    fun join(timeoutMillis: Long) {
        loopThread?.join(timeoutMillis)
    }

    fun runOneIteration() {
        if (!isRunning) return

        val intent = intentLoop.consumeReadyIntent(nowMillis())
        if (intent.desiredPlayState == PlayState.Stopped) {
            finishSession()
            return
        }

        if (intent.hasTargetChange) {
            val target = requireNotNull(intent.desiredSentenceIndex)
            abortController.changeTarget(target)
            playbackCursor = target
            renderCursor = target
            hasRenderedEndOfBook = false
        }

        if (intent.desiredPlayState == PlayState.Paused) {
            sleepMillis(tickMillis)
            return
        }

        if (!hasRenderedEndOfBook) {
            val rendered = producer.renderLookAheadFrom(renderCursor)
            repeat(rendered) {
                advanceRenderCursor()
            }
        }

        if (consumer.playNext()) {
            if (isEndOfBook(playbackCursor)) {
                finishSession()
                return
            }
            playbackCursor = nextAfter(playbackCursor) ?: playbackCursor
        }

        if (starvationState.isBuffering) {
            sleepMillis(tickMillis)
        }
    }

    override fun play() {
        intentLoop.resume()
    }

    override fun pause() {
        intentLoop.pause()
    }

    override fun resume() {
        intentLoop.resume()
    }

    override fun stop() {
        intentLoop.stop()
    }

    override fun seekTo(sentenceIndex: SentenceIndex) {
        intentLoop.seekTo(sentenceIndex, nowMillis())
    }

    private fun advanceRenderCursor() {
        if (hasRenderedEndOfBook) return
        if (isEndOfBook(renderCursor)) {
            hasRenderedEndOfBook = true
            return
        }
        renderCursor = nextAfter(renderCursor) ?: renderCursor
    }

    private fun finishSession() {
        producer.stop()
        flushBuffer()
        consumer.flushSink()
        isRunning = false
    }
}

fun PlaybackProducer.asDriver(): PlaybackProducerDriver =
    object : PlaybackProducerDriver {
        override fun renderLookAheadFrom(target: SentenceIndex): Int =
            this@asDriver.renderLookAheadFrom(target)

        override fun stop() {
            this@asDriver.stop()
        }
    }

fun PlaybackConsumer.asDriver(): PlaybackConsumerDriver =
    object : PlaybackConsumerDriver {
        override fun playNext(): Boolean =
            this@asDriver.playNext()

        override fun flushSink() {
            this@asDriver.flushSink()
        }
    }

fun AbortController.asDriver(): AbortDriver =
    object : AbortDriver {
        override fun changeTarget(target: SentenceIndex) {
            this@asDriver.changeTarget(target)
        }
    }
