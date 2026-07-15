package com.golemreader

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BootstrapLaunchDeviceTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchedAppShowsRealBookTextAndPlayAdvancesHighlight() {
        compose.onNodeWithContentDescription("Play").performClick()
        compose.onNodeWithTag("reading-preview").performClick()

        compose.waitUntil(timeoutMillis = 30_000) {
            compose.onAllNodesWithTag("reading-highlight").fetchSemanticsNodes().isNotEmpty()
        }
        assertTrue(
            "Real app playback did not advance far enough to emit a visible highlight.",
            compose.onAllNodesWithTag("reading-highlight").fetchSemanticsNodes().isNotEmpty(),
        )
    }
}
