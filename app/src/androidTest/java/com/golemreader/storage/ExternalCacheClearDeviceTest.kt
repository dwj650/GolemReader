package com.golemreader.storage

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExternalCacheClearDeviceTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun seedForExternalOsCacheClear() {
        assumeExternalCacheCheck()
        context.deleteDatabase(TEST_DB)
        val rebuildableCache = File(context.cacheDir, "rebuildable").apply {
            deleteRecursively()
            mkdirs()
        }
        File(rebuildableCache, CACHE_FILE).writeText("rebuildable")

        openDatabase().useDatabase {
            it.dbMetaDao().upsert(DbMetaEntity(key = PRECIOUS_KEY, value = "precious"))
        }
    }

    @Test
    fun verifyAfterExternalOsCacheClear() {
        assumeExternalCacheCheck()
        assertFalse(File(context.cacheDir, "rebuildable/$CACHE_FILE").exists())
        openDatabase().useDatabase {
            assertEquals(
                DbMetaEntity(key = PRECIOUS_KEY, value = "precious"),
                it.dbMetaDao().get(PRECIOUS_KEY),
            )
        }
    }

    private fun assumeExternalCacheCheck() {
        val arguments = InstrumentationRegistry.getArguments()
        assumeTrue(arguments.getString(ARG_EXTERNAL_CACHE_CHECK) == "true")
    }

    private fun openDatabase(): PreciousDatabaseHandle {
        val database = Room.databaseBuilder(
            context,
            PreciousDatabase::class.java,
            TEST_DB,
        )
            .addMigrations(PreciousDatabase.Migrations.V1_TO_V2)
            .allowMainThreadQueries()
            .build()
        return PreciousDatabaseHandle(database)
    }

    private companion object {
        const val ARG_EXTERNAL_CACHE_CHECK = "golem.externalCacheCheck"
        const val TEST_DB = "golem-external-cache-clear-test"
        const val CACHE_FILE = "os-cache.tmp"
        const val PRECIOUS_KEY = "os-cache-clear"
    }
}
