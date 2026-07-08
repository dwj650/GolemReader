package com.golemreader.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GolemThemeTokensTest {
    @Test
    fun shippedThemesResolveEveryTokenCategory() {
        listOf(GolemThemeValueSets.dark, GolemThemeValueSets.light).forEach { valueSet ->
            assertComplete(valueSet)
        }
    }

    @Test
    fun darkThemeUsesD98PrototypePaletteAnchors() {
        val colors = GolemThemeValueSets.dark.colors

        assertEquals(Color(0xFF0D0F0C), colors.background)
        assertEquals(Color(0xFF181C15), colors.surface)
        assertEquals(Color(0xFFD8DDCC), colors.textPrimary)
        assertEquals(Color(0xFFA8CC3A), colors.accent)
        assertEquals(Color(0xFFE8A040), colors.highlight)
    }

    @Test
    fun primaryTextContrastPassesBaselineInBothThemes() {
        listOf(GolemThemeValueSets.dark, GolemThemeValueSets.light).forEach { valueSet ->
            assertTrue(
                "${valueSet.name} primary text contrast",
                contrastRatio(valueSet.colors.textPrimary, valueSet.colors.background) >= 4.5,
            )
        }
    }

    @Test
    fun thirdThemeValueSetUsesSameTokenMapWithoutScreenChanges() {
        val testTheme = GolemThemeValueSets.dark.copy(
            name = "test-third-theme",
            colors = GolemThemeValueSets.dark.colors.copy(accent = Color(0xFF44CCFF)),
        )

        assertComplete(testTheme)
        assertNotEquals(GolemThemeValueSets.dark.colors.accent, testTheme.colors.accent)
        assertEquals(GolemThemeValueSets.dark.spacing.screenPadding, testTheme.spacing.screenPadding)
    }

    @Test
    fun followSystemResolvesFromOsDarkState() {
        assertEquals(
            GolemThemeValueSets.dark,
            resolveThemeValueSet(ThemeChoice.FollowSystem, systemDark = true),
        )
        assertEquals(
            GolemThemeValueSets.light,
            resolveThemeValueSet(ThemeChoice.FollowSystem, systemDark = false),
        )
    }

    private fun assertComplete(valueSet: GolemThemeValueSet) {
        assertTrue(valueSet.colors.allResolved())
        assertTrue(valueSet.typography.allResolved())
        assertTrue(valueSet.shapes.allResolved())
        assertTrue(valueSet.elevation.allResolved())
        assertTrue(valueSet.spacing.allResolved())
        assertTrue(valueSet.motion.allResolved())
    }
}
