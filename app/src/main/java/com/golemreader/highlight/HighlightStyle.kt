package com.golemreader.highlight

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class HighlightStyle(
    val extraPadding: Dp,
    val contrastMultiplier: Float,
    val fadeMillis: Int,
) {
    fun effective(reducedMotion: Boolean): HighlightStyle =
        if (reducedMotion) copy(fadeMillis = 0) else this

    companion object {
        val V1Defaults = HighlightStyle(
            extraPadding = 0.dp,
            contrastMultiplier = 1f,
            fadeMillis = 0,
        )
    }
}
