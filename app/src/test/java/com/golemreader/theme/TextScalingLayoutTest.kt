package com.golemreader.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import com.golemreader.highlight.HighlightStateEmitter
import com.golemreader.playback.StarvationState
import com.golemreader.text.ClauseTag
import com.golemreader.text.SegmentType
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import com.golemreader.transport.TransportCommands
import com.golemreader.ui.navigation.GolemBottomNavigation
import com.golemreader.ui.navigation.GolemTab
import com.golemreader.ui.nowplaying.NowPlayingScreen
import com.golemreader.ui.nowplaying.NowPlayingTransportControls
import com.golemreader.ui.reading.ReadingViewScreen
import com.golemreader.ui.settings.SettingEntry
import com.golemreader.ui.settings.SettingId
import com.golemreader.ui.settings.SettingsScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TextScalingLayoutTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun maxCombinedScaleKeepsSettingsTextLaidOut() = assertMaxScaleVisible("settings-title") {
        SettingsScreen(entries = settingsEntries(), controlContent = {})
    }

    @Test
    fun maxCombinedScaleKeepsNowPlayingTextLaidOut() = assertMaxScaleVisible("now-playing-position") {
        NowPlayingScreen(
            bookTitle = "A deliberately long book title that must reflow",
            highlightEmitter = HighlightStateEmitter(),
            starvationState = StarvationState(),
            controls = NowPlayingTransportControls(TransportCommands()),
            sentences = listOf(sentence()),
            onOpenReading = {},
        )
    }

    @Test
    fun maxCombinedScaleKeepsReadingTextLaidOut() = assertMaxScaleVisible("reading-row") {
        ReadingViewScreen(
            bookTitle = "A deliberately long book title that must reflow",
            sentences = listOf(sentence()),
            highlightEmitter = HighlightStateEmitter(),
        )
    }

    @Test
    fun maxCombinedScaleKeepsBottomNavigationLabelsLaidOut() {
        compose.setContent {
            MaxOsDensity {
                GolemThemeProvider(ThemeChoice.Dark, highContrast = false, textScale = TextScaleStep.Maximum) {
                    GolemBottomNavigation(GolemTab.NowPlaying, onTabSelected = {})
                }
            }
        }
        compose.onNodeWithText("Now Playing").assertIsDisplayed()
        compose.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun highContrastTokensAndMaximumTextScaleComposeWithoutInterference() {
        var capturedTokens: GolemThemeValueSet? = null
        var capturedFontScale = 0f
        compose.setContent {
            MaxOsDensity {
                GolemThemeProvider(ThemeChoice.Dark, highContrast = true, textScale = TextScaleStep.Maximum) {
                    val tokens = GolemTheme.tokens
                    val density = LocalDensity.current
                    SideEffect {
                        capturedTokens = tokens
                        capturedFontScale = density.fontScale
                    }
                    Box(Modifier.fillMaxSize())
                }
            }
        }

        compose.runOnIdle {
            assertEquals(GolemThemeValueSets.hcDark, capturedTokens)
            assertEquals(3.0f, capturedFontScale, 0.0001f)
        }
    }

    @Test
    fun raisingInAppStepGrowsTextPixelsForEveryCheckedSurfaceRole() {
        val step = mutableStateOf(TextScaleStep.Default)
        var pixels = emptyMap<String, Float>()
        compose.setContent {
            MaxOsDensity {
                GolemThemeProvider(ThemeChoice.Dark, highContrast = false, textScale = step.value) {
                    val density = LocalDensity.current
                    val typography = GolemTheme.tokens.typography
                    SideEffect {
                        pixels = with(density) {
                            mapOf(
                                "Settings" to typography.screenTitle.fontSize.toPx(),
                                "Now Playing" to typography.body.fontSize.toPx(),
                                "Reading" to typography.reading.fontSize.toPx(),
                                "bottom nav" to typography.label.fontSize.toPx(),
                            )
                        }
                    }
                    Box(Modifier.fillMaxSize())
                }
            }
        }
        lateinit var before: Map<String, Float>
        compose.runOnIdle { before = pixels }
        compose.runOnIdle { step.value = TextScaleStep.Maximum }
        compose.waitForIdle()
        compose.runOnIdle {
            pixels.forEach { (surface, after) ->
                assertTrue("$surface text pixels must grow: ${before.getValue(surface)} -> $after", after > before.getValue(surface))
            }
        }
    }

    @Test
    fun raisingSystemFontScaleGrowsTextWithInAppStepHeldConstant() {
        val systemFontScale = mutableStateOf(1f)
        var pixels = 0f
        compose.setContent {
            CompositionLocalProvider(
                LocalDensity provides Density(density = 3f, fontScale = systemFontScale.value),
            ) {
                GolemThemeProvider(ThemeChoice.Dark, highContrast = false, textScale = TextScaleStep.Large) {
                    val density = LocalDensity.current
                    val fontSize = GolemTheme.tokens.typography.body.fontSize
                    SideEffect { pixels = with(density) { fontSize.toPx() } }
                    Box(Modifier.fillMaxSize())
                }
            }
        }
        var before = 0f
        compose.runOnIdle { before = pixels }
        compose.runOnIdle { systemFontScale.value = 2f }
        compose.waitForIdle()
        compose.runOnIdle { assertTrue("OS font scale must grow rendered text pixels", pixels > before) }
    }

    private fun assertMaxScaleVisible(tag: String, content: @androidx.compose.runtime.Composable () -> Unit) {
        compose.setContent {
            MaxOsDensity {
                GolemThemeProvider(ThemeChoice.Dark, highContrast = false, textScale = TextScaleStep.Maximum) {
                    content()
                }
            }
        }
        compose.onNodeWithTag(tag).assertIsDisplayed()
    }

    @androidx.compose.runtime.Composable
    private fun MaxOsDensity(content: @androidx.compose.runtime.Composable () -> Unit) {
        CompositionLocalProvider(LocalDensity provides Density(density = 3f, fontScale = 2f)) {
            content()
        }
    }

    private fun settingsEntries() = listOf(
        SettingEntry(SettingId.Theme, "Theme", "Appearance", "F-065", true),
        SettingEntry(SettingId.HighContrast, "High contrast", "Accessibility", "F-066", true),
        SettingEntry(SettingId.TextScale, "Text size", "Accessibility", "F-068", true),
    )

    private fun sentence() = SentenceRecord(
        index = SentenceIndex("book", 0, 0),
        display = "A long reading sentence that wraps naturally instead of being truncated or ellipsized.",
        spoken = "A long reading sentence that wraps naturally.",
        segmentType = SegmentType.SentenceTerminal,
        clauseTag = ClauseTag(0, 0),
    )
}
