package com.golemreader.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.golemreader.MainActivity
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsNavigationDeviceTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNavigationSettingsAndReadingBackFlowAreLive() {
        compose.onNodeWithTag("bottom-nav-nowplaying").assertIsDisplayed()
        compose.onNodeWithTag("bottom-nav-settings").assertIsDisplayed().performClick()

        compose.onNodeWithTag("settings-title").assertIsDisplayed()
        compose.onNodeWithTag("settings-group-appearance").assertIsDisplayed()
        compose.onNodeWithText("Theme").assertIsDisplayed()
        assertTrue(compose.onAllNodesWithText("Speed").fetchSemanticsNodes().isEmpty())

        compose.onNodeWithTag("bottom-nav-nowplaying").performClick()
        compose.onNodeWithTag("reading-preview").assertIsDisplayed().performClick()
        compose.onNodeWithTag("reading-back").assertIsDisplayed().performClick()
        compose.onNodeWithTag("reading-preview").assertIsDisplayed()
    }

    @Test
    fun selectingDarkThemeRethemesLive() {
        compose.onNodeWithTag("bottom-nav-settings").performClick()

        compose.onNodeWithText("Dark").performClick().assertIsSelected()
    }
}
