package com.golemreader.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.golemreader.AppInfo
import com.golemreader.highlight.HighlightStateEmitter
import com.golemreader.playback.StarvationState
import com.golemreader.text.SentenceRecord
import com.golemreader.transport.TransportCommands
import com.golemreader.ui.nowplaying.NowPlayingScreen
import com.golemreader.ui.nowplaying.NowPlayingTransportControls
import com.golemreader.ui.reading.ReadingViewScreen

@Composable
fun GolemReaderApp(
    bookTitle: String = AppInfo.name,
    sentences: List<SentenceRecord> = emptyList(),
    highlightEmitter: HighlightStateEmitter = remember { HighlightStateEmitter() },
    starvationState: StarvationState = remember { StarvationState() },
    transportControls: NowPlayingTransportControls = remember {
        NowPlayingTransportControls(TransportCommands())
    },
) {
    var screen by remember { mutableStateOf(GolemScreen.Reading) }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ScreenSwitch(
                    selected = screen,
                    onSelected = { screen = it },
                )
                when (screen) {
                    GolemScreen.Reading -> ReadingViewScreen(
                        bookTitle = bookTitle,
                        sentences = sentences,
                        highlightEmitter = highlightEmitter,
                        modifier = Modifier.weight(1f),
                    )
                    GolemScreen.NowPlaying -> NowPlayingScreen(
                        bookTitle = bookTitle,
                        highlightEmitter = highlightEmitter,
                        starvationState = starvationState,
                        controls = transportControls,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private enum class GolemScreen {
    Reading,
    NowPlaying,
}

@Composable
private fun ScreenSwitch(
    selected: GolemScreen,
    onSelected: (GolemScreen) -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = { onSelected(GolemScreen.Reading) },
            enabled = selected != GolemScreen.Reading,
        ) {
            Text("Reading")
        }
        Button(
            onClick = { onSelected(GolemScreen.NowPlaying) },
            enabled = selected != GolemScreen.NowPlaying,
        ) {
            Text("Now Playing")
        }
    }
}
