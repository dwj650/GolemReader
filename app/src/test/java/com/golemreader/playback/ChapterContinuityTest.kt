package com.golemreader.playback

import com.golemreader.text.ClauseTag
import com.golemreader.text.SegmentType
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChapterContinuityTest {
    @Test
    fun chapterEndAdvancesToNextChapterWithoutResettingCompositeIndex() {
        val continuity = ChapterContinuity(
            chapters = listOf(
                listOf(sentence(chapter = 5, sentence = 0), sentence(chapter = 5, sentence = 1)),
                listOf(sentence(chapter = 6, sentence = 0)),
            ),
        )

        val next = continuity.nextAfter(SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = 1))

        assertEquals(SentenceIndex(bookHash = "book", chapterOrdinal = 6, sentenceOrdinal = 0), next?.index)
    }

    @Test
    fun endOfBookStopsWithoutPhantomSentence() {
        val continuity = ChapterContinuity(chapters = listOf(listOf(sentence(chapter = 5, sentence = 0))))

        assertEquals(null, continuity.nextAfter(SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = 0)))
        assertTrue(continuity.isEndOfBook(SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = 0)))
        assertFalse(continuity.isEndOfBook(SentenceIndex(bookHash = "book", chapterOrdinal = 4, sentenceOrdinal = 99)))
    }

    private fun sentence(chapter: Int, sentence: Int) = SentenceRecord(
        index = SentenceIndex(bookHash = "book", chapterOrdinal = chapter, sentenceOrdinal = sentence),
        display = "display $chapter:$sentence",
        spoken = "spoken $chapter:$sentence",
        segmentType = SegmentType.SentenceTerminal,
        clauseTag = ClauseTag(parentSentenceOrdinal = sentence, clauseOrdinal = 0),
    )
}
