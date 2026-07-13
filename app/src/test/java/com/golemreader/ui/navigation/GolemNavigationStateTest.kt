package com.golemreader.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class GolemNavigationStateTest {
    @Test
    fun bottomNavigationExposesNowPlayingAndSettingsOnly() {
        assertEquals(
            listOf(GolemTab.NowPlaying, GolemTab.Settings),
            bottomNavigationTabs(),
        )
        assertFalse(GolemDestination.entries.any { it.name == "Library" })
    }

    @Test
    fun selectingATabShowsThatDestinationAndClosesReading() {
        val state = GolemNavigationState()
            .openReading()
            .selectTab(GolemTab.Settings)

        assertEquals(GolemTab.Settings, state.selectedTab)
        assertEquals(GolemDestination.Settings, state.destination)
    }

    @Test
    fun readingOpensOverNowPlayingAndExplicitCloseReturnsThere() {
        val reading = GolemNavigationState()
            .selectTab(GolemTab.NowPlaying)
            .openReading()

        assertEquals(GolemDestination.Reading, reading.destination)
        assertEquals(GolemDestination.NowPlaying, reading.closeReading().destination)
    }

    @Test
    fun backFromReadingReturnsToNowPlaying() {
        val state = GolemNavigationState().openReading().onBack()

        assertEquals(GolemDestination.NowPlaying, state.destination)
    }
}
