package com.golemreader.theme

enum class TextScaleStep(
    val storedValue: String,
    val multiplier: Float,
    val percentage: Int,
) {
    Smallest("0.85", 0.85f, 85),
    Default("1.0", 1.0f, 100),
    Large("1.15", 1.15f, 115),
    Larger("1.3", 1.3f, 130),
    Maximum("1.5", 1.5f, 150);

    fun previous(): TextScaleStep = entries.getOrElse(ordinal - 1) { this }

    fun next(): TextScaleStep = entries.getOrElse(ordinal + 1) { this }

    companion object {
        fun fromStoredValue(value: String?): TextScaleStep =
            entries.firstOrNull { it.storedValue == value } ?: Default
    }
}
