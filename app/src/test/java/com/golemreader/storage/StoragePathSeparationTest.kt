package com.golemreader.storage

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoragePathSeparationTest {
    @Test
    fun preciousAndRebuildableLocationsAreNonNested() {
        val tiers = StorageTierLocations(
            preciousDatabase = File("/data/user/0/com.golemreader/databases/golem_precious.db"),
            importedAssets = File("/data/user/0/com.golemreader/files/imported-assets"),
            rebuildableCache = File("/data/user/0/com.golemreader/cache/rebuildable"),
        )

        assertTrue(tiers.arePreciousAndRebuildableSeparated())
        assertFalse(tiers.rebuildableCache.canEnumerate(tiers.preciousDatabase))
        assertFalse(requireNotNull(tiers.preciousDatabase.parentFile).canEnumerate(tiers.rebuildableCache))
    }
}
