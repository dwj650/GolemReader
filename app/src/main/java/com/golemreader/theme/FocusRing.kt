package com.golemreader.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

fun Modifier.golemFocusRing(): Modifier = composed {
    val tokens = GolemTheme.tokens
    var focused by remember { mutableStateOf(false) }
    val ringWidth = 3.dp

    this
        .onFocusChanged { focused = it.isFocused }
        .graphicsLayer { clip = false }
        .drawWithContent {
            drawContent()
            if (focused) {
                val ringWidthPx = ringWidth.toPx()
                drawRoundRect(
                    color = tokens.colors.focusRing,
                    topLeft = Offset(-ringWidthPx, -ringWidthPx),
                    size = Size(
                        width = size.width + (ringWidthPx * 2),
                        height = size.height + (ringWidthPx * 2),
                    ),
                    cornerRadius = CornerRadius(tokens.shapes.control.toPx() + ringWidthPx),
                    style = Stroke(width = ringWidthPx),
                )
            }
        }
}
