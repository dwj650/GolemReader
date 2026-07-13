package com.golemreader.ui.settings

@JvmInline
value class SettingId(val value: String) {
    companion object {
        val Theme = SettingId("theme")
        val Speed = SettingId("speed")
        val SentencePauses = SettingId("sentence-pauses")
        val Volume = SettingId("volume")
    }
}

data class SettingEntry(
    val id: SettingId,
    val label: String,
    val group: String,
    val owningFeature: String,
    val owningFeatureBuilt: Boolean,
)

data class SettingsSection(
    val label: String,
    val entries: List<SettingEntry>,
)

object SettingsMap {
    private val registeredEntries = listOf(
        SettingEntry(SettingId.Theme, "Theme", "Appearance", "F-065", owningFeatureBuilt = true),
        SettingEntry(SettingId.Speed, "Speed", "Playback", "F-005", owningFeatureBuilt = false),
        SettingEntry(
            SettingId.SentencePauses,
            "Pauses between sentences",
            "Playback",
            "F-006",
            owningFeatureBuilt = false,
        ),
        SettingEntry(SettingId.Volume, "Volume", "Playback", "F-007", owningFeatureBuilt = false),
    )

    fun visibleEntries(entries: List<SettingEntry> = registeredEntries): List<SettingEntry> =
        entries.filter(SettingEntry::owningFeatureBuilt)
}

fun settingsSections(entries: List<SettingEntry>): List<SettingsSection> =
    entries
        .groupBy(SettingEntry::group)
        .map { (group, groupedEntries) -> SettingsSection(group, groupedEntries) }
