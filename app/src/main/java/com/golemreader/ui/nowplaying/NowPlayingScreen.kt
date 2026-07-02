package com.golemreader.ui.nowplaying

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.golemreader.highlight.HighlightState
import com.golemreader.highlight.HighlightStateEmitter
import com.golemreader.playback.StarvationState
import com.golemreader.transport.TransportCommands
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class NowPlayingTransportControls(
    private val commands: TransportCommands,
) {
    fun play() {
        commands.play()
    }

    fun pause() {
        commands.pause()
    }

    fun resume() {
        commands.resume()
    }

    fun stop() {
        commands.stop()
    }
}

@Composable
fun NowPlayingScreen(
    bookTitle: String,
    highlightEmitter: HighlightStateEmitter,
    starvationState: StarvationState,
    controls: NowPlayingTransportControls,
    modifier: Modifier = Modifier,
    pollingIntervalMillis: Long = 100L,
) {
    var highlightState by remember(highlightEmitter) {
        mutableStateOf(highlightEmitter.currentState())
    }
    var isBuffering by remember(starvationState) {
        mutableStateOf(starvationState.isBuffering)
    }
    LaunchedEffect(highlightEmitter, starvationState, pollingIntervalMillis) {
        while (isActive) {
            highlightState = highlightEmitter.currentState()
            isBuffering = starvationState.isBuffering
            delay(pollingIntervalMillis)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = bookTitle,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = positionText(highlightState),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("now-playing-position"),
        )
        BufferingIndicator(isBuffering = isBuffering)
        TransportButtons(controls)
        ReservedSlot(testTag = "sync-preview-slot")
        ReservedSlot(testTag = "action-row-slot")
    }
}

fun positionText(highlightState: HighlightState?): String =
    highlightState?.sentenceIndex?.let { index ->
        "Chapter ${index.chapterOrdinal}, sentence ${index.sentenceOrdinal + 1}"
    } ?: "Position pending"

@Composable
private fun TransportButtons(controls: NowPlayingTransportControls) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(onClick = controls::play, modifier = Modifier.weight(1f)) {
            Text("Play")
        }
        Button(onClick = controls::pause, modifier = Modifier.weight(1f)) {
            Text("Pause")
        }
        Button(onClick = controls::resume, modifier = Modifier.weight(1f)) {
            Text("Resume")
        }
        Button(onClick = controls::stop, modifier = Modifier.weight(1f)) {
            Text("Stop")
        }
    }
}

@Composable
private fun ReservedSlot(testTag: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag(testTag),
    ) {
        Spacer(modifier = Modifier.fillMaxSize())
    }
}
