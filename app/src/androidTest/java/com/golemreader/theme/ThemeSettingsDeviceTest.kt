package com.golemreader.theme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.golemreader.storage.GolemStorageSubstrate
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class ThemeSettingsDeviceTest {
    @Test
    fun writesPersistedDarkChoiceForS12DeviceVerification() {
        val storage = GolemStorageSubstrate.initialize(
            context = ApplicationProvider.getApplicationContext<Context>(),
            allowMainThreadQueriesForTests = true,
        )

        storage.preciousDatabase.useDatabase { database ->
            val repository = ThemeSettingsRepository(database.themeSettingsDao())

            runBlocking { repository.setChoice(ThemeChoice.Dark) }

            assertEquals(ThemeChoice.Dark, repository.currentChoice())
        }
    }
}
