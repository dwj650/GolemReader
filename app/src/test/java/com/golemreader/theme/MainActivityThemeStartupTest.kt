package com.golemreader.theme

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Test

class MainActivityThemeStartupTest {
    @Test
    fun mainActivityDoesNotReadThemeChoiceSynchronouslyDuringComposition() {
        val source = File("src/main/java/com/golemreader/MainActivity.kt").readText()

        assertFalse(
            "MainActivity must not call ThemeSettingsRepository.currentChoice() from setContent; Room rejects main-thread reads on device.",
            source.contains("currentChoice()"),
        )
    }
}
