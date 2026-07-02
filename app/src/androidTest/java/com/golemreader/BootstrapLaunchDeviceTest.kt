package com.golemreader

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BootstrapLaunchDeviceTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchedAppShowsRealBookTextAndPlayAdvancesHighlight() {
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithTag("reading-row").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onAllNodesWithTag("reading-row")[0].assertIsDisplayed()

        compose.onNodeWithText("Now Playing").performClick()
        compose.onNodeWithText("Play").performClick()
        compose.onNodeWithText("Reading").performClick()

        compose.waitUntil(timeoutMillis = 30_000) {
            compose.onAllNodesWithTag("reading-highlight").fetchSemanticsNodes().isNotEmpty()
        }
        assertTrue(
            "Real app playback did not advance far enough to emit a visible highlight.",
            compose.onAllNodesWithTag("reading-highlight").fetchSemanticsNodes().isNotEmpty(),
        )
    }
}
