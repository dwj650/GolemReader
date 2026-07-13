package com.golemreader.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

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
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = tokens.colors.surfaceRaised,
                shape = RoundedCornerShape(tokens.shapes.panel),
            )
            .padding(tokens.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(tokens.spacing.xs),
    ) {
        themeChoiceOptions().forEach { option ->
            val isSelected = option.choice == selected
            val segmentShape = RoundedCornerShape(tokens.shapes.control)
            Text(
                text = option.label,
                style = tokens.typography.control,
                color = if (isSelected) tokens.colors.textPrimary else tokens.colors.textSecondary,
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (isSelected) tokens.colors.surface else tokens.colors.surfaceRaised,
                        shape = segmentShape,
                    )
                    .then(
                        if (isSelected) {
                            Modifier.border(tokens.spacing.xxs, tokens.colors.accent, segmentShape)
                        } else {
                            Modifier
                        },
                    )
                    .selectable(
                        selected = isSelected,
                        onClick = { onSelected(option.choice) },
                        role = Role.RadioButton,
                    )
                    .padding(vertical = tokens.spacing.sm),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
