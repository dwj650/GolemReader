package com.golemreader.theme

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusRingContractTest {
    @Test
    fun sharedRingUsesThemeTokenThreeDpStrokeAndFocusState() {
        val source = File("src/main/java/com/golemreader/theme/FocusRing.kt").readText()

        assertTrue(source.contains("fun Modifier.golemFocusRing()"))
        assertTrue(source.contains("tokens.colors.focusRing"))
        assertTrue(source.contains("3.dp"))
        assertTrue(source.contains("tokens.shapes.control"))
        assertTrue(source.contains("onFocusChanged"))
    }

    @Test
    fun sharedRingContainsNoAnimationApi() {
        val source = File("src/main/java/com/golemreader/theme/FocusRing.kt").readText()

        listOf("animate", "Animatable", "Transition", "transition").forEach { forbidden ->
            assertFalse("FocusRing.kt must not contain ${'$'}forbidden", source.contains(forbidden))
        }
    }

    @Test
    fun everyCurrentControlFileUsesSharedRing() {
        listOf(
            "theme/ThemeChoicePicker.kt",
            "theme/HighContrastToggle.kt",
            "theme/TextScaleStepper.kt",
            "theme/ReducedMotionToggle.kt",
            "ui/navigation/GolemBottomNavigation.kt",
            "ui/nowplaying/NowPlayingScreen.kt",
            "ui/reading/ReadingViewScreen.kt",
        ).forEach { relative ->
            val source = File("src/main/java/com/golemreader/$relative").readText()
            assertTrue("$relative must use golemFocusRing", source.contains("golemFocusRing()"))
        }
    }
}
