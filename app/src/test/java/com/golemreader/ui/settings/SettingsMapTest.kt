package com.golemreader.ui.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SettingsMapTest {
    @Test
    fun productionMenuYieldsOnlyEntriesWhoseOwningFeaturesAreBuilt() {
        val entries = SettingsMap.visibleEntries()

        assertEquals(listOf(SettingId.Theme, SettingId.HighContrast, SettingId.TextScale), entries.map { it.id })
        assertFalse(entries.any { it.owningFeature in setOf("F-005", "F-006", "F-007") })
    }

    @Test
    fun highContrastRegistersAsBuiltAccessibilitySettingOwnedByF066() {
        val highContrast = SettingsMap.visibleEntries().single { it.id == SettingId.HighContrast }

        assertEquals("High contrast", highContrast.label)
        assertEquals("Accessibility", highContrast.group)
        assertEquals("F-066", highContrast.owningFeature)
        assertEquals(true, highContrast.owningFeatureBuilt)
    }

    @Test
    fun textSizeRegistersAsBuiltAccessibilitySettingOwnedByF068() {
        val textScale = SettingsMap.visibleEntries().single { it.id == SettingId.TextScale }

        assertEquals("Text size", textScale.label)
        assertEquals("Accessibility", textScale.group)
        assertEquals("F-068", textScale.owningFeature)
        assertEquals(true, textScale.owningFeatureBuilt)
    }

    @Test
    fun accessibilityGroupListsHighContrastThenTextSize() {
        val accessibility = settingsSections(SettingsMap.visibleEntries())
            .single { it.label == "Accessibility" }

        assertEquals(
            listOf(SettingId.HighContrast, SettingId.TextScale),
            accessibility.entries.map { it.id },
        )
    }

    @Test
    fun unbuiltEntriesAreAbsentRatherThanDisabledPlaceholders() {
        val entries = listOf(
            SettingEntry(SettingId.Theme, "Theme", "Appearance", "F-065", owningFeatureBuilt = true),
            SettingEntry(SettingId.Speed, "Speed", "Playback", "F-005", owningFeatureBuilt = false),
        )

        assertEquals(listOf(SettingId.Theme), SettingsMap.visibleEntries(entries).map { it.id })
    }

    @Test
    fun fakeRegisteredEntrySurfacesInItsGroupWithoutShellChanges() {
        val fake = SettingEntry(
            id = SettingId("test-only"),
            label = "Test setting",
            group = "Test group",
            owningFeature = "F-TEST",
            owningFeatureBuilt = true,
        )

        val sections = settingsSections(SettingsMap.visibleEntries(listOf(fake)))

        assertEquals(listOf(SettingsSection("Test group", listOf(fake))), sections)
    }
}
