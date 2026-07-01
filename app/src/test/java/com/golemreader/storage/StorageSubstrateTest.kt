package com.golemreader.storage

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StorageSubstrateTest {
    @Test
    fun firstRunInitializesAllThreeTiersAsEmpty() {
        val substrate = GolemStorageSubstrate.initialize(
            context = ApplicationProvider.getApplicationContext(),
            databaseName = "first-run-empty",
            allowMainThreadQueriesForTests = true,
        )

        substrate.preciousDatabase.useDatabase {
            assertEquals(emptyList<DbMetaEntity>(), it.dbMetaDao().all())
        }
        assertTrue(substrate.importedAssets.isEmpty())
        assertTrue(substrate.rebuildableStore.isEmpty())
        assertTrue(substrate.locations.importedAssets.exists())
        assertTrue(substrate.locations.rebuildableCache.exists())
        assertTrue(substrate.locations.arePreciousAndRebuildableSeparated())
    }
}
