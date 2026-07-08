package com.golemreader.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class ThemeChoiceOption(
    val choice: ThemeChoice,
    val label: String,
)

fun themeChoiceOptions(): List<ThemeChoiceOption> =
    listOf(
        ThemeChoiceOption(ThemeChoice.FollowSystem, "System"),
        ThemeChoiceOption(ThemeChoice.Light, "Light"),
        ThemeChoiceOption(ThemeChoice.Dark, "Dark"),
    )

@Composable
fun ThemeChoicePicker(
    selected: ThemeChoice,
    onSelected: (ThemeChoice) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GolemTheme.tokens
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
    ) {
        themeChoiceOptions().forEach { option ->
            val content: @Composable RowScope.() -> Unit = {
                Text(option.label, style = tokens.typography.control)
            }
            if (option.choice == selected) {
                Button(onClick = { onSelected(option.choice) }, content = content)
            } else {
                OutlinedButton(onClick = { onSelected(option.choice) }, content = content)
            }
        }
    }
}
