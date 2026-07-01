package com.golemreader.identity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class EpubStructuralReaderTest {
    private val reader = EpubStructuralReader()

    @Test
    fun validEpubResolvesSpineEntriesInDocumentOrder() {
        val spine = reader.read(IdentityFixture.file("valid.epub"))

        assertEquals(
            listOf(
                "OEBPS/chapters/chapter-1.xhtml",
                "OEBPS/chapters/chapter-2.xhtml",
                "OEBPS/chapters/chapter-3.xhtml",
            ),
            spine.documents.map { it.entryName },
        )
        assertEquals(listOf(53L, 54L, 55L), spine.documents.map { it.byteLength })
    }

    @Test
    fun linearNoItemrefsAreIncludedInSpineOrder() {
        val spine = reader.read(IdentityFixture.file("with-appendix.epub"))

        assertEquals(
            listOf(
                "OEBPS/chapters/chapter-1.xhtml",
                "OEBPS/chapters/chapter-2.xhtml",
                "OEBPS/chapters/appendix.xhtml",
                "OEBPS/chapters/chapter-3.xhtml",
            ),
            spine.documents.map { it.entryName },
        )
        assertEquals(listOf(53L, 54L, 56L, 55L), spine.documents.map { it.byteLength })
    }

    @Test
    fun malformedInputReportsTypedErrors() {
        assertThrows(EpubStructuralException.MissingContainer::class.java) {
            reader.read(IdentityFixture.file("malformed-no-container.epub"))
        }
        assertThrows(EpubStructuralException.EmptySpine::class.java) {
            reader.read(IdentityFixture.file("malformed-empty-spine.epub"))
        }
    }
}
