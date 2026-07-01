package com.golemreader.identity

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.golemreader.storage.PreciousDatabase
import java.io.File
import java.util.Random
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookIdentityDeviceTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val fixtureDir by lazy { File(context.cacheDir, "identity-device-fixtures") }

    @Before
    fun resetStorage() {
        context.deleteDatabase(TEST_DB)
        fixtureDir.deleteRecursively()
        fixtureDir.mkdirs()
    }

    @After
    fun cleanStorage() {
        context.deleteDatabase(TEST_DB)
        fixtureDir.deleteRecursively()
    }

    @Test
    fun sameBookReaddIsRecognizedAsKnownIdentity() {
        val book = writeEpub("same.epub", listOf("Same book chapter."))

        val database = openDatabase()
        try {
            val service = BookIdentityService(database.bookIdentityDao())

            val first = service.register(book)
            val second = service.register(book)

            assertEquals(BookIdentityRegistrationStatus.New, first.status)
            assertEquals(BookIdentityRegistrationStatus.Known, second.status)
            assertEquals(first.hash, second.hash)
            assertEquals(1, database.bookIdentityDao().count())
        } finally {
            database.close()
        }
    }

    @Test
    fun genuinelyDifferentBookCreatesNewIdentityRecord() {
        val firstBook = writeEpub("distinct-a.epub", listOf("Distinct device book A."))
        val secondBook = writeEpub("distinct-b.epub", listOf("Distinct device book B."))

        val database = openDatabase()
        try {
            val service = BookIdentityService(database.bookIdentityDao())

            val first = service.register(firstBook)
            val second = service.register(secondBook)

            assertEquals(BookIdentityRegistrationStatus.New, first.status)
            assertEquals(BookIdentityRegistrationStatus.New, second.status)
            assertFalse(first.hash == second.hash)
            assertEquals(2, database.bookIdentityDao().count())
        } finally {
            database.close()
        }
    }

    @Test
    fun identityRecordSurvivesDatabaseReopen() {
        val book = writeEpub("persistent.epub", listOf("Persistent identity chapter."))
        val firstDatabase = openDatabase()
        val hash = try {
            BookIdentityService(firstDatabase.bookIdentityDao()).register(book).hash
        } finally {
            firstDatabase.close()
        }

        val secondDatabase = openDatabase()
        try {
            val service = BookIdentityService(secondDatabase.bookIdentityDao())

            assertTrue(service.isKnown(hash))
            assertEquals(hash, secondDatabase.bookIdentityDao().get(hash)?.hash)
        } finally {
            secondDatabase.close()
        }
    }

    @Test
    fun recordsImportTimeAndMemoryCostReadings() {
        val fewMegabytes = ByteArray(3 * 1024 * 1024)
            .also { Random(20).nextBytes(it) }
        val book = writeEpubBytes("few-mb.epub", listOf(fewMegabytes))
        val runtime = Runtime.getRuntime()

        System.gc()
        val beforeBytes = runtime.totalMemory() - runtime.freeMemory()
        val startedAtNanos = System.nanoTime()
        val hash = BookIdentityHasher().hash(book)
        val elapsedMillis = (System.nanoTime() - startedAtNanos) / 1_000_000
        System.gc()
        val afterBytes = runtime.totalMemory() - runtime.freeMemory()

        assertEquals(64, hash.length)
        Log.i(TAG, "T-020-C1 hash time for ${book.length()} bytes: ${elapsedMillis}ms")
        Log.i(TAG, "T-020-C2 memory before=$beforeBytes after=$afterBytes delta=${afterBytes - beforeBytes}")
    }

    private fun openDatabase(): PreciousDatabase =
        Room.databaseBuilder(
            context,
            PreciousDatabase::class.java,
            TEST_DB,
        )
            .addMigrations(
                PreciousDatabase.Migrations.V1_TO_V2,
                PreciousDatabase.Migrations.V2_TO_V3,
            )
            .allowMainThreadQueries()
            .build()

    private fun writeEpub(name: String, chapters: List<String>): File =
        writeEpubBytes(name, chapters.map { chapter ->
            "<html><body><p>$chapter</p></body></html>".toByteArray()
        })

    private fun writeEpubBytes(name: String, chapters: List<ByteArray>): File {
        val output = File(fixtureDir, name)
        ZipOutputStream(output.outputStream()).use { zip ->
            zip.writeEntry(
                "META-INF/container.xml",
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
                  <rootfiles>
                    <rootfile full-path="OEBPS/content.opf" media-type="application/oebps-package+xml"/>
                  </rootfiles>
                </container>
                """.trimIndent().toByteArray(),
            )
            val manifest = chapters.indices.joinToString(separator = "\n") { index ->
                """    <item id="chap$index" href="chapters/chapter-$index.xhtml" media-type="application/xhtml+xml"/>"""
            }
            val spine = chapters.indices.joinToString(separator = "\n") { index ->
                """    <itemref idref="chap$index"/>"""
            }
            zip.writeEntry(
                "OEBPS/content.opf",
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <package version="3.0" xmlns="http://www.idpf.org/2007/opf">
                  <metadata/>
                  <manifest>
                $manifest
                    <item id="style" href="style.css" media-type="text/css"/>
                  </manifest>
                  <spine>
                $spine
                  </spine>
                </package>
                """.trimIndent().toByteArray(),
            )
            chapters.forEachIndexed { index, chapter ->
                zip.writeEntry(
                    "OEBPS/chapters/chapter-$index.xhtml",
                    chapter,
                )
            }
            zip.writeEntry("OEBPS/style.css", "body { color: black; }".toByteArray())
        }
        return output
    }

    private fun ZipOutputStream.writeEntry(name: String, bytes: ByteArray) {
        putNextEntry(ZipEntry(name))
        write(bytes)
        closeEntry()
    }

    private companion object {
        const val TEST_DB = "golem-identity-device-test"
        const val TAG = "BookIdentityDevice"
    }
}
