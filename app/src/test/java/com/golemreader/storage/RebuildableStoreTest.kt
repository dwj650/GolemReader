package com.golemreader.storage

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class RebuildableStoreTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun cacheClearRemovesOnlyRebuildableData() {
        val root = temporaryFolder.newFolder("app")
        val precious = File(root, "databases/golem_precious.db").apply {
            requireNotNull(parentFile).mkdirs()
            writeText("precious")
        }
        val rebuildable = File(root, "cache/rebuildable").apply {
            mkdirs()
            resolve("segment.tmp").writeText("rebuildable")
        }
        val tiers = StorageTierLocations(
            preciousDatabase = precious,
            importedAssets = File(root, "files/imported-assets"),
            rebuildableCache = rebuildable,
        )
        val store = RebuildableStore(rebuildable)
        store.put("ram-entry", "value".toByteArray())

        CacheClearRoutine.clearRebuildable(tiers, store)

        assertEquals("precious", precious.readText())
        assertTrue(rebuildable.exists())
        assertFalse(File(rebuildable, "segment.tmp").exists())
        assertTrue(store.isEmpty())
    }

    @Test
    fun evictionProtectsEntriesMarkedInUseOrNearPlayhead() {
        val cache = temporaryFolder.newFolder("rebuildable")
        val store = RebuildableStore(cache)
        store.put("ordinary", "ordinary".toByteArray())
        store.put("in-use", "in-use".toByteArray(), inUse = true)
        store.put("near-playhead", "near-playhead".toByteArray(), nearPlayhead = true)

        val firstPass = store.evictUnprotected()

        assertEquals(setOf("ordinary"), firstPass.removedIds)
        assertFalse(store.contains("ordinary"))
        assertTrue(store.contains("in-use"))
        assertTrue(store.contains("near-playhead"))

        store.mark("in-use", inUse = false)
        store.mark("near-playhead", nearPlayhead = false)
        val secondPass = store.evictUnprotected()

        assertEquals(setOf("in-use", "near-playhead"), secondPass.removedIds)
        assertTrue(store.isEmpty())
    }
}
