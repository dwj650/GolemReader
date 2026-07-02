package com.golemreader.playback

import com.golemreader.text.SentenceIndex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StarvationStateTest {
    @Test
    fun holdExposesBufferingAndKeepsLatestIntentLive() {
        val starvation = StarvationState()
        val skippedDuringHold = SentenceIndex(bookHash = "book", chapterOrdinal = 7, sentenceOrdinal = 3)

        starvation.onConsumerOutrunsProducer()
        starvation.recordIntentDuringHold(skippedDuringHold)

        assertTrue(starvation.isBuffering)
        assertEquals(skippedDuringHold, starvation.latestIntentDuringHold)

        starvation.onAudioRefilled()

        assertFalse(starvation.isBuffering)
        assertEquals(skippedDuringHold, starvation.latestIntentDuringHold)
    }
}
