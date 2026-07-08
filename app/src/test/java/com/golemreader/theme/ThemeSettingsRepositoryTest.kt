package com.golemreader.theme

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.golemreader.storage.PreciousDatabase
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ThemeSettingsRepositoryTest {
    @Test
    fun themeChoiceDefaultsToFollowSystem() {
        val database = openDatabase()
        try {
            val repository = ThemeSettingsRepository(database.themeSettingsDao())

            assertEquals(ThemeChoice.FollowSystem, repository.currentChoice())
        } finally {
            database.close()
        }
    }

    @Test
    fun themeChoicePersistsUpdates() {
        val database = openDatabase()
        try {
            val repository = ThemeSettingsRepository(database.themeSettingsDao())

            repository.setChoice(ThemeChoice.Dark)

            assertEquals(ThemeChoice.Dark, repository.currentChoice())
        } finally {
            database.close()
        }
    }

    private fun openDatabase(): PreciousDatabase =
        Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PreciousDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
}
