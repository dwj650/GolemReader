package com.golemreader.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalGolemTheme = staticCompositionLocalOf { GolemThemeValueSets.dark }

object GolemTheme {
    val tokens: GolemThemeValueSet
        @Composable
        @ReadOnlyComposable
        get() = LocalGolemTheme.current
}

@Composable
fun GolemThemeProvider(
    choice: ThemeChoice,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val valueSet = resolveThemeValueSet(choice, systemDark)
    ApplySystemBars(valueSet)
    CompositionLocalProvider(LocalGolemTheme provides valueSet) {
        MaterialTheme(
            colorScheme = valueSet.toMaterialColorScheme(),
            typography = MaterialTheme.typography,
            content = content,
        )
    }
}

@Composable
private fun ApplySystemBars(valueSet: GolemThemeValueSet) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        window.statusBarColor = valueSet.colors.systemBarBackground.toArgb()
        window.navigationBarColor = valueSet.colors.navigationBarBackground.toArgb()
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = valueSet.colors.useDarkSystemBarIcons
            isAppearanceLightNavigationBars = valueSet.colors.useDarkSystemBarIcons
        }
    }
}

private fun GolemThemeValueSet.toMaterialColorScheme(): ColorScheme =
    if (colors.useDarkSystemBarIcons) {
        lightColorScheme(
            primary = colors.accent,
            onPrimary = colors.onAccent,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.surface,
            onSurface = colors.textPrimary,
            primaryContainer = colors.highlightSoft,
            onPrimaryContainer = colors.onHighlight,
            tertiary = colors.highlight,
        )
    } else {
        darkColorScheme(
            primary = colors.accent,
            onPrimary = colors.onAccent,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.surface,
            onSurface = colors.textPrimary,
            primaryContainer = colors.highlightSoft,
            onPrimaryContainer = colors.onHighlight,
            tertiary = colors.highlight,
        )
    }
