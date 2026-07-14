package com.golemreader.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import com.golemreader.playback.PlayState
import com.golemreader.theme.TextScaleStep
import com.golemreader.theme.ThemeChoice
import com.golemreader.transport.TransportCommands
import com.golemreader.transport.TransportHub
import com.golemreader.transport.TransportIntentWriter
import com.golemreader.ui.nowplaying.NowPlayingTransportControls
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class KeyboardNavigationDeviceTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun nowPlayingAndReadingTraverseForwardAndBackwardInDeclaredOrder() {
        compose.setContent { InKeyboardMode { GolemReaderApp() } }

        assertForwardAndBackward(
            "reading-preview",
            "transport-play",
            "transport-pause",
            "transport-resume",
            "transport-stop",
            "bottom-nav-nowplaying",
            "bottom-nav-settings",
        )

        compose.onNodeWithTag("reading-preview").performKeyInput { pressKey(Key.Enter) }
        assertForwardAndBackwardFromFocused(
            "reading-back",
            "bottom-nav-nowplaying",
            "bottom-nav-settings",
        )
    }

    @Test
    fun navTabSwitchPlacesFocusOnTheNewScreensFirstControl() {
        compose.setContent { InKeyboardMode { GolemReaderApp() } }
        compose.onRoot().performKeyInput { pressKey(Key.Tab) }

        press("bottom-nav-settings", Key.Enter)

        compose.onNodeWithTag("theme-followsystem").assertIsFocused()
    }

    @Test
    fun settingsTraversesEveryControlThenBothLiveTabsInDeclaredOrder() {
        compose.setContent { InKeyboardMode { GolemReaderApp() } }
        compose.onNodeWithTag("bottom-nav-settings").performClick()

        assertTrue(compose.onAllNodesWithTag("bottom-nav-library").fetchSemanticsNodes().isEmpty())
        assertForwardAndBackwardFromFocused(
            "theme-followsystem",
            "theme-light",
            "theme-dark",
            "high-contrast-toggle",
            "text-scale-decrease",
            "text-scale-increase",
            "reduced-motion-toggle",
            "bottom-nav-nowplaying",
            "bottom-nav-settings",
        )
    }

    @Test
    fun enterAndSpaceMatchTapForNavigationSettingsAndTransportControls() {
        val theme = mutableStateOf(ThemeChoice.FollowSystem)
        val highContrast = mutableStateOf(false)
        val textScale = mutableStateOf(TextScaleStep.Default)
        val reducedMotion = mutableStateOf(false)
        val writer = RecordingIntentWriter()
        compose.setContent {
            InKeyboardMode {
                GolemReaderApp(
                    themeChoice = theme.value,
                    highContrast = highContrast.value,
                    textScale = textScale.value,
                    inAppReducedMotion = reducedMotion.value,
                    onThemeChoiceSelected = { theme.value = it },
                    onHighContrastToggled = { highContrast.value = it },
                    onTextScaleChanged = { textScale.value = it },
                    onReducedMotionToggled = { reducedMotion.value = it },
                    transportControls = NowPlayingTransportControls(
                        TransportCommands(TransportHub.attach(writer)),
                    ),
                )
            }
        }

        compose.onRoot().performKeyInput { pressKey(Key.Tab) }

        press("transport-play", Key.Enter)
        press("transport-pause", Key.Spacebar)
        press("transport-resume", Key.Enter)
        press("transport-stop", Key.Spacebar)
        compose.runOnIdle {
            assertEquals(
                listOf(PlayState.Playing, PlayState.Paused, PlayState.Playing, PlayState.Stopped),
                writer.playStates,
            )
        }

        press("bottom-nav-settings", Key.Enter)
        press("theme-light", Key.Spacebar)
        press("high-contrast-toggle", Key.Spacebar)
        press("text-scale-decrease", Key.Enter)
        press("reduced-motion-toggle", Key.Enter)
        compose.runOnIdle {
            assertEquals(ThemeChoice.Light, theme.value)
            assertTrue(highContrast.value)
            assertEquals(TextScaleStep.Smallest, textScale.value)
            assertTrue(reducedMotion.value)
        }

        press("bottom-nav-nowplaying", Key.Spacebar)
        press("reading-preview", Key.Enter)
        press("reading-back", Key.Spacebar)
        compose.onNodeWithTag("reading-preview")
    }

    private fun assertForwardAndBackward(vararg tags: String) {
        compose.onRoot().performKeyInput { pressKey(Key.Tab) }
        assertForwardAndBackwardFromFocused(*tags)
    }

    private fun assertForwardAndBackwardFromFocused(vararg tags: String) {
        compose.onNodeWithTag(tags.first()).assertIsFocused()

        tags.toList().zipWithNext().forEach { (current, next) ->
            compose.onNodeWithTag(current).performKeyInput { pressKey(Key.Tab) }
            compose.onNodeWithTag(next).assertIsFocused()
        }
        tags.reversed().zipWithNext().forEach { (current, previous) ->
            compose.onNodeWithTag(current).performKeyInput {
                keyDown(Key.ShiftLeft)
                pressKey(Key.Tab)
                keyUp(Key.ShiftLeft)
            }
            compose.onNodeWithTag(previous).assertIsFocused()
        }
    }

    private fun press(tag: String, key: Key) {
        compose.onNodeWithTag(tag)
            .performSemanticsAction(SemanticsActions.RequestFocus) { it() }
            .assertIsFocused()
            .performKeyInput { pressKey(key) }
        compose.waitForIdle()
    }

    @Composable
    private fun InKeyboardMode(content: @Composable () -> Unit) {
        val inputModeManager = LocalInputModeManager.current
        SideEffect { inputModeManager.requestInputMode(InputMode.Keyboard) }
        content()
    }

    private class RecordingIntentWriter : TransportIntentWriter {
        val playStates = mutableListOf<PlayState>()

        override fun play() { playStates += PlayState.Playing }
        override fun pause() { playStates += PlayState.Paused }
        override fun resume() { playStates += PlayState.Playing }
        override fun stop() { playStates += PlayState.Stopped }
        override fun seekTo(sentenceIndex: com.golemreader.text.SentenceIndex) = Unit
    }
}
