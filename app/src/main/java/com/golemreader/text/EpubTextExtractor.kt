package com.golemreader.text

import com.golemreader.identity.EpubStructuralReader
import java.io.ByteArrayInputStream
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

class EpubTextExtractor(
    private val structuralReader: EpubStructuralReader,
) {
    fun extract(file: File): EpubTextParseResult {
        val spine = structuralReader.read(file)
        val chapters = spine.documents.mapIndexed { chapterOrdinal, document ->
            try {
                val bytes = document.openStream().use { it.readBytes() }
                val xml = parseXml(bytes)
                ChapterParseResult(
                    chapterOrdinal = chapterOrdinal,
                    entryName = document.entryName,
                    tokens = extractTokens(xml),
                )
            } catch (error: Exception) {
                ChapterParseResult(
                    chapterOrdinal = chapterOrdinal,
                    entryName = document.entryName,
                    tokens = emptyList(),
                    failed = true,
                    failureMessage = error.message,
                )
            }
        }
        return EpubTextParseResult(chapters)
    }

    private fun parseXml(bytes: ByteArray): Document =
        xmlFactory().newDocumentBuilder().parse(ByteArrayInputStream(bytes))

    private fun extractTokens(document: Document): List<StructuralToken> {
        val body = document.elementsByLocalName("body").firstOrNull() ?: document.documentElement
        val tokens = mutableListOf(
            StructuralToken(
                type = StructuralTokenType.ChapterBoundary,
                text = "",
            ),
        )
        body.walkElements { element ->
            val localName = element.localName ?: element.nodeName
            val tokenType = when {
                localName in HEADING_TAGS -> StructuralTokenType.Heading
                localName == "p" && element.attributeContains("type", "title") -> StructuralTokenType.Heading
                localName == "p" -> StructuralTokenType.Paragraph
                else -> null
            }
            val text = element.textContent.normalizeXmlText()
            if (tokenType != null && text.isNotBlank()) {
                tokens += StructuralToken(tokenType, text)
            }
        }
        return tokens
    }

    private fun xmlFactory(): DocumentBuilderFactory =
        DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            setFeatureIfSupported("http://apache.org/xml/features/disallow-doctype-decl", true)
            setFeatureIfSupported("http://xml.org/sax/features/external-general-entities", false)
            setFeatureIfSupported("http://xml.org/sax/features/external-parameter-entities", false)
            setFeatureIfSupported("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        }

    private fun Document.elementsByLocalName(localName: String): List<Element> {
        val nodes = getElementsByTagNameNS("*", localName)
        return List(nodes.length) { index -> nodes.item(index) as Element }
    }

    private fun Element.walkElements(visit: (Element) -> Unit) {
        val children = childNodes
        for (index in 0 until children.length) {
            val child = children.item(index)
            if (child.nodeType == Node.ELEMENT_NODE) {
                val element = child as Element
                visit(element)
                element.walkElements(visit)
            }
        }
    }

    private fun Element.attributeContains(localName: String, value: String): Boolean {
        val attribute = getAttribute(localName)
            .ifBlank { getAttributeNS("http://www.idpf.org/2007/ops", localName) }
        return attribute.split(Regex("\\s+")).contains(value)
    }

    private fun DocumentBuilderFactory.setFeatureIfSupported(name: String, value: Boolean) {
        try {
            setFeature(name, value)
        } catch (_: Exception) {
            // Android and desktop XML parsers expose different hardening flags.
        }
    }

    private fun String.normalizeXmlText(): String =
        replace(Regex("[\\t\\n\\r ]+"), " ").trim()

    private companion object {
        val HEADING_TAGS = setOf("h1", "h2", "h3", "h4", "h5", "h6")
    }
}
