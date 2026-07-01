package com.golemreader.identity

import java.io.File

class BookIdentityService(
    private val dao: BookIdentityDao,
    private val hasher: BookIdentityHasher = BookIdentityHasher(),
    private val clock: () -> Long = { System.currentTimeMillis() },
) {
    fun computeIdentity(file: File): String = hasher.hash(file)

    fun register(file: File): BookIdentityRegistration {
        val hash = computeIdentity(file)
        if (dao.get(hash) != null) {
            return BookIdentityRegistration(hash, BookIdentityRegistrationStatus.Known)
        }
        val inserted = dao.insertIfAbsent(
            BookIdentityEntity(
                hash = hash,
                algorithm = BookIdentityHasher.ALGORITHM,
                recipeVersion = BookIdentityHasher.RECIPE_VERSION,
                createdAtEpochMs = clock(),
            ),
        )
        val status = if (inserted == -1L) {
            BookIdentityRegistrationStatus.Known
        } else {
            BookIdentityRegistrationStatus.New
        }
        return BookIdentityRegistration(hash, status)
    }

    fun isKnown(hash: String): Boolean = dao.get(hash) != null
}

data class BookIdentityRegistration(
    val hash: String,
    val status: BookIdentityRegistrationStatus,
)

enum class BookIdentityRegistrationStatus {
    Known,
    New,
}
