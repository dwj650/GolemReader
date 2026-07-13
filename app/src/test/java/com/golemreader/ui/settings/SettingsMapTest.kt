package com.golemreader.ui.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SettingsMapTest {
    @Test
    fun productionMenuYieldsOnlyEntriesWhoseOwningFeaturesAreBuilt() {
        val entries = SettingsMap.visibleEntries()

        assertEquals(listOf(SettingId.Theme), entries.map { it.id })
        assertFalse(entries.any { it.owningFeature in setOf("F-005", "F-006", "F-007") })
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
