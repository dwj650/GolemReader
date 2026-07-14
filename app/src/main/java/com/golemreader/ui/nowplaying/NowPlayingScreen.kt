package com.golemreader.ui.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.shape.RoundedCornerShape
import com.golemreader.highlight.HighlightState
import com.golemreader.highlight.HighlightStateEmitter
import com.golemreader.playback.StarvationState
import com.golemreader.theme.GolemTheme
import com.golemreader.theme.GolemThemeValueSets
import com.golemreader.theme.golemFocusRing
import com.golemreader.text.SentenceRecord
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
    sentences: List<SentenceRecord>,
    onOpenReading: () -> Unit,
    firstControlFocusRequester: FocusRequester? = null,
    modifier: Modifier = Modifier,
    pollingIntervalMillis: Long = GolemThemeValueSets.dark.motion.pollingIntervalMillis,
) {
    val tokens = GolemTheme.tokens
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
            .padding(tokens.spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(tokens.spacing.lg),
    ) {
        Text(
            text = bookTitle,
            style = tokens.typography.screenTitle,
            color = tokens.colors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = positionText(highlightState),
            style = tokens.typography.body,
            color = tokens.colors.textSecondary,
            modifier = Modifier.testTag("now-playing-position"),
        )
        BufferingIndicator(isBuffering = isBuffering)
        ReadingPreviewStrip(
            text = previewSentenceText(sentences, highlightState) ?: "Reading position pending",
            onOpenReading = onOpenReading,
            focusRequester = firstControlFocusRequester,
        )
        TransportButtons(controls)
        ReservedSlot(testTag = "action-row-slot")
    }
}

@Composable
private fun ReadingPreviewStrip(
    text: String,
    onOpenReading: () -> Unit,
    focusRequester: FocusRequester?,
) {
    val tokens = GolemTheme.tokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = tokens.colors.surface,
                shape = RoundedCornerShape(tokens.shapes.panel),
            )
            .then(
                if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier,
            )
            .golemFocusRing()
            .clickable(onClick = onOpenReading)
            .padding(tokens.spacing.md)
            .testTag("reading-preview"),
        verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
    ) {
        Text(
            text = text,
            style = tokens.typography.reading,
            color = tokens.colors.textPrimary,
        )
        Text(
            text = "Tap to open reader",
            style = tokens.typography.label,
            color = tokens.colors.textSecondary,
        )
    }
}

fun positionText(highlightState: HighlightState?): String =
    highlightState?.sentenceIndex?.let { index ->
        "Chapter ${index.chapterOrdinal}, sentence ${index.sentenceOrdinal + 1}"
    } ?: "Position pending"

fun previewSentenceText(
    sentences: List<SentenceRecord>,
    highlightState: HighlightState?,
): String? =
    highlightState?.sentenceIndex?.let { currentIndex ->
        sentences.firstOrNull { it.index == currentIndex }?.display
    }

@Composable
private fun TransportButtons(controls: NowPlayingTransportControls) {
    val tokens = GolemTheme.tokens
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = controls::play,
            modifier = Modifier.weight(1f).golemFocusRing().testTag("transport-play"),
        ) {
            Text("Play", style = tokens.typography.control)
        }
        Button(
            onClick = controls::pause,
            modifier = Modifier.weight(1f).golemFocusRing().testTag("transport-pause"),
        ) {
            Text("Pause", style = tokens.typography.control)
        }
        Button(
            onClick = controls::resume,
            modifier = Modifier.weight(1f).golemFocusRing().testTag("transport-resume"),
        ) {
            Text("Resume", style = tokens.typography.control)
        }
        Button(
            onClick = controls::stop,
            modifier = Modifier.weight(1f).golemFocusRing().testTag("transport-stop"),
        ) {
            Text("Stop", style = tokens.typography.control)
        }
    }
}

@Composable
private fun ReservedSlot(testTag: String) {
    val tokens = GolemTheme.tokens
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(tokens.spacing.reservedSlotHeight)
            .testTag(testTag),
    ) {
        Spacer(modifier = Modifier.fillMaxSize())
    }
}
