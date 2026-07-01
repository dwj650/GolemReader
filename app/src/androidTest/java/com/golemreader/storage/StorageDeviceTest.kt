package com.golemreader.storage

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StorageDeviceTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Before
    fun resetStorage() {
        context.deleteDatabase(TEST_DB)
        File(context.cacheDir, "rebuildable").deleteRecursively()
    }

    @Test
    fun preciousDataSurvivesDatabaseReopen() {
        openDatabase().useDatabase {
            it.dbMetaDao().upsert(DbMetaEntity(key = "survival", value = "precious"))
        }

        openDatabase().useDatabase {
            assertEquals(
                DbMetaEntity(key = "survival", value = "precious"),
                it.dbMetaDao().get("survival"),
            )
        }
    }

    @Test
    fun cacheClearOnDevicePathsLeavesPreciousDataIntact() {
        val rebuildableCache = File(context.cacheDir, "rebuildable").apply { mkdirs() }
        val rebuildableFile = File(rebuildableCache, "segment.tmp").apply {
            writeText("rebuildable")
        }
        val store = RebuildableStore(rebuildableCache)
        store.put("ram-entry", "ram".toByteArray())

        openDatabase().useDatabase {
            it.dbMetaDao().upsert(DbMetaEntity(key = "cache-clear", value = "precious"))
        }

        CacheClearRoutine.clearRebuildable(
            StorageTierLocations(
                preciousDatabase = context.getDatabasePath(TEST_DB),
                importedAssets = File(context.filesDir, "imported-assets"),
                rebuildableCache = rebuildableCache,
            ),
            store,
        )

        assertFalse(rebuildableFile.exists())
        assertTrue(store.isEmpty())
        openDatabase().useDatabase {
            assertEquals(
                DbMetaEntity(key = "cache-clear", value = "precious"),
                it.dbMetaDao().get("cache-clear"),
            )
        }
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
        const val TEST_DB = "golem-device-storage-test"
    }
}
