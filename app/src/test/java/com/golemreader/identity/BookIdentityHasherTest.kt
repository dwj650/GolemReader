package com.golemreader.identity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BookIdentityHasherTest {
    private val hasher = BookIdentityHasher()

    @Test
    fun knownEpubMatchesFixedSha256Digest() {
        assertEquals(
            "a8e69c8b4b53fb33e34bc2ef16b950f27a1d51a225d2fec4b249f642391565ac",
            hasher.hash(IdentityFixture.file("valid.epub")),
        )
    }

    @Test
    fun repeatedRunsAreDeterministic() {
        val file = IdentityFixture.file("valid.epub")

        assertEquals(hasher.hash(file), hasher.hash(file))
    }

    @Test
    fun decompressedSpineBytesIgnoreZipPackaging() {
        assertEquals(
            hasher.hash(IdentityFixture.file("valid.epub")),
            hasher.hash(IdentityFixture.file("repackaged.epub")),
        )
    }

    @Test
    fun nonSpineManifestChangesDoNotAffectIdentity() {
        assertEquals(
            hasher.hash(IdentityFixture.file("valid.epub")),
            hasher.hash(IdentityFixture.file("excluded-mutated.epub")),
        )
    }

    @Test
    fun spineContentChangesAffectIdentity() {
        assertNotEquals(
            hasher.hash(IdentityFixture.file("valid.epub")),
            hasher.hash(IdentityFixture.file("content-mutated.epub")),
        )
    }

    @Test
    fun spineOrderChangesAffectIdentity() {
        assertEquals(
            "58b51809be499393d6c1aa66f58b77894b078b5bba1621da9232d3d5fdc67320",
            hasher.hash(IdentityFixture.file("reordered.epub")),
        )
        assertNotEquals(
            hasher.hash(IdentityFixture.file("valid.epub")),
            hasher.hash(IdentityFixture.file("reordered.epub")),
        )
    }

    @Test
    fun distinctBooksHaveDistinctIdentities() {
        assertNotEquals(
            hasher.hash(IdentityFixture.file("distinct-a.epub")),
            hasher.hash(IdentityFixture.file("distinct-b.epub")),
        )
    }

    @Test
    fun appendixWithLinearNoContributesToIdentity() {
        assertEquals(
            "f2f29b161f698ebafaa28c889da1ba905f83b51c1d38cfebe68855424959377b",
            hasher.hash(IdentityFixture.file("with-appendix.epub")),
        )
        assertNotEquals(
            hasher.hash(IdentityFixture.file("valid.epub")),
            hasher.hash(IdentityFixture.file("with-appendix.epub")),
        )
    }

    @Test
    fun malformedInputDoesNotProduceIdentity() {
        assertThrows(EpubStructuralException.MissingContainer::class.java) {
            hasher.hash(IdentityFixture.file("malformed-no-container.epub"))
        }
        assertThrows(EpubStructuralException.EmptySpine::class.java) {
            hasher.hash(IdentityFixture.file("malformed-empty-spine.epub"))
        }
    }
}
