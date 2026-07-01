package com.golemreader.text

import java.text.Normalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class PreCleanStageTest {
    private val stage = PreCleanStage()

    @Test
    fun normalizesWhitespaceControlsQuotesDashesAndUnicodeComposition() {
        val decomposed = "Cafe\u0301"
        val dirty = "$decomposed\u200B\u202E  “quoted”\nvalue⁠—done"

        val cleaned = stage.clean(dirty)

        assertEquals("Café \"quoted\" value-done", cleaned)
        assertFalse(cleaned.contains('\u200B'))
        assertFalse(cleaned.contains('\u202E'))
        assertEquals(true, Normalizer.isNormalized(cleaned, Normalizer.Form.NFC))
    }
}
