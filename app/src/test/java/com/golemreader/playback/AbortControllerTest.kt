package com.golemreader.playback

import com.golemreader.text.SentenceIndex
import org.junit.Assert.assertEquals
import org.junit.Test

class AbortControllerTest {
    @Test
    fun targetChangeUsesRequiredAbortOrder() {
        val events = mutableListOf<String>()
        val controller = AbortController(
            stopProducer = { events += "stop" },
            flushBuffer = { events += "flush-buffer" },
            flushAudioSink = { events += "flush-sink" },
            setTarget = { events += "set-target:${it.chapterOrdinal}:${it.sentenceOrdinal}" },
            rerenderSmallFirst = { events += "rerender-small-first" },
        )

        controller.changeTarget(SentenceIndex(bookHash = "book", chapterOrdinal = 6, sentenceOrdinal = 0))

        assertEquals(
            listOf(
                "stop",
                "flush-buffer",
                "flush-sink",
                "set-target:6:0",
                "rerender-small-first",
            ),
            events,
        )
    }

    @Test
    fun targetChangeNotifiesHighlightSyncWithoutChangingAbortOrder() {
        val events = mutableListOf<String>()
        val controller = AbortController(
            stopProducer = { events += "stop" },
            flushBuffer = { events += "flush-buffer" },
            flushAudioSink = { events += "flush-sink" },
            setTarget = { events += "set-target:${it.chapterOrdinal}:${it.sentenceOrdinal}" },
            rerenderSmallFirst = { events += "rerender-small-first" },
            onTargetChanged = { events += "highlight:${it.chapterOrdinal}:${it.sentenceOrdinal}" },
        )

        controller.changeTarget(SentenceIndex(bookHash = "book", chapterOrdinal = 6, sentenceOrdinal = 2))

        assertEquals(
            listOf(
                "highlight:6:2",
                "stop",
                "flush-buffer",
                "flush-sink",
                "set-target:6:2",
                "rerender-small-first",
            ),
            events,
        )
    }
}
