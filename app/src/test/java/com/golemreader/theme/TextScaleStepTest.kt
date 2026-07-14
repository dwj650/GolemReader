package com.golemreader.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class TextScaleStepTest {
    @Test
    fun everyStoredStepResolvesToItsApprovedMultiplier() {
        val expected = mapOf(
            "0.85" to 0.85f,
            "1.0" to 1.0f,
            "1.15" to 1.15f,
            "1.3" to 1.3f,
            "1.5" to 1.5f,
        )

        assertEquals(expected, TextScaleStep.entries.associate { it.storedValue to it.multiplier })
        expected.forEach { (storedValue, multiplier) ->
            assertEquals(multiplier, TextScaleStep.fromStoredValue(storedValue).multiplier)
        }
    }

    @Test
    fun unknownOrMissingStoredValueDefaultsToOneHundredPercent() {
        assertEquals(TextScaleStep.Default, TextScaleStep.fromStoredValue("unexpected"))
        assertEquals(TextScaleStep.Default, TextScaleStep.fromStoredValue(null))
        assertEquals(1.0f, TextScaleStep.Default.multiplier)
    }

    @Test
    fun providerSeamCombinesSystemAndInAppFontScaleMultiplicatively() {
        assertEquals(3.0f, combinedFontScale(systemFontScale = 2.0f, TextScaleStep.Maximum), 0.0001f)
    }

    @Test
    fun stepNavigationStopsAtBothEnds() {
        assertEquals(TextScaleStep.Smallest, TextScaleStep.Smallest.previous())
        assertEquals(TextScaleStep.Default, TextScaleStep.Smallest.next())
        assertEquals(TextScaleStep.Larger, TextScaleStep.Maximum.previous())
        assertEquals(TextScaleStep.Maximum, TextScaleStep.Maximum.next())
    }
}
