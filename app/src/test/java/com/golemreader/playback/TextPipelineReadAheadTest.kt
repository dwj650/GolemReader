package com.golemreader.playback

import com.golemreader.identity.BookIdentityHasher
import com.golemreader.identity.EpubStructuralReader
import com.golemreader.text.EpubTextExtractor
import com.golemreader.text.PreCleanStage
import com.golemreader.text.SentenceSegmenter
import com.golemreader.text.TextFixture
import com.golemreader.text.TextPipeline
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextPipelineReadAheadTest {
    @Test
    fun processChapterWithReadAheadReturnsCurrentAndNextChapterBeforeBoundary() {
        val book = TextFixture.file("tom-sawyer.epub")
        val bookHash = BookIdentityHasher().hash(book)
        val pipeline = TextPipeline(
            extractor = EpubTextExtractor(EpubStructuralReader()),
            preCleanStage = PreCleanStage(),
            segmenter = SentenceSegmenter(maxSegmentCharacters = 800),
        )

        val result = pipeline.processChapterWithReadAhead(
            book = book,
            bookHash = bookHash,
            chapterOrdinal = 5,
            lookAheadChapterCount = 1,
        )

        assertEquals(listOf(5, 6), result.chapters.map { it.chapterOrdinal })
        assertTrue(result.orderedSentences.first().index.chapterOrdinal == 5)
        assertTrue(result.orderedSentences.any { it.index.chapterOrdinal == 6 })
        assertTrue(result.readAheadEvidence.contains("processed chapter 6 ahead of boundary 5"))
    }
}
