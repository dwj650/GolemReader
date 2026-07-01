package com.golemreader.audio

import com.golemreader.text.SegmentType
import com.golemreader.text.SentenceRecord

object TerminalCueHygiene {
    private val quoteMarks = setOf('"', '\'', '\u201c', '\u201d', '\u2018', '\u2019')
    private val spokenClosers = setOf('"', '\'', '\u201c', '\u201d', '\u2018', '\u2019', ')', ']', '}')
    private val sentenceTerminals = setOf('.', '!', '?', '\u2026', '\u2014', '-')
    private val collapseCandidates = setOf('.', '!', '?')

    fun clean(record: SentenceRecord): SentenceRecord {
        val cleanedSpoken = cleanSpoken(record.spoken, record.segmentType)
        return record.copy(spoken = cleanedSpoken)
    }

    private fun cleanSpoken(
        text: String,
        segmentType: SegmentType,
    ): String {
        var cleaned = text.trim()
            .filterNot { it in quoteMarks }
            .replace(Regex("\\s+"), " ")
            .replace(Regex("\\s+([.!?\u2026\u2014;:])"), "$1")

        cleaned = protectThenCollapseRuns(cleaned)
        if (segmentType == SegmentType.SentenceTerminal && isBareTerminal(cleaned)) {
            cleaned += "."
        }
        return cleaned
    }

    private fun protectThenCollapseRuns(text: String): String {
        if (text.isEmpty()) return text
        val output = StringBuilder(text.length)
        var index = 0
        while (index < text.length) {
            val current = text[index]
            if (current == '.' && text.startsWith("...", index)) {
                output.append("...")
                index += 3
            } else if (current == '\u2026') {
                output.append(current)
                while (index + 1 < text.length && text[index + 1] == current) {
                    index += 1
                }
                index += 1
            } else if (current == '\u2014') {
                output.append(current)
                while (index + 1 < text.length && text[index + 1] == current) {
                    index += 1
                }
                index += 1
            } else if (current == '-' && text.startsWith("--", index)) {
                output.append("--")
                index += 2
            } else if (current in collapseCandidates) {
                output.append(current)
                while (index + 1 < text.length && text[index + 1] == current) {
                    index += 1
                }
                index += 1
            } else {
                output.append(current)
                index += 1
            }
        }
        return output.toString()
    }

    private fun isBareTerminal(text: String): Boolean {
        val lastContent = text.trimEnd()
            .dropLastWhile { it in spokenClosers || it.isWhitespace() }
            .lastOrNull()
        return lastContent == null || lastContent !in sentenceTerminals
    }
}
