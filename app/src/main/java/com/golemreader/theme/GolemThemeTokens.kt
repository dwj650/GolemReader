package com.golemreader.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

data class GolemThemeValueSet(
    val name: String,
    val colors: GolemColors,
    val typography: GolemTypography,
    val shapes: GolemShapes,
    val elevation: GolemElevation,
    val spacing: GolemSpacing,
    val motion: GolemMotion,
)

data class GolemColors(
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val outline: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val onAccent: Color,
    val highlight: Color,
    val highlightSoft: Color,
    val onHighlight: Color,
    val systemBarBackground: Color,
    val navigationBarBackground: Color,
    val useDarkSystemBarIcons: Boolean,
) {
    fun allResolved(): Boolean =
        listOf(
            background,
            surface,
            surfaceRaised,
            outline,
            textPrimary,
            textSecondary,
            accent,
            onAccent,
            highlight,
            highlightSoft,
            onHighlight,
            systemBarBackground,
            navigationBarBackground,
        ).all { it != Color.Unspecified }
}

data class GolemTypography(
    val screenTitle: TextStyle,
    val body: TextStyle,
    val bodyStrong: TextStyle,
    val label: TextStyle,
    val control: TextStyle,
    val reading: TextStyle,
) {
    fun allResolved(): Boolean =
        listOf(screenTitle, body, bodyStrong, label, control, reading).all {
            it.fontSize.isSp && it.lineHeight.isSp
        }
}

data class GolemShapes(
    val control: Dp,
    val panel: Dp,
    val highlight: Dp,
    val pill: Dp,
) {
    fun allResolved(): Boolean = listOf(control, panel, highlight, pill).all { it >= 0.dp }
}

data class GolemElevation(
    val none: Dp,
    val raised: Dp,
) {
    fun allResolved(): Boolean = listOf(none, raised).all { it >= 0.dp }
}

data class GolemSpacing(
    val none: Dp,
    val xxs: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val screenPadding: Dp,
    val reservedSlotHeight: Dp,
) {
    fun allResolved(): Boolean =
        listOf(none, xxs, xs, sm, md, lg, screenPadding, reservedSlotHeight).all { it >= 0.dp }
}

data class GolemMotion(
    val pollingIntervalMillis: Long,
    val highlightScrollEnabled: Boolean,
    val highlightTransitionMillis: Int,
    val spinnerRotationMillis: Int,
) {
    fun allResolved(): Boolean =
        pollingIntervalMillis > 0 &&
            highlightTransitionMillis >= 0 &&
            spinnerRotationMillis > 0
}

object GolemThemeValueSets {
    private val baseShapes = GolemShapes(
        control = 8.dp,
        panel = 8.dp,
        highlight = 6.dp,
        pill = 999.dp,
    )

    private val baseElevation = GolemElevation(
        none = 0.dp,
        raised = 2.dp,
    )

    private val baseSpacing = GolemSpacing(
        none = 0.dp,
        xxs = 2.dp,
        xs = 4.dp,
        sm = 8.dp,
        md = 12.dp,
        lg = 16.dp,
        screenPadding = 16.dp,
        reservedSlotHeight = 48.dp,
    )

    private val baseMotion = GolemMotion(
        pollingIntervalMillis = 100L,
        highlightScrollEnabled = true,
        highlightTransitionMillis = 220,
        spinnerRotationMillis = 1100,
    )

    val dark = GolemThemeValueSet(
        name = "dark",
        colors = GolemColors(
            background = Color(0xFF0D0F0C),
            surface = Color(0xFF181C15),
            surfaceRaised = Color(0xFF202519),
            outline = Color(0xFF2A3024),
            textPrimary = Color(0xFFD8DDCC),
            textSecondary = Color(0xFF7A826C),
            accent = Color(0xFFA8CC3A),
            onAccent = Color(0xFF0D0F0C),
            highlight = Color(0xFFE8A040),
            highlightSoft = Color(0x29E8A040),
            onHighlight = Color(0xFFD8DDCC),
            systemBarBackground = Color(0xFF0D0F0C),
            navigationBarBackground = Color(0xFF181C15),
            useDarkSystemBarIcons = false,
        ),
        typography = baseTypography(),
        shapes = baseShapes,
        elevation = baseElevation,
        spacing = baseSpacing,
        motion = baseMotion,
    )

