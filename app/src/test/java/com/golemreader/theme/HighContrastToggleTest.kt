package com.golemreader.theme

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class HighContrastToggleTest {
    @Test
    fun switchExposesItsVisibleSettingLabelToAccessibilityServices() {
        val source = File("src/main/java/com/golemreader/theme/HighContrastToggle.kt").readText()

        assertTrue(source.contains("contentDescription = \"High contrast\""))
    }
}
