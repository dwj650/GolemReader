package com.golemreader.playback

import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord

class ChapterContinuity(
    chapters: List<List<SentenceRecord>>,
) {
    private val sentences = chapters.flatten()
    private val positions = sentences
        .mapIndexed { position, sentence -> sentence.index to position }
        .toMap()

    fun nextAfter(index: SentenceIndex): SentenceRecord? {
        val position = positions[index] ?: return null
        return sentences.getOrNull(position + 1)
    }

    fun isEndOfBook(index: SentenceIndex): Boolean =
        sentences.lastOrNull()?.index == index

    fun orderedSentences(): List<SentenceRecord> =
        sentences.toList()
}
