package com.golemreader.ui.navigation

enum class GolemDestination {
    NowPlaying,
    Settings,
    Reading,
}

enum class GolemTab(
    val destination: GolemDestination,
    val label: String,
) {
    NowPlaying(GolemDestination.NowPlaying, "Now Playing"),
    Settings(GolemDestination.Settings, "Settings"),
}

fun bottomNavigationTabs(): List<GolemTab> = GolemTab.entries

data class GolemNavigationState(
    val selectedTab: GolemTab = GolemTab.NowPlaying,
    private val readingOpen: Boolean = false,
) {
    val destination: GolemDestination
        get() = if (readingOpen) GolemDestination.Reading else selectedTab.destination

    fun selectTab(tab: GolemTab): GolemNavigationState =
        copy(selectedTab = tab, readingOpen = false)

    fun openReading(): GolemNavigationState =
        copy(selectedTab = GolemTab.NowPlaying, readingOpen = true)

    fun closeReading(): GolemNavigationState =
        copy(selectedTab = GolemTab.NowPlaying, readingOpen = false)

    fun onBack(): GolemNavigationState =
        if (readingOpen) closeReading() else this
}
