package com.golemreader.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun TextScaleStepper(
    step: TextScaleStep,
    onStepChanged: (TextScaleStep) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GolemTheme.tokens
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = { onStepChanged(step.previous()) },
            enabled = step != TextScaleStep.Smallest,
            modifier = Modifier.semantics {
                contentDescription = "Decrease text size"
            },
        ) {
            Text(text = "A−", style = tokens.typography.control)
        }
        Text(
            text = "${step.percentage}%",
            style = tokens.typography.control,
            color = tokens.colors.textPrimary,
        )
        Button(
            onClick = { onStepChanged(step.next()) },
            enabled = step != TextScaleStep.Maximum,
            modifier = Modifier.semantics {
                contentDescription = "Increase text size"
            },
        ) {
            Text(text = "A+", style = tokens.typography.control)
        }
    }
}
