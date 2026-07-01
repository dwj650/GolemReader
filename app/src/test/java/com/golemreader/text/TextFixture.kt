package com.golemreader.text

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object TextFixture {
    fun file(name: String): File {
        val resource = requireNotNull(
            javaClass.classLoader?.getResource("fixtures/text/$name"),
        ) { "Missing text fixture: $name" }
        return File(resource.toURI())
    }
}

fun ZipOutputStream.writeEntry(name: String, bytes: ByteArray) {
    putNextEntry(ZipEntry(name))
    write(bytes)
    closeEntry()
}
