package com.golemreader.text

import com.golemreader.identity.EpubStructuralReader
import com.golemreader.storage.StoragePlacementRule
import com.golemreader.storage.StorageTier
import com.golemreader.storage.StoredDataType
import java.io.File
import java.nio.charset.Charset
import java.util.zip.ZipOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class EpubTextExtractorTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val extractor = EpubTextExtractor(EpubStructuralReader())

    @Test
    fun tomSawyerChapterOneExtractsOrderedStructuralTokens() {
        val result = extractor.extract(TextFixture.file("tom-sawyer.epub"))

        val chapter = result.chapters.single { it.entryName == "epub/text/chapter-1.xhtml" }

        assertFalse(chapter.failed)
        assertEquals(5, chapter.chapterOrdinal)
        assertEquals(
            listOf(
                StructuralTokenType.ChapterBoundary,
                StructuralTokenType.Heading,
                StructuralTokenType.Heading,
                StructuralTokenType.Paragraph,
                StructuralTokenType.Paragraph,
            ),
            chapter.tokens.take(5).map { it.type },
        )
        assertEquals("I", chapter.tokens[1].text)
        assertEquals("Tom Plays, Fights, and Hides", chapter.tokens[2].text)
        assertEquals("“Tom!”", chapter.tokens[3].text)
        assertTrue(chapter.tokens.any { it.text.contains("St. Petersburg") })
        assertTrue(chapter.tokens.none { it.text.contains("<em>") })
    }

    @Test
    fun extractionIsDeterministicForTheSameSource() {
        val first = extractor.extract(TextFixture.file("tom-sawyer.epub"))
            .chapters.single { it.entryName == "epub/text/chapter-1.xhtml" }
        val second = extractor.extract(TextFixture.file("tom-sawyer.epub"))
            .chapters.single { it.entryName == "epub/text/chapter-1.xhtml" }

        assertEquals(first.tokens.joinToString("\n") { "${it.type}:${it.text}" }, second.tokens.joinToString("\n") { "${it.type}:${it.text}" })
    }

    @Test
    fun pipelineCacheEntriesBelongToRebuildableTier() {
        assertEquals(
            StorageTier.Rebuildable,
            StoragePlacementRule.tierFor(StoredDataType.PipelineCacheEntry),
        )
    }

    @Test
    fun declaredNonUtf8EncodingIsRespected() {
        val latin1Chapter = """<?xml version="1.0" encoding="ISO-8859-1"?><html xmlns="http://www.w3.org/1999/xhtml"><body><section><p>Café cost 3.14 dollars.</p></section></body></html>"""
        val epub = writeEpub(
            "latin1.epub",
            listOf(
                ChapterFixture(
                    path = "OEBPS/chapter.xhtml",
                    bytes = latin1Chapter.toByteArray(Charset.forName("ISO-8859-1")),
                ),
            ),
        )

        val chapter = extractor.extract(epub).chapters.single()

        assertFalse(chapter.failed)
        assertEquals("Café cost 3.14 dollars.", chapter.tokens.single { it.type == StructuralTokenType.Paragraph }.text)
    }

    @Test
    fun malformedChapterIsFlaggedWithoutAbortingOtherChapters() {
        val epub = writeEpub(
            "partly-bad.epub",
            listOf(
                ChapterFixture("OEBPS/bad.xhtml", "<html><body><p>Broken".toByteArray()),
                ChapterFixture(
                    "OEBPS/good.xhtml",
                    """<?xml version="1.0" encoding="UTF-8"?><html xmlns="http://www.w3.org/1999/xhtml"><body><p>Good chapter.</p></body></html>"""
                        .toByteArray(),
                ),
            ),
        )

        val result = extractor.extract(epub)

        assertTrue(result.chapters[0].failed)
        assertTrue(result.chapters[0].tokens.isEmpty())
        assertFalse(result.chapters[1].failed)
        assertEquals("Good chapter.", result.chapters[1].tokens.single { it.type == StructuralTokenType.Paragraph }.text)
    }

    private fun writeEpub(name: String, chapters: List<ChapterFixture>): File {
        val output = temporaryFolder.newFile(name)
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
            val manifest = chapters.indices.joinToString("\n") { index ->
                """<item id="chapter-$index" href="${chapters[index].path.removePrefix("OEBPS/")}" media-type="application/xhtml+xml"/>"""
            }
            val spine = chapters.indices.joinToString("\n") { index ->
                """<itemref idref="chapter-$index"/>"""
            }
            zip.writeEntry(
                "OEBPS/content.opf",
                """<?xml version="1.0" encoding="UTF-8"?>
                |<package version="3.0" xmlns="http://www.idpf.org/2007/opf">
                  <metadata/>
                  <manifest>$manifest</manifest>
                  <spine>$spine</spine>
                </package>
                """.trimMargin().toByteArray(),
            )
            chapters.forEach { chapter -> zip.writeEntry(chapter.path, chapter.bytes) }
        }
        return output
    }

    private data class ChapterFixture(val path: String, val bytes: ByteArray)
}
