package com.golemreader.text

import java.text.BreakIterator
import java.util.Locale

class SentenceSegmenter(
    private val maxSegmentCharacters: Int = 360,
) {
    fun segment(
        text: String,
        locale: Locale = Locale.getDefault(),
    ): List<SentenceSegment> {
        if (text.isBlank()) return emptyList()

        val rawSentences = sentenceRanges(text, locale)
            .map { range -> text.substring(range.first, range.last + 1) }
            .filter { it.isNotEmpty() }
        val corrected = applyThinCorrections(rawSentences)
        return corrected.flatMapIndexed { sentenceOrdinal, sentence ->
            if (sentence.length > maxSegmentCharacters) {
                splitClauses(sentence, sentenceOrdinal)
            } else {
                listOf(
                    SentenceSegment(
                        text = sentence,
                        type = SegmentType.SentenceTerminal,
                        clauseTag = ClauseTag(sentenceOrdinal, 0),
                    ),
                )
            }
        }
    }

    private fun sentenceRanges(text: String, locale: Locale): List<IntRange> {
        val iterator = BreakIterator.getSentenceInstance(locale)
        iterator.setText(text)
        val ranges = mutableListOf<IntRange>()
        var start = iterator.first()
        var end = iterator.next()
        while (end != BreakIterator.DONE) {
            ranges += start until end
            start = end
            end = iterator.next()
        }
        return ranges
    }

    private fun applyThinCorrections(sentences: List<String>): List<String> {
        val corrected = mutableListOf<String>()
        var index = 0
        while (index < sentences.size) {
            var current = sentences[index]
            while (index + 1 < sentences.size && shouldMerge(current, sentences[index + 1])) {
                current += sentences[index + 1]
                index += 1
            }
            corrected += current
            index += 1
        }
        return corrected
    }

    private fun shouldMerge(current: String, next: String): Boolean {
        val trimmed = current.trimEnd()
        if (ABBREVIATIONS.any { trimmed.endsWith(it) }) return true
        val last = trimmed.takeLast(1)
        val nextFirst = next.trimStart().firstOrNull()
        return last == "." && trimmed.dropLast(1).lastOrNull()?.isDigit() == true && nextFirst?.isDigit() == true
    }

    private fun splitClauses(sentence: String, parentSentenceOrdinal: Int): List<SentenceSegment> {
        val delimiter = if (';' in sentence) ';' else ','
        val pieces = mutableListOf<String>()
        var start = 0
        sentence.forEachIndexed { index, character ->
            if (character == delimiter) {
                val candidate = sentence.substring(start, index + 1)
                if (candidate.isNotBlank()) {
                    pieces += candidate
                    start = index + 1
                }
            }
        }
        if (start < sentence.length) {
            pieces += sentence.substring(start)
        }

        val usefulPieces = pieces.filter { it.isNotBlank() }
        if (usefulPieces.size < 2) {
            return listOf(
                SentenceSegment(
                    text = sentence,
                    type = SegmentType.SentenceTerminal,
                    clauseTag = ClauseTag(parentSentenceOrdinal, 0),
                ),
            )
        }
        return usefulPieces.mapIndexed { clauseOrdinal, clause ->
            SentenceSegment(
                text = clause,
                type = SegmentType.ClauseSubSplit,
                clauseTag = ClauseTag(parentSentenceOrdinal, clauseOrdinal),
            )
        }
    }

    private companion object {
        val ABBREVIATIONS = setOf(
            "Mr.",
            "Mrs.",
            "Ms.",
            "Dr.",
            "Prof.",
            "Rev.",
            "St.",
            "Jr.",
            "Sr.",
            "etc.",
        )
    }
}
