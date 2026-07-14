package com.golemreader.theme

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class TextScalePlumbingTest {
    @Test
    fun appRoutesTextScaleOnlyThroughThemeProviderAndThemeOwnedControl() {
        val source = File("src/main/java/com/golemreader/ui/GolemReaderApp.kt").readText()

        assertTrue(source.contains("textScale: TextScaleStep = TextScaleStep.Default"))
        assertTrue(source.contains("GolemThemeProvider("))
        assertTrue(source.contains("textScale = textScale"))
        assertTrue(source.contains("SettingId.TextScale -> TextScaleStepper("))
    }
}
