package com.golemreader.ui.nowplaying

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.golemreader.theme.GolemTheme

fun bufferingStatusText(isBuffering: Boolean): String? =
    if (isBuffering) "Catching up..." else null

@Composable
fun BufferingIndicator(
    isBuffering: Boolean,
    modifier: Modifier = Modifier,
) {
    bufferingStatusText(isBuffering)?.let { status ->
        val tokens = GolemTheme.tokens
        Text(
            text = status,
            style = tokens.typography.bodyStrong,
            color = tokens.colors.highlight,
            modifier = modifier.testTag("buffering-indicator"),
        )
    }
}
