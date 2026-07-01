package com.golemreader.identity

import java.io.File

object IdentityFixture {
    fun file(name: String): File {
        val resource = requireNotNull(
            javaClass.classLoader?.getResource("fixtures/identity/$name"),
        ) { "Missing identity fixture: $name" }
        return File(resource.toURI())
    }
}
