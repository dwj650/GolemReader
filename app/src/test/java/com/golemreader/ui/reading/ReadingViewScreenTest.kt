package com.golemreader.ui.reading

import com.golemreader.highlight.HighlightIndexMapper
import com.golemreader.highlight.HighlightStateEmitter
import com.golemreader.text.ClauseTag
import com.golemreader.text.SegmentType
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingViewScreenTest {
    @Test
    fun sentenceRowsUseDisplayTextWithoutReplacingQuotesOrPunctuation() {
        val rows = readingRows(
            sentences = listOf(
                sentence(
                    ordinal = 0,
                    display = "\"Hello, Tom!\" she said.",
                    spoken = "Hello Tom she said",
                ),
            ),
            highlightState = null,
        )

        assertEquals("\"Hello, Tom!\" she said.", rows.single().text)
    }

    @Test
    fun highlightedRowUsesSharedSentenceIndexFromEmitterState() {
        val sentences = listOf(sentence(0), sentence(1), sentence(2))
        val emitter = HighlightStateEmitter()
        val target = requireNotNull(HighlightIndexMapper(sentences).targetFor(index(1)))

        emitter.emit(target)

        val rows = readingRows(sentences, emitter.currentState())

        assertFalse(rows[0].isHighlighted)
        assertTrue(rows[1].isHighlighted)
        assertFalse(rows[2].isHighlighted)
        assertEquals(index(1), highlightedSentenceIndex(rows))
    }

    private fun sentence(
        ordinal: Int,
        display: String = "display $ordinal",
        spoken: String = "spoken $ordinal",
    ) = SentenceRecord(
        index = index(ordinal),
        display = display,
        spoken = spoken,
        segmentType = SegmentType.SentenceTerminal,
        clauseTag = ClauseTag(parentSentenceOrdinal = ordinal, clauseOrdinal = 0),
    )

    private fun index(ordinal: Int) =
        SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = ordinal)
}
