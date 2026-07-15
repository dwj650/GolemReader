package com.golemreader.ui

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.view.WindowInsetsCompat
import com.golemreader.MainActivity
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class S18InsetIconTransportDeviceTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun transportButtonsExposeAccessibleNames() {
        listOf("Play", "Pause", "Resume", "Stop").forEach { name ->
            compose.onNodeWithContentDescription(name).fetchSemanticsNode()
        }
    }

    @Test
    fun allFourSurfacesRespectTheStatusBarInset() {
        assertBelowStatusBar(compose.onNodeWithText("tom-sawyer"))
        assertBelowStatusBar(compose.onNodeWithTag("bottom-nav-nowplaying"))

        compose.onNodeWithTag("reading-preview").performClick()
        assertBelowStatusBar(compose.onNodeWithTag("reading-back"))

        compose.onNodeWithTag("reading-back").performClick()
        compose.onNodeWithTag("bottom-nav-settings").performClick()
        assertBelowStatusBar(compose.onNodeWithTag("settings-title"))
    }

    private fun assertBelowStatusBar(node: SemanticsNodeInteraction) {
        val bounds: Rect = node.fetchSemanticsNode().boundsInRoot
        val statusBarBottom = WindowInsetsCompat.toWindowInsetsCompat(
            compose.activity.window.decorView.rootWindowInsets,
        ).getInsets(WindowInsetsCompat.Type.statusBars()).top

        assertTrue(
            "Node top ${bounds.top}px must be at or below status-bar bottom ${statusBarBottom}px",
            bounds.top >= statusBarBottom,
        )
    }
}
