package com.golemreader.identity

import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.nio.file.Paths
import java.util.zip.ZipFile
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.w3c.dom.Element

class EpubStructuralReader {
    fun read(file: File): EpubSpine {
        ZipFile(file).use { zip ->
            val packagePath = readPackagePath(zip)
            val packageEntry = zip.getEntry(packagePath)
                ?: throw EpubStructuralException.MissingPackage(packagePath)
            val packageDocument = parseXml(
                path = packagePath,
                bytes = zip.getInputStream(packageEntry).readBytes(),
            )
            val manifest = packageDocument.elementsByLocalName("item")
                .associate { item ->
                    val id = item.getAttribute("id")
                    val href = item.getAttribute("href")
                    id to href
                }
            val spineItemrefs = packageDocument.elementsByLocalName("itemref")
            if (spineItemrefs.isEmpty()) {
                throw EpubStructuralException.EmptySpine()
            }
            val basePath = packagePath.substringBeforeLast('/', missingDelimiterValue = "")
            val documents = spineItemrefs.map { itemref ->
                val idref = itemref.getAttribute("idref")
                val href = manifest[idref]
                    ?: throw EpubStructuralException.MissingManifestItem(idref)
                val entryName = resolveZipEntryName(basePath, href)
                val entry = zip.getEntry(entryName)
                    ?: throw EpubStructuralException.MissingSpineEntry(entryName)
                EpubSpineDocument(
                    entryName = entryName,
                    byteLength = entry.size,
                    openStream = { ZipFile(file).getInputStreamForEntry(entryName) },
                )
            }
            return EpubSpine(documents)
        }
    }

    private fun readPackagePath(zip: ZipFile): String {
        val containerEntry = zip.getEntry(CONTAINER_PATH)
            ?: throw EpubStructuralException.MissingContainer()
        val containerDocument = parseXml(
            path = CONTAINER_PATH,
            bytes = zip.getInputStream(containerEntry).readBytes(),
        )
        return containerDocument.elementsByLocalName("rootfile")
            .firstOrNull()
            ?.getAttribute("full-path")
            ?.takeIf { it.isNotBlank() }
            ?: throw EpubStructuralException.MissingPackagePath()
    }

    private fun parseXml(path: String, bytes: ByteArray): Document =
        try {
            xmlFactory().newDocumentBuilder().parse(ByteArrayInputStream(bytes))
        } catch (error: Exception) {
            throw EpubStructuralException.MalformedXml(path, error)
        }

    private fun xmlFactory(): DocumentBuilderFactory =
        DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            setFeatureIfSupported("http://apache.org/xml/features/disallow-doctype-decl", true)
            setFeatureIfSupported("http://xml.org/sax/features/external-general-entities", false)
            setFeatureIfSupported("http://xml.org/sax/features/external-parameter-entities", false)
            setFeatureIfSupported("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        }

    private fun DocumentBuilderFactory.setFeatureIfSupported(name: String, value: Boolean) {
        try {
            setFeature(name, value)
        } catch (_: Exception) {
            // Android and desktop XML parsers expose different hardening flags.
        }
    }

    private fun resolveZipEntryName(basePath: String, href: String): String {
        val resolved = if (basePath.isBlank()) {
            Paths.get(href)
        } else {
            Paths.get(basePath).resolve(href)
        }
        return resolved.normalize().toString().replace(File.separatorChar, '/')
    }

    private fun Document.elementsByLocalName(localName: String): List<Element> {
        val nodes = getElementsByTagNameNS("*", localName)
        return List(nodes.length) { index -> nodes.item(index) as Element }
    }

    private fun ZipFile.getInputStreamForEntry(entryName: String): InputStream {
        val entry = getEntry(entryName)
            ?: throw EpubStructuralException.MissingSpineEntry(entryName)
        return object : InputStream() {
            private val delegate = getInputStream(entry)

            override fun read(): Int = delegate.read()

            override fun read(buffer: ByteArray, offset: Int, length: Int): Int =
                delegate.read(buffer, offset, length)

            override fun close() {
                delegate.close()
                this@getInputStreamForEntry.close()
            }
        }
    }

    private companion object {
        const val CONTAINER_PATH = "META-INF/container.xml"
    }
}

data class EpubSpine(val documents: List<EpubSpineDocument>)

data class EpubSpineDocument(
    val entryName: String,
    val byteLength: Long,
    val openStream: () -> InputStream,
)
