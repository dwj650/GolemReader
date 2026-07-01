package com.golemreader.identity

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.golemreader.storage.PreciousDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BookIdentityServiceTest {
    @Test
    fun firstRunHasEmptyBookIdentityTable() {
        val database = openDatabase()
        try {
            assertEquals(0, database.bookIdentityDao().count())
        } finally {
            database.close()
        }
    }

    @Test
    fun registerStoresNewIdentityThenRecognizesKnownIdentity() {
        val database = openDatabase()
        try {
            val service = BookIdentityService(
                dao = database.bookIdentityDao(),
                clock = { 1234L },
            )

            val first = service.register(IdentityFixture.file("valid.epub"))
            val second = service.register(IdentityFixture.file("valid.epub"))

            assertEquals(BookIdentityRegistrationStatus.New, first.status)
            assertEquals(BookIdentityRegistrationStatus.Known, second.status)
            assertEquals(first.hash, second.hash)
            assertTrue(service.isKnown(first.hash))
            assertEquals(1, database.bookIdentityDao().count())
            assertEquals("SHA-256", database.bookIdentityDao().get(first.hash)?.algorithm)
            assertEquals(1, database.bookIdentityDao().get(first.hash)?.recipeVersion)
            assertEquals(1234L, database.bookIdentityDao().get(first.hash)?.createdAtEpochMs)
        } finally {
            database.close()
        }
    }

    @Test
    fun differentBookCreatesSecondRecord() {
        val database = openDatabase()
        try {
            val service = BookIdentityService(
                dao = database.bookIdentityDao(),
                clock = { 1L },
            )

            val first = service.register(IdentityFixture.file("distinct-a.epub"))
            val second = service.register(IdentityFixture.file("distinct-b.epub"))

            assertEquals(BookIdentityRegistrationStatus.New, first.status)
            assertEquals(BookIdentityRegistrationStatus.New, second.status)
            assertFalse(first.hash == second.hash)
            assertEquals(2, database.bookIdentityDao().count())
        } finally {
            database.close()
        }
    }

    @Test
    fun malformedBookDoesNotWriteIdentity() {
        val database = openDatabase()
        try {
            val service = BookIdentityService(
                dao = database.bookIdentityDao(),
                clock = { 1L },
            )

            assertThrows(EpubStructuralException.MissingContainer::class.java) {
                service.register(IdentityFixture.file("malformed-no-container.epub"))
            }
            assertEquals(0, database.bookIdentityDao().count())
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
