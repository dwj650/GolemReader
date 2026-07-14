package com.golemreader.theme

import com.golemreader.highlight.HighlightStyle
import com.golemreader.ui.nowplaying.CATCHING_UP_ANNOUNCEMENT
import com.golemreader.ui.nowplaying.STARVATION_ANNOUNCE_HOLD_MILLIS
import com.golemreader.ui.nowplaying.StarvationAnnouncement
import com.golemreader.ui.reading.HighlightScrollMode
import com.golemreader.ui.reading.highlightScrollMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReducedMotionContractTest {
    @Test
    fun reducedMotionOverridesOnlyTheMotionBlockAtTheProviderSeam() {
        val base = GolemThemeValueSets.hcDark

        val reduced = applyReducedMotion(base, reducedMotion = true)

        assertEquals(base.colors, reduced.colors)
        assertEquals(base.typography, reduced.typography)
        assertEquals(base.shapes, reduced.shapes)
        assertEquals(base.elevation, reduced.elevation)
        assertEquals(base.spacing, reduced.spacing)
        assertFalse(reduced.motion.highlightScrollEnabled)
        assertEquals(0, reduced.motion.highlightTransitionMillis)
        assertEquals(base.motion.pollingIntervalMillis, reduced.motion.pollingIntervalMillis)
        assertEquals(base.motion.spinnerRotationMillis, reduced.motion.spinnerRotationMillis)
        assertEquals(base, applyReducedMotion(base, reducedMotion = false))
    }

    @Test
    fun osOrInAppPreferenceEnablesEffectiveReducedMotion() {
        assertFalse(effectiveReducedMotion(osRemoveAnimations = false, inAppEnabled = false))
        assertTrue(effectiveReducedMotion(osRemoveAnimations = true, inAppEnabled = false))
        assertTrue(effectiveReducedMotion(osRemoveAnimations = false, inAppEnabled = true))
        assertTrue(effectiveReducedMotion(osRemoveAnimations = true, inAppEnabled = true))
    }

    @Test
    fun highlightSelectsGlideNormallyAndInstantJumpWhenReduced() {
        assertEquals(HighlightScrollMode.Animated, highlightScrollMode(scrollEnabled = true))
        assertEquals(HighlightScrollMode.Instant, highlightScrollMode(scrollEnabled = false))
    }

    @Test
    fun highlightStyleDefaultsReproduceCurrentLookAndReductionForcesFadeToZero() {
        val defaults = HighlightStyle.V1Defaults

        assertEquals(0f, defaults.extraPadding.value)
        assertEquals(1f, defaults.contrastMultiplier)
        assertEquals(0, defaults.fadeMillis)
        assertEquals(defaults, defaults.effective(reducedMotion = false))

        val varied = defaults.copy(contrastMultiplier = 1.4f, fadeMillis = 90)
        assertNotEquals(defaults, varied)
        assertEquals(0, varied.effective(reducedMotion = true).fadeMillis)
        assertEquals(1.4f, varied.effective(reducedMotion = true).contrastMultiplier)
    }

    @Test
    fun starvationAnnouncesOnceAtThresholdAndNeverOnRecovery() {
        val decision = StarvationAnnouncement()

        assertNull(decision.update(isBuffering = true, heldMillis = STARVATION_ANNOUNCE_HOLD_MILLIS - 1))
        assertEquals(
            CATCHING_UP_ANNOUNCEMENT,
            decision.update(isBuffering = true, heldMillis = STARVATION_ANNOUNCE_HOLD_MILLIS),
        )
        assertNull(decision.update(isBuffering = true, heldMillis = STARVATION_ANNOUNCE_HOLD_MILLIS + 100))
        assertNull(decision.update(isBuffering = false, heldMillis = 0))
    }

    @Test
    fun aNewStarvationHoldCanAnnounceAfterRecoveryReset() {
        val decision = StarvationAnnouncement()
        decision.update(isBuffering = true, heldMillis = STARVATION_ANNOUNCE_HOLD_MILLIS)
        decision.update(isBuffering = false, heldMillis = 0)

        assertEquals(
            CATCHING_UP_ANNOUNCEMENT,
            decision.update(isBuffering = true, heldMillis = STARVATION_ANNOUNCE_HOLD_MILLIS),
        )
    }
}