    val light = GolemThemeValueSet(
        name = "light",
        colors = GolemColors(
            background = Color(0xFFF8FAF0),
            surface = Color(0xFFFFFFFF),
            surfaceRaised = Color(0xFFEFF3E2),
            outline = Color(0xFFD2D9C2),
            textPrimary = Color(0xFF1E261A),
            textSecondary = Color(0xFF5D6652),
            accent = Color(0xFF4F6F12),
            onAccent = Color(0xFFFFFFFF),
            highlight = Color(0xFFB86716),
            highlightSoft = Color(0x26B86716),
            onHighlight = Color(0xFF1E261A),
            systemBarBackground = Color(0xFFF8FAF0),
            navigationBarBackground = Color(0xFFFFFFFF),
            useDarkSystemBarIcons = true,
        ),
        typography = baseTypography(),
        shapes = baseShapes,
        elevation = baseElevation,
        spacing = baseSpacing,
        motion = baseMotion,
    )

    val hcDark = GolemThemeValueSet(
        name = "hc-dark",
        colors = GolemColors(
            background = Color(0xFF000000),
            surface = Color(0xFF0A0A0A),
            surfaceRaised = Color(0xFF141414),
            outline = Color(0xFFFFFFFF),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFD6D6D6),
            accent = Color(0xFFCFFF00),
            onAccent = Color(0xFF000000),
            highlight = Color(0xFFFFD000),
            highlightSoft = Color(0xFFFFF0A0),
            onHighlight = Color(0xFF000000),
            systemBarBackground = Color(0xFF000000),
            navigationBarBackground = Color(0xFF0A0A0A),
            useDarkSystemBarIcons = false,
        ),
        typography = baseTypography(),
        shapes = baseShapes,
        elevation = baseElevation,
        spacing = baseSpacing,
        motion = baseMotion,
    )

    val hcLight = GolemThemeValueSet(
        name = "hc-light",
        colors = GolemColors(
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            surfaceRaised = Color(0xFFF2F2F2),
            outline = Color(0xFF000000),
            textPrimary = Color(0xFF000000),
            textSecondary = Color(0xFF333333),
            accent = Color(0xFF003A80),
            onAccent = Color(0xFFFFFFFF),
            highlight = Color(0xFFC00000),
            highlightSoft = Color(0xFFFFDADA),
            onHighlight = Color(0xFF000000),
            systemBarBackground = Color(0xFFFFFFFF),
            navigationBarBackground = Color(0xFFFFFFFF),
            useDarkSystemBarIcons = true,
        ),
        typography = baseTypography(),
        shapes = baseShapes,
        elevation = baseElevation,
        spacing = baseSpacing,
        motion = baseMotion,
    )

    private fun baseTypography() = GolemTypography(
        screenTitle = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            lineHeight = 28.sp,
        ),
        body = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp,
        ),
        bodyStrong = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            lineHeight = 22.sp,
        ),
        label = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp,
        ),
        control = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        ),
        reading = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Normal,
            fontSize = 19.sp,
            lineHeight = 33.sp,
        ),
    )
}

fun resolveThemeValueSet(
    choice: ThemeChoice,
    systemDark: Boolean,
    highContrast: Boolean,
): GolemThemeValueSet {
    val useDarkValues = when (choice) {
        ThemeChoice.FollowSystem -> systemDark
        ThemeChoice.Light -> false
        ThemeChoice.Dark -> true
    }
    return when {
        highContrast && useDarkValues -> GolemThemeValueSets.hcDark
        highContrast -> GolemThemeValueSets.hcLight
        useDarkValues -> GolemThemeValueSets.dark
        else -> GolemThemeValueSets.light
    }
}

fun contrastRatio(foreground: Color, background: Color): Double {
    val lighter = max(foreground.relativeLuminance(), background.relativeLuminance())
    val darker = min(foreground.relativeLuminance(), background.relativeLuminance())
    return (lighter + 0.05) / (darker + 0.05)
}

private fun Color.relativeLuminance(): Double {
    fun channel(value: Float): Double {
        val normalized = value.toDouble()
        return if (normalized <= 0.03928) {
            normalized / 12.92
        } else {
            ((normalized + 0.055) / 1.055).pow(2.4)
        }
    }

    return 0.2126 * channel(red) + 0.7152 * channel(green) + 0.0722 * channel(blue)
}
