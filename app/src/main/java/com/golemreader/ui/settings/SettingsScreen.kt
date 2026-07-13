package com.golemreader.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.golemreader.theme.GolemTheme

@Composable
fun SettingsScreen(
    entries: List<SettingEntry>,
    controlContent: @Composable (SettingEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GolemTheme.tokens
    val sections = settingsSections(entries)
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(tokens.spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(tokens.spacing.lg),
    ) {
        Text(
            text = "Settings",
            style = tokens.typography.screenTitle,
            color = tokens.colors.textPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings-title"),
        )
        sections.forEach { section ->
            SettingsSectionContent(section, controlContent)
        }
    }
}

@Composable
private fun SettingsSectionContent(
    section: SettingsSection,
    controlContent: @Composable (SettingEntry) -> Unit,
) {
    val tokens = GolemTheme.tokens
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(tokens.spacing.md),
    ) {
        Text(
            text = section.label,
            style = tokens.typography.label,
            color = tokens.colors.textSecondary,
            modifier = Modifier.testTag("settings-group-${section.label.lowercase()}"),
        )
        section.entries.forEach { entry ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
            ) {
                Text(
                    text = entry.label,
                    style = tokens.typography.bodyStrong,
                    color = tokens.colors.textPrimary,
                )
                controlContent(entry)
            }
        }
    }
}
