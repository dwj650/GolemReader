package com.golemreader.identity

sealed class EpubStructuralException(message: String, cause: Throwable? = null) :
    Exception(message, cause) {
    class MissingContainer :
        EpubStructuralException("EPUB is missing META-INF/container.xml")

    class MissingPackagePath :
        EpubStructuralException("EPUB container does not name an OPF package file")

    class MissingPackage(path: String) :
        EpubStructuralException("EPUB package file is missing: $path")

    class EmptySpine :
        EpubStructuralException("EPUB package spine has no itemrefs")

    class MissingManifestItem(idref: String) :
        EpubStructuralException("EPUB spine references missing manifest item: $idref")

    class MissingSpineEntry(entryName: String) :
        EpubStructuralException("EPUB spine entry is missing: $entryName")

    class MalformedXml(path: String, cause: Throwable) :
        EpubStructuralException("EPUB XML is malformed: $path", cause)
}
