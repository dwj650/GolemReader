package com.golemreader.text

import com.golemreader.storage.StoragePlacementRule
import com.golemreader.storage.StoredDataType
import java.io.File
import java.util.Locale

class TextPipeline(
    private val extractor: EpubTextExtractor,
    private val preCleanStage: PreCleanStage = PreCleanStage(),
    private val segmenter: SentenceSegmenter = SentenceSegmenter(),
) {
    fun processChapter(
        book: File,
        bookHash: String,
        chapterOrdinal: Int,
        locale: Locale = Locale.getDefault(),
    ): TextPipelineChapterResult {
        val chapter = extractor.extract(book).chapters.first { it.chapterOrdinal == chapterOrdinal }
        return processChapter(chapter, bookHash, locale)
    }

    fun processChapterWithReadAhead(
        book: File,
        bookHash: String,
        chapterOrdinal: Int,
        lookAheadChapterCount: Int,
        locale: Locale = Locale.getDefault(),
    ): TextPipelineReadAheadResult {
        val parsed = extractor.extract(book)
        val lastOrdinal = chapterOrdinal + lookAheadChapterCount.coerceAtLeast(0)
        val chapters = parsed.chapters
            .filter { it.chapterOrdinal in chapterOrdinal..lastOrdinal }
            .map { chapter -> processChapter(chapter, bookHash, locale) }
        val evidence = chapters
            .drop(1)
            .map { chapter -> "processed chapter ${chapter.chapterOrdinal} ahead of boundary $chapterOrdinal" }

        return TextPipelineReadAheadResult(
            chapters = chapters,
            readAheadEvidence = evidence,
        )
    }

    private fun processChapter(
        chapter: ChapterParseResult,
        bookHash: String,
        locale: Locale,
    ): TextPipelineChapterResult {
        require(!chapter.failed) {
            "Chapter ${chapter.entryName} failed to parse: ${chapter.failureMessage}"
        }

        val ruleTrace = mutableListOf<RuleApplication>()
        val sentences = mutableListOf<SentenceRecord>()
        chapter.tokens
            .filter { it.type == StructuralTokenType.Paragraph }
            .forEach { token ->
                val preCleaned = preCleanStage.clean(token.text)
                val shared = applyRuleSlot(RuleSlot.SharedPreFork, preCleaned, ruleTrace)
                segmenter.segment(shared, locale).forEach { segment ->
                    val display = applyRuleSlot(RuleSlot.DisplayBranch, segment.text.trim(), ruleTrace)
                    val spoken = applyRuleSlot(RuleSlot.SpokenBranch, segment.text.toSpokenRendering(), ruleTrace)
                    if (spoken.isNotBlank()) {
                        sentences += SentenceRecord(
                            index = SentenceIndex(
                                bookHash = bookHash,
                                chapterOrdinal = chapter.chapterOrdinal,
                                sentenceOrdinal = sentences.size,
                            ),
                            display = display,
                            spoken = spoken,
                            segmentType = segment.type,
                            clauseTag = segment.clauseTag,
                        )
                    }
                }
            }

        return TextPipelineChapterResult(
            chapterOrdinal = chapter.chapterOrdinal,
            storageTier = StoragePlacementRule.tierFor(StoredDataType.PipelineCacheEntry),
            sentences = sentences,
            ruleTrace = ruleTrace,
        )
    }

    private fun applyRuleSlot(
        slot: RuleSlot,
        text: String,
        trace: MutableList<RuleApplication>,
    ): String {
        trace += RuleApplication(slot, changed = false)
        return text
    }

    private fun String.toSpokenRendering(): String =
        trim()
            .filterNot { it == '"' || it == '\'' || it == '[' || it == ']' }
            .replace(Regex("\\s+"), " ")
}

data class TextPipelineReadAheadResult(
    val chapters: List<TextPipelineChapterResult>,
    val readAheadEvidence: List<String>,
) {
    val orderedSentences: List<SentenceRecord>
        get() = chapters.flatMap { it.sentences }
}
