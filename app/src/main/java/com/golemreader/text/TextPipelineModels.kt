package com.golemreader.text

import com.golemreader.storage.StorageTier

enum class StructuralTokenType {
    ChapterBoundary,
    Heading,
    Paragraph,
}

data class StructuralToken(
    val type: StructuralTokenType,
    val text: String,
)

data class ChapterParseResult(
    val chapterOrdinal: Int,
    val entryName: String,
    val tokens: List<StructuralToken>,
    val failed: Boolean = false,
    val failureMessage: String? = null,
)

data class EpubTextParseResult(
    val chapters: List<ChapterParseResult>,
)

enum class SegmentType {
    SentenceTerminal,
    ClauseSubSplit,
}

data class ClauseTag(
    val parentSentenceOrdinal: Int,
    val clauseOrdinal: Int,
)

data class SentenceSegment(
    val text: String,
    val type: SegmentType,
    val clauseTag: ClauseTag,
)

enum class RuleSlot {
    SharedPreFork,
    DisplayBranch,
    SpokenBranch,
}

data class RuleApplication(
    val slot: RuleSlot,
    val changed: Boolean,
)

data class SentenceRecord(
    val index: SentenceIndex,
    val display: String,
    val spoken: String,
    val segmentType: SegmentType,
    val clauseTag: ClauseTag,
)

data class TextPipelineChapterResult(
    val chapterOrdinal: Int,
    val storageTier: StorageTier,
    val sentences: List<SentenceRecord>,
    val ruleTrace: List<RuleApplication>,
)
