package com.golemreader.storage

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PreciousDatabaseTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun firstRunInitializesPreciousDbMetaAsEmpty() {
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PreciousDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()

        try {
            assertEquals(emptyList<DbMetaEntity>(), database.dbMetaDao().all())
        } finally {
            database.close()
        }
    }

    @Test
    fun seededDbMetaSurvivesCacheClearByteIdentical() {
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PreciousDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
        val root = temporaryFolder.newFolder("storage-root")
        val rebuildableCache = File(root, "cache/rebuildable").apply { mkdirs() }
        val rebuildableStore = RebuildableStore(rebuildableCache)

        try {
            val seeded = DbMetaEntity(key = "seed", value = "precious")
            database.dbMetaDao().upsert(seeded)

            CacheClearRoutine.clearRebuildable(
                StorageTierLocations(
                    preciousDatabase = File(root, "databases/golem_precious.db").apply {
                        parentFile?.mkdirs()
                        createNewFile()
                    },
                    importedAssets = File(root, "files/imported-assets"),
                    rebuildableCache = rebuildableCache,
                ),
                rebuildableStore,
            )

            assertEquals(seeded, database.dbMetaDao().get("seed"))
        } finally {
            database.close()
        }
    }

    @Test
    fun d32AddressingKeyCarriesBookSentenceAndSpanSkeleton() {
        val address = BookTextAddress(
            bookIdentityHash = "pending-f020-hash",
            chapterOrdinal = 3,
            sentenceOrdinal = 14,
            spanStart = 2,
            spanEnd = 8,
        )

        assertEquals("pending-f020-hash", address.bookIdentityHash)
        assertEquals(3, address.chapterOrdinal)
        assertEquals(14, address.sentenceOrdinal)
        assertEquals(2, address.spanStart)
        assertEquals(8, address.spanEnd)
        assertNotNull(address)
    }
}
