package com.golemreader.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun ReducedMotionToggle(
    enabled: Boolean,
    onToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GolemTheme.tokens
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (enabled) "On" else "Off",
            style = tokens.typography.control,
            color = tokens.colors.textSecondary,
        )
        Switch(
            checked = enabled,
            onCheckedChange = onToggled,
            modifier = Modifier
                .golemFocusRing()
                .testTag("reduced-motion-toggle")
                .semantics { contentDescription = "Reduce motion" },
        )
    }
}
