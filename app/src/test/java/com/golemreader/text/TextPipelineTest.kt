package com.golemreader.text

import com.golemreader.identity.BookIdentityHasher
import com.golemreader.identity.EpubStructuralReader
import com.golemreader.storage.StorageTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TextPipelineTest {
    private val pipeline = TextPipeline(
        extractor = EpubTextExtractor(EpubStructuralReader()),
        preCleanStage = PreCleanStage(),
        segmenter = SentenceSegmenter(maxSegmentCharacters = 800),
    )

    @Test
    fun tomSawyerChapterOneRunsEndToEndWithSharedCompositeIndexes() {
        val book = TextFixture.file("tom-sawyer.epub")
        val bookHash = BookIdentityHasher().hash(book)

        val result = pipeline.processChapter(book, bookHash, chapterOrdinal = 5)

        assertEquals(StorageTier.Rebuildable, result.storageTier)
        assertTrue(result.ruleTrace.map { it.slot }.containsAll(listOf(RuleSlot.SharedPreFork, RuleSlot.DisplayBranch, RuleSlot.SpokenBranch)))
        assertTrue(result.sentences.size > 120)

        val firstDialogue = result.sentences.first { it.display == "\"Tom!\"" }
        assertEquals(SentenceIndex(bookHash = bookHash, chapterOrdinal = 5, sentenceOrdinal = 0), firstDialogue.index)
        assertEquals("Tom!", firstDialogue.spoken)
        assertEquals(firstDialogue.index, result.sentences[0].index)

        result.sentences.forEachIndexed { ordinal, sentence ->
            assertEquals(bookHash, sentence.index.bookHash)
            assertEquals(5, sentence.index.chapterOrdinal)
            assertEquals(ordinal, sentence.index.sentenceOrdinal)
            assertFalse(sentence.spoken.contains("“"))
            assertFalse(sentence.spoken.contains("”"))
        }
    }

    @Test
    fun pipelineRunsWithEmptyRuleSlotsAndF029Absent() {
        val book = TextFixture.file("tom-sawyer.epub")
        val bookHash = BookIdentityHasher().hash(book)

        val result = pipeline.processChapter(book, bookHash, chapterOrdinal = 5)

        assertEquals(
            listOf(
                RuleApplication(RuleSlot.SharedPreFork, changed = false),
                RuleApplication(RuleSlot.DisplayBranch, changed = false),
                RuleApplication(RuleSlot.SpokenBranch, changed = false),
            ),
            result.ruleTrace.distinct(),
        )
        assertTrue(result.sentences.all { it.spoken.isNotBlank() })
    }
}
