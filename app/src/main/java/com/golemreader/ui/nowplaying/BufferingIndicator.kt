package com.golemreader.ui.nowplaying

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

fun bufferingStatusText(isBuffering: Boolean): String? =
    if (isBuffering) "Catching up..." else null

@Composable
fun BufferingIndicator(
    isBuffering: Boolean,
    modifier: Modifier = Modifier,
) {
    bufferingStatusText(isBuffering)?.let { status ->
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = modifier.testTag("buffering-indicator"),
        )
    }
}
