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
                                chapterOrdinal = chapterOrdinal,
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
            chapterOrdinal = chapterOrdinal,
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
