package com.golemreader.text

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SentenceSegmenterTest {
    @Test
    fun thinRulesKeepAbbreviationsDecimalsAndEllipsisTogether() {
        val segmenter = SentenceSegmenter(maxSegmentCharacters = 400)
        val text = "A newcomer arrived in St. Petersburg. It cost 3.14 cents... Then Tom laughed."

        val segments = segmenter.segment(text, Locale.US)

        assertEquals(
            listOf(
                "A newcomer arrived in St. Petersburg. ",
                "It cost 3.14 cents... ",
                "Then Tom laughed.",
            ),
            segments.map { it.text },
        )
        assertTrue(segments.all { it.type == SegmentType.SentenceTerminal })
    }

    @Test
    fun reassemblingSegmentsReproducesInputText() {
        val segmenter = SentenceSegmenter(maxSegmentCharacters = 400)
        val text = "No answer. \"Tom!\" She waited."

        val segments = segmenter.segment(text, Locale.US)

        assertEquals(text, segments.joinToString(separator = "") { it.text })
    }

    @Test
    fun overlongSentenceSplitsAtClauseBoundariesWithCompleteTags() {
        val segmenter = SentenceSegmenter(maxSegmentCharacters = 56)
        val text = "Tom waited by the fence, watching the road; Huck listened from the alley, quiet and careful."

        val segments = segmenter.segment(text, Locale.US)

        assertEquals(
            listOf(
                "Tom waited by the fence, watching the road;",
                " Huck listened from the alley, quiet and careful.",
            ),
            segments.map { it.text },
        )
        assertEquals(
            listOf(
                ClauseTag(parentSentenceOrdinal = 0, clauseOrdinal = 0),
                ClauseTag(parentSentenceOrdinal = 0, clauseOrdinal = 1),
            ),
            segments.map { it.clauseTag },
        )
        assertTrue(segments.all { it.type == SegmentType.ClauseSubSplit })
        assertFalse(segments.any { it.type == SegmentType.SentenceTerminal })
    }

    @Test
    fun segmentationIsDeterministicForSameInputAndLocale() {
        val segmenter = SentenceSegmenter(maxSegmentCharacters = 400)
        val text = "Tom did play hookey, and he had a very good time. No answer."

        val first = segmenter.segment(text, Locale.US)
        val second = segmenter.segment(text, Locale.US)

        assertEquals(first, second)
    }
}
