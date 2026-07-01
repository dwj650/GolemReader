package com.golemreader.audio

import com.golemreader.text.ClauseTag
import com.golemreader.text.SegmentType
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

class TerminalCueHygieneTest {
    @Test
    fun sentenceTerminalGetsSpokenCleanupWithoutChangingDisplayText() {
        val record = sentenceRecord(
            display = "\"Tom !\"",
            spoken = "\"Tom !!!\"",
            segmentType = SegmentType.SentenceTerminal,
        )

        val cleaned = TerminalCueHygiene.clean(record)

        assertEquals("\"Tom !\"", cleaned.display)
        assertEquals("Tom!", cleaned.spoken)
        assertNotSame(record, cleaned)
    }

    @Test
    fun bareSentenceTerminalGetsPeriodButClauseSubSplitDoesNot() {
        val sentence = sentenceRecord(
            display = "Tom waited",
            spoken = "Tom waited",
            segmentType = SegmentType.SentenceTerminal,
        )
        val clause = sentenceRecord(
            display = "Tom waited",
            spoken = "Tom waited",
            segmentType = SegmentType.ClauseSubSplit,
        )

        assertEquals("Tom waited.", TerminalCueHygiene.clean(sentence).spoken)
        assertEquals("Tom waited", TerminalCueHygiene.clean(clause).spoken)
    }

    @Test
    fun protectsEllipsisAndDashBeforeCollapsingRepeatedTerminals() {
        assertEquals("He waited...", TerminalCueHygiene.clean(sentenceRecord(spoken = "He waited...")).spoken)
        assertEquals("He waited!", TerminalCueHygiene.clean(sentenceRecord(spoken = "He waited!!!")).spoken)
        assertEquals("He waited\u2026", TerminalCueHygiene.clean(sentenceRecord(spoken = "He waited\u2026")).spoken)
        assertEquals("He waited\u2026", TerminalCueHygiene.clean(sentenceRecord(spoken = "He waited\u2026\u2026")).spoken)
        assertEquals("He waited\u2014", TerminalCueHygiene.clean(sentenceRecord(spoken = "He waited\u2014")).spoken)
        assertEquals("He waited\u2014", TerminalCueHygiene.clean(sentenceRecord(spoken = "He waited\u2014\u2014")).spoken)
        assertEquals("He waited?!", TerminalCueHygiene.clean(sentenceRecord(spoken = "He waited?!")).spoken)
    }

    @Test
    fun bareTerminalDefinitionIgnoresTrailingSpokenClosers() {
        val cleaned = TerminalCueHygiene.clean(sentenceRecord(spoken = "Tom stopped)\""))

        assertEquals("Tom stopped).", cleaned.spoken)
        assertFalse(cleaned.spoken.contains("\""))
    }

    @Test
    fun engineBlindCoreDoesNotMentionVoiceNames() {
        val source = javaClass.classLoader
            ?.getResource("../../../main/java/com/golemreader/audio/TerminalCueHygiene.kt")
            ?.readText()
            .orEmpty()

        assertFalse(source.contains("Kokoro", ignoreCase = true))
        assertFalse(source.contains("Piper", ignoreCase = true))
    }

    private fun sentenceRecord(
        spoken: String,
        display: String = spoken,
        segmentType: SegmentType = SegmentType.SentenceTerminal,
    ) = SentenceRecord(
        index = SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = 0),
        display = display,
        spoken = spoken,
        segmentType = segmentType,
        clauseTag = ClauseTag(parentSentenceOrdinal = 0, clauseOrdinal = 0),
    )
}
