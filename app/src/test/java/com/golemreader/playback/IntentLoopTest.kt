package com.golemreader.playback

import com.golemreader.text.SentenceIndex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IntentLoopTest {
    @Test
    fun rapidPositionChangesDebounceToLatestTarget() {
        val loop = IntentLoop(debounceMillis = 200)
        val first = index(chapter = 5, sentence = 10)
        val second = index(chapter = 5, sentence = 11)
        val final = index(chapter = 6, sentence = 0)

        loop.seekTo(first, nowMillis = 1_000)
        loop.seekTo(second, nowMillis = 1_050)
        loop.seekTo(final, nowMillis = 1_100)

        assertFalse(loop.consumeReadyIntent(nowMillis = 1_250).hasTargetChange)

        val intent = loop.consumeReadyIntent(nowMillis = 1_301)

        assertTrue(intent.hasTargetChange)
        assertEquals(final, intent.desiredSentenceIndex)
        assertEquals(PlayState.Playing, intent.desiredPlayState)
    }

    @Test
    fun pauseAndResumeChangePlayStateWithoutForcingRerender() {
        val loop = IntentLoop(debounceMillis = 200)

        loop.pause()
        val paused = loop.consumeReadyIntent(nowMillis = 0)
        loop.resume()
        val resumed = loop.consumeReadyIntent(nowMillis = 0)

        assertFalse(paused.hasTargetChange)
        assertEquals(PlayState.Paused, paused.desiredPlayState)
        assertFalse(resumed.hasTargetChange)
        assertEquals(PlayState.Playing, resumed.desiredPlayState)
    }

    private fun index(chapter: Int, sentence: Int) =
        SentenceIndex(bookHash = "book", chapterOrdinal = chapter, sentenceOrdinal = sentence)
}
