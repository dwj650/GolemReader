package com.golemreader.identity

import java.io.File
import java.nio.ByteBuffer
import java.security.MessageDigest

class BookIdentityHasher(
    private val reader: EpubStructuralReader = EpubStructuralReader(),
) {
    fun hash(file: File): String {
        val digest = MessageDigest.getInstance(ALGORITHM)
        reader.read(file).documents.forEach { document ->
            digest.update(ByteBuffer.allocate(Long.SIZE_BYTES).putLong(document.byteLength).array())
            document.openStream().use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (true) {
                    val read = input.read(buffer)
                    if (read == -1) break
                    digest.update(buffer, 0, read)
                }
            }
        }
        return digest.digest().joinToString(separator = "") { "%02x".format(it) }
    }

    companion object {
        const val ALGORITHM = "SHA-256"
        const val RECIPE_VERSION = 1
    }
}
