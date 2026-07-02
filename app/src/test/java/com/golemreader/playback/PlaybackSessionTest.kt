package com.golemreader.playback

import com.golemreader.text.SentenceIndex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackSessionTest {
    @Test
    fun runningSessionDrivesProducerAndConsumerWhilePlaying() {
        val clock = ManualClock()
        val producer = RecordingProducer(renderedPerCall = 1)
        val consumer = RecordingConsumer(playResults = ArrayDeque(listOf(true, true)))
        val loop = IntentLoop(debounceMillis = 0)
        val session = PlaybackSession(
            intentLoop = loop,
            producer = producer,
            consumer = consumer,
            abortController = RecordingAbortController(),
            starvationState = StarvationState(),
            initialTarget = index(0),
            nowMillis = clock::now,
            sleepMillis = { clock.advance(it) },
        )

        session.runOneIteration()
        session.runOneIteration()

        assertEquals(listOf(index(0), index(1)), producer.lookAheadTargets)
        assertEquals(2, consumer.playCount)
        assertTrue(session.isRunning)
    }

    @Test
    fun pauseKeepsSessionRunningWithoutPlayingOrFlushing() {
        val producer = RecordingProducer(renderedPerCall = 1)
        val consumer = RecordingConsumer(playResults = ArrayDeque(listOf(true)))
        val loop = IntentLoop(debounceMillis = 0)
        val session = PlaybackSession(
            intentLoop = loop,
            producer = producer,
            consumer = consumer,
            abortController = RecordingAbortController(),
            starvationState = StarvationState(),
            initialTarget = index(0),
        )

        loop.pause()
        session.runOneIteration()

        assertEquals(emptyList<SentenceIndex>(), producer.lookAheadTargets)
        assertEquals(0, consumer.playCount)
        assertEquals(0, consumer.flushCount)
        assertTrue(session.isRunning)
    }

    @Test
    fun resumeContinuesAtSamePositionWithoutAbort() {
        val producer = RecordingProducer(renderedPerCall = 1)
        val consumer = RecordingConsumer(playResults = ArrayDeque(listOf(true, true)))
        val abort = RecordingAbortController()
        val loop = IntentLoop(debounceMillis = 0)
        val session = PlaybackSession(
            intentLoop = loop,
            producer = producer,
            consumer = consumer,
            abortController = abort,
            starvationState = StarvationState(),
            initialTarget = index(0),
        )

        session.runOneIteration()
        loop.pause()
        session.runOneIteration()
        loop.resume()
        session.runOneIteration()

        assertEquals(listOf(index(0), index(1)), producer.lookAheadTargets)
        assertEquals(2, consumer.playCount)
        assertEquals(emptyList<SentenceIndex>(), abort.targets)
    }

    @Test
    fun seekAbortsToTargetAndContinuesFromTarget() {
        val clock = ManualClock()
        val producer = RecordingProducer(renderedPerCall = 1)
        val consumer = RecordingConsumer(playResults = ArrayDeque(listOf(true)))
        val abort = RecordingAbortController()
        val loop = IntentLoop(debounceMillis = 0)
        val session = PlaybackSession(
            intentLoop = loop,
            producer = producer,
            consumer = consumer,
            abortController = abort,
            starvationState = StarvationState(),
            initialTarget = index(0),
            nowMillis = clock::now,
        )

        loop.seekTo(index(4), nowMillis = 10)
        clock.set(11)
        session.runOneIteration()

        assertEquals(listOf(index(4)), abort.targets)
        assertEquals(listOf(index(4)), producer.lookAheadTargets)
    }

    @Test
    fun stopFlushesSinkAndEndsSession() {
        val consumer = RecordingConsumer(playResults = ArrayDeque())
        var bufferFlushCount = 0
        val loop = IntentLoop(debounceMillis = 0)
        val session = PlaybackSession(
            intentLoop = loop,
            producer = RecordingProducer(renderedPerCall = 0),
            consumer = consumer,
            abortController = RecordingAbortController(),
            starvationState = StarvationState(),
            initialTarget = index(0),
            flushBuffer = { bufferFlushCount += 1 },
        )

        loop.stop()
        session.runOneIteration()

        assertEquals(1, bufferFlushCount)
        assertEquals(1, consumer.flushCount)
        assertFalse(session.isRunning)
    }

    private class RecordingProducer(
        private val renderedPerCall: Int,
    ) : PlaybackProducerDriver {
        val lookAheadTargets = mutableListOf<SentenceIndex>()

        override fun renderLookAheadFrom(target: SentenceIndex): Int {
            lookAheadTargets += target
            return renderedPerCall
        }

        override fun stop() = Unit
    }

    private class RecordingConsumer(
        private val playResults: ArrayDeque<Boolean>,
    ) : PlaybackConsumerDriver {
        var playCount = 0
        var flushCount = 0

        override fun playNext(): Boolean {
            playCount += 1
            return playResults.removeFirstOrNull() ?: false
        }

        override fun flushSink() {
            flushCount += 1
        }
    }

    private class RecordingAbortController : AbortDriver {
        val targets = mutableListOf<SentenceIndex>()

        override fun changeTarget(target: SentenceIndex) {
            targets += target
        }
    }

    private class ManualClock {
        private var value = 0L

        fun now(): Long = value

        fun set(nowMillis: Long) {
            value = nowMillis
        }

        fun advance(deltaMillis: Long) {
            value += deltaMillis
        }
    }

    private fun index(sentence: Int) =
        SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = sentence)
}
