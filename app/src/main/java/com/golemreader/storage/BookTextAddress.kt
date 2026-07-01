package com.golemreader.storage

data class BookTextAddress(
    val bookIdentityHash: String,
    val chapterOrdinal: Int,
    val sentenceOrdinal: Int,
    val spanStart: Int? = null,
    val spanEnd: Int? = null,
) {
    init {
        require(bookIdentityHash.isNotBlank()) { "Book identity hash must not be blank." }
        require(chapterOrdinal >= 0) { "Chapter ordinal must be zero or greater." }
        require(sentenceOrdinal >= 0) { "Sentence ordinal must be zero or greater." }
        require((spanStart == null) == (spanEnd == null)) {
            "Span start and end must either both be present or both be absent."
        }
        if (spanStart != null && spanEnd != null) {
            require(spanStart >= 0) { "Span start must be zero or greater." }
            require(spanEnd >= spanStart) { "Span end must be greater than or equal to span start." }
        }
    }
}
