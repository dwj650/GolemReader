package com.golemreader.theme

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class TextScaleStepperTest {
    @Test
    fun stepperShowsPercentageAndLabelsBothButtonsForAccessibility() {
        val file = File("src/main/java/com/golemreader/theme/TextScaleStepper.kt")
        val source = if (file.exists()) file.readText() else ""

        assertTrue(source.contains("${'$'}{step.percentage}%"))
        assertTrue(source.contains("contentDescription = \"Decrease text size\""))
        assertTrue(source.contains("contentDescription = \"Increase text size\""))
        assertTrue(source.contains("enabled = step != TextScaleStep.Smallest"))
        assertTrue(source.contains("enabled = step != TextScaleStep.Maximum"))
    }
}
