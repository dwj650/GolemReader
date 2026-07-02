package com.golemreader.highlight

import com.golemreader.text.ClauseTag
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord

data class HighlightTarget(
    val sentenceIndex: SentenceIndex,
    val clauseTag: ClauseTag,
)

class HighlightIndexMapper(
    sentenceRecords: List<SentenceRecord>,
) {
    private val recordsByIndex = sentenceRecords.associateBy { it.index }

    fun targetFor(index: SentenceIndex): HighlightTarget? =
        recordsByIndex[index]?.let { record ->
            HighlightTarget(
                sentenceIndex = record.index,
                clauseTag = record.clauseTag,
            )
        }
}
