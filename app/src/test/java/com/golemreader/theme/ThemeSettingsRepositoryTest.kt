package com.golemreader.theme

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.golemreader.storage.PreciousDatabase
import java.util.concurrent.Executors
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
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
    fun themeChoicePersistsUpdates() = runBlocking {
        val database = openDatabase()
        try {
            val repository = ThemeSettingsRepository(database.themeSettingsDao())

            repository.setChoice(ThemeChoice.Dark)

            assertEquals(ThemeChoice.Dark, repository.currentChoice())
        } finally {
            database.close()
        }
    }

    @Test
    fun setChoiceExecutesDaoWriteOffTheCallingThread() = runBlocking {
        val callingThread = Thread.currentThread().name
        val dao = RecordingThemeSettingsDao()
        Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "theme-settings-io")
        }.asCoroutineDispatcher().use { ioDispatcher ->
            val repository = ThemeSettingsRepository(dao, ioDispatcher)

            repository.setChoice(ThemeChoice.Dark)

            assertTrue(dao.writeThread?.startsWith("theme-settings-io") == true)
            assertNotEquals(callingThread, dao.writeThread)
        }
    }

    @Test
    fun highContrastDefaultsOffAndPersistsIndependentlyFromThemeChoice() = runBlocking {
        val database = openDatabase()
        try {
            val repository = ThemeSettingsRepository(database.themeSettingsDao())

            assertEquals(false, repository.highContrastFlow().first())

            repository.setChoice(ThemeChoice.Light)
            repository.setHighContrast(true)

            assertEquals(ThemeChoice.Light, repository.currentChoice())
            assertEquals(true, repository.highContrastFlow().first())
        } finally {
            database.close()
        }
    }

    @Test
    fun setHighContrastExecutesDaoWriteOffTheCallingThread() = runBlocking {
        val callingThread = Thread.currentThread().name
        val dao = RecordingThemeSettingsDao()
        Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "high-contrast-settings-io")
        }.asCoroutineDispatcher().use { ioDispatcher ->
            val repository = ThemeSettingsRepository(dao, ioDispatcher)

            repository.setHighContrast(true)

            assertEquals(ThemeSettingEntity.HIGH_CONTRAST_KEY, dao.entity?.key)
            assertTrue(dao.writeThread?.startsWith("high-contrast-settings-io") == true)
            assertNotEquals(callingThread, dao.writeThread)
        }
    }

    @Test
    fun textScaleDefaultsToOneHundredPercentAndPersistsIndependently() = runBlocking {
        val database = openDatabase()
        try {
            val repository = ThemeSettingsRepository(database.themeSettingsDao())

            assertEquals(TextScaleStep.Default, repository.textScaleFlow().first())

            repository.setChoice(ThemeChoice.Light)
            repository.setHighContrast(true)
            repository.setTextScale(TextScaleStep.Maximum)

            assertEquals(ThemeChoice.Light, repository.currentChoice())
            assertEquals(true, repository.highContrastFlow().first())
            assertEquals(TextScaleStep.Maximum, repository.textScaleFlow().first())
        } finally {
            database.close()
        }
    }

    @Test
    fun setTextScaleExecutesDaoWriteOffTheCallingThread() = runBlocking {
        val callingThread = Thread.currentThread().name
        val dao = RecordingThemeSettingsDao()
        Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "text-scale-settings-io")
        }.asCoroutineDispatcher().use { ioDispatcher ->
            val repository = ThemeSettingsRepository(dao, ioDispatcher)

            repository.setTextScale(TextScaleStep.Maximum)

            assertEquals(ThemeSettingEntity.TEXT_SCALE_KEY, dao.entity?.key)
            assertEquals(TextScaleStep.Maximum.storedValue, dao.entity?.choice)
            assertTrue(dao.writeThread?.startsWith("text-scale-settings-io") == true)
            assertNotEquals(callingThread, dao.writeThread)
        }
    }

    private fun openDatabase(): PreciousDatabase =
        Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PreciousDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()

    private class RecordingThemeSettingsDao : ThemeSettingsDao {
        var entity: ThemeSettingEntity? = null
        var writeThread: String? = null

        override fun get(key: String): ThemeSettingEntity? = entity

        override fun observe(key: String): Flow<ThemeSettingEntity?> = flowOf(entity)

        override fun upsert(entity: ThemeSettingEntity) {
            this.entity = entity
            writeThread = Thread.currentThread().name
        }
    }
}
