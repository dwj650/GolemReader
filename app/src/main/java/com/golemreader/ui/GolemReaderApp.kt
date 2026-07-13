package com.golemreader.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.golemreader.AppInfo
import com.golemreader.highlight.HighlightStateEmitter
import com.golemreader.playback.StarvationState
import com.golemreader.theme.GolemTheme
import com.golemreader.theme.GolemThemeProvider
import com.golemreader.theme.ThemeChoice
import com.golemreader.theme.ThemeChoicePicker
import com.golemreader.text.SentenceRecord
import com.golemreader.transport.TransportCommands
import com.golemreader.ui.nowplaying.NowPlayingScreen
import com.golemreader.ui.nowplaying.NowPlayingTransportControls
import com.golemreader.ui.navigation.GolemBottomNavigation
import com.golemreader.ui.navigation.GolemDestination
import com.golemreader.ui.navigation.GolemNavigationState
import com.golemreader.ui.reading.ReadingViewScreen
import com.golemreader.ui.settings.SettingId
import com.golemreader.ui.settings.SettingsMap
import com.golemreader.ui.settings.SettingsScreen

@Composable
fun GolemReaderApp(
    bookTitle: String = AppInfo.name,
    sentences: List<SentenceRecord> = emptyList(),
    highlightEmitter: HighlightStateEmitter = remember { HighlightStateEmitter() },
    starvationState: StarvationState = remember { StarvationState() },
    themeChoice: ThemeChoice = ThemeChoice.FollowSystem,
    onThemeChoiceSelected: (ThemeChoice) -> Unit = {},
    transportControls: NowPlayingTransportControls = remember {
        NowPlayingTransportControls(TransportCommands())
    },
) {
    var navigation by remember { mutableStateOf(GolemNavigationState()) }

    BackHandler(enabled = navigation.destination == GolemDestination.Reading) {
        navigation = navigation.onBack()
    }

    GolemThemeProvider(choice = themeChoice) {
        val tokens = GolemTheme.tokens
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = tokens.colors.background,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    when (navigation.destination) {
                        GolemDestination.Reading -> ReadingViewScreen(
                            bookTitle = bookTitle,
                            sentences = sentences,
                            highlightEmitter = highlightEmitter,
                            onBack = { navigation = navigation.closeReading() },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(tokens.spacing.screenPadding),
                        )
                        GolemDestination.NowPlaying -> NowPlayingScreen(
                            bookTitle = bookTitle,
                            highlightEmitter = highlightEmitter,
                            starvationState = starvationState,
                            controls = transportControls,
                            sentences = sentences,
                            onOpenReading = { navigation = navigation.openReading() },
                        )
                        GolemDestination.Settings -> SettingsScreen(
                            entries = SettingsMap.visibleEntries(),
                            controlContent = { entry ->
                                if (entry.id == SettingId.Theme) {
                                    ThemeChoicePicker(
                                        selected = themeChoice,
                                        onSelected = onThemeChoiceSelected,
                                    )
                                }
                            },
                        )
                    }
                }
                GolemBottomNavigation(
                    selectedTab = navigation.selectedTab,
                    onTabSelected = { tab -> navigation = navigation.selectTab(tab) },
                )
            }
        }
    }
}
