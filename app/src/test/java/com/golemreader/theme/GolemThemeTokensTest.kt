package com.golemreader.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GolemThemeTokensTest {
    @Test
    fun shippedThemesResolveEveryTokenCategory() {
        listOf(
            GolemThemeValueSets.dark,
            GolemThemeValueSets.light,
            GolemThemeValueSets.hcDark,
            GolemThemeValueSets.hcLight,
        ).forEach { valueSet ->
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
    fun baseThemeTextContrastKeepsExistingFloor() {
        listOf(GolemThemeValueSets.dark, GolemThemeValueSets.light).forEach { valueSet ->
            assertContrastAtLeast(valueSet, "primary text", valueSet.colors.textPrimary, 4.5)
        }
    }

    @Test
    fun highContrastThemesMeetCentralD105Contract() {
        listOf(GolemThemeValueSets.hcDark, GolemThemeValueSets.hcLight).forEach(::assertHighContrast)
    }

    @Test
    fun centralHighContrastContractRejectsAWeakPalette() {
        val weakPalette = GolemThemeValueSets.dark.copy(name = "deliberately-weak-hc")

        assertThrows(AssertionError::class.java) {
            assertHighContrast(weakPalette)
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
    fun resolverMapsThemeChoiceSystemStateAndHighContrastToFourValueSets() {
        data class Case(
            val choice: ThemeChoice,
            val systemDark: Boolean,
            val highContrast: Boolean,
            val expected: GolemThemeValueSet,
        )

        listOf(
            Case(ThemeChoice.FollowSystem, true, false, GolemThemeValueSets.dark),
            Case(ThemeChoice.FollowSystem, false, false, GolemThemeValueSets.light),
            Case(ThemeChoice.Light, true, false, GolemThemeValueSets.light),
            Case(ThemeChoice.Dark, false, false, GolemThemeValueSets.dark),
            Case(ThemeChoice.FollowSystem, true, true, GolemThemeValueSets.hcDark),
            Case(ThemeChoice.FollowSystem, false, true, GolemThemeValueSets.hcLight),
            Case(ThemeChoice.Light, true, true, GolemThemeValueSets.hcLight),
            Case(ThemeChoice.Dark, false, true, GolemThemeValueSets.hcDark),
        ).forEach { case ->
            assertEquals(
                case.expected,
                resolveThemeValueSet(case.choice, case.systemDark, case.highContrast),
            )
        }
    }

    @Test
    fun togglingHighContrastSwapsAndRestoresResolvedValueSet() {
        val base = resolveThemeValueSet(ThemeChoice.Dark, systemDark = false, highContrast = false)
        val enabled = resolveThemeValueSet(ThemeChoice.Dark, systemDark = false, highContrast = true)
        val restored = resolveThemeValueSet(ThemeChoice.Dark, systemDark = false, highContrast = false)

        assertEquals(GolemThemeValueSets.dark, base)
        assertEquals(GolemThemeValueSets.hcDark, enabled)
        assertNotEquals(base, enabled)
        assertEquals(base, restored)
    }

    private fun assertHighContrast(valueSet: GolemThemeValueSet) {
        val colors = valueSet.colors
        assertContrastAtLeast(valueSet, "primary text", colors.textPrimary, 7.0)
        assertContrastAtLeast(valueSet, "secondary text", colors.textSecondary, 7.0)
        assertContrastAtLeast(valueSet, "accent control", colors.accent, 3.0)
        assertContrastAtLeast(valueSet, "highlight", colors.highlight, 3.0)
        assertTrue(
            "${valueSet.name} on-accent contrast",
            contrastRatio(colors.onAccent, colors.accent) >= 3.0,
        )
        assertTrue(
            "${valueSet.name} on-highlight contrast",
            contrastRatio(colors.onHighlight, colors.highlight) >= 3.0,
        )
    }

    private fun assertContrastAtLeast(
        valueSet: GolemThemeValueSet,
        pairName: String,
        foreground: Color,
        minimum: Double,
    ) {
        assertTrue(
            "${valueSet.name} $pairName contrast",
            contrastRatio(foreground, valueSet.colors.background) >= minimum,
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
