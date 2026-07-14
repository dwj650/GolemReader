package com.golemreader.ui.nowplaying

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.golemreader.theme.GolemTheme
import kotlinx.coroutines.delay

fun bufferingStatusText(isBuffering: Boolean): String? =
    if (isBuffering) CATCHING_UP_ANNOUNCEMENT else null

@Composable
fun BufferingIndicator(
    isBuffering: Boolean,
    modifier: Modifier = Modifier,
) {
    val decision = remember { StarvationAnnouncement() }
    var announcement by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(isBuffering) {
        announcement = null
        if (isBuffering) {
            delay(STARVATION_ANNOUNCE_HOLD_MILLIS)
            announcement = decision.update(
                isBuffering = true,
                heldMillis = STARVATION_ANNOUNCE_HOLD_MILLIS,
            )
        } else {
            decision.update(isBuffering = false, heldMillis = 0)
        }
    }
    bufferingStatusText(isBuffering)?.let { status ->
        val tokens = GolemTheme.tokens
        Text(
            text = status,
            style = tokens.typography.bodyStrong,
            color = tokens.colors.highlight,
            modifier = modifier.testTag("buffering-indicator"),
        )
    }
    announcement?.let { spokenText ->
        Text(
            text = "",
            modifier = Modifier
                .testTag("buffering-announcement")
                .semantics {
                    liveRegion = LiveRegionMode.Polite
                    contentDescription = spokenText
                },
        )
    }
}
