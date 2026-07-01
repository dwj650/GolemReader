package com.golemreader.text

import java.text.Normalizer

class PreCleanStage {
    fun clean(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFC)
            .stripFormatControls()
            .straightenPunctuation()
            .replace(WHITESPACE, " ")
            .trim()

    private fun String.stripFormatControls(): String =
        filterNot { character ->
            character == '\uFEFF' ||
                character == '\u2060' ||
                character in '\u200B'..'\u200F' ||
                character in '\u202A'..'\u202E' ||
                character in '\u2066'..'\u2069'
        }

    private fun String.straightenPunctuation(): String =
        map { character ->
            when (character) {
                '\u2018', '\u2019', '\u201A', '\u201B' -> '\''
                '\u201C', '\u201D', '\u201E', '\u201F' -> '"'
                '\u2010', '\u2011', '\u2012', '\u2013', '\u2014', '\u2015' -> '-'
                else -> character
            }
        }.joinToString(separator = "")

    private companion object {
        val WHITESPACE = Regex("[\\s\\u00A0]+")
    }
}
