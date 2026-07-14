package com.golemreader.theme

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReducedMotionPlumbingTest {
    @Test
    fun appRoutesEffectiveReducedMotionThroughProviderAndThemeOwnedControl() {
        val app = File("src/main/java/com/golemreader/ui/GolemReaderApp.kt").readText()
        val activity = File("src/main/java/com/golemreader/MainActivity.kt").readText()

        assertTrue(app.contains("reducedMotion = reducedMotion"))
        assertTrue(app.contains("inAppReducedMotion: Boolean = false"))
        assertTrue(app.contains("SettingId.ReducedMotion -> ReducedMotionToggle("))
        assertTrue(app.contains("enabled = inAppReducedMotion"))
        assertTrue(activity.contains("reducedMotionFlow()"))
        assertTrue(activity.contains("Settings.Global.ANIMATOR_DURATION_SCALE"))
        assertTrue(activity.contains("effectiveReducedMotion(osReducedMotion, inAppReducedMotion)"))
        assertTrue(activity.contains("inAppReducedMotion = inAppReducedMotion"))
        assertTrue(activity.contains("override fun onResume()"))
    }

    @Test
    fun readingSurfaceHasOnlyScrollMotionAndBranchesToInstantJump() {
        val source = File("src/main/java/com/golemreader/ui/reading/ReadingViewScreen.kt").readText()

        assertTrue(source.contains("HighlightScrollMode.Animated -> listState.animateScrollToItem"))
        assertTrue(source.contains("HighlightScrollMode.Instant -> listState.scrollToItem"))
        assertTrue(source.contains("val textColor ="))
        assertFalse(source.contains("animateTextColor"))
        assertFalse(source.contains("AnimatedVisibility"))
    }

    @Test
    fun bufferingIndicatorIsStaticAndUsesAPoliteGatedAnnouncementNode() {
        val source = File("src/main/java/com/golemreader/ui/nowplaying/BufferingIndicator.kt").readText()

        assertTrue(source.contains("delay(STARVATION_ANNOUNCE_HOLD_MILLIS)"))
        assertTrue(source.contains("liveRegion = LiveRegionMode.Polite"))
        assertTrue(source.contains("contentDescription = spokenText"))
        assertTrue(source.contains("decision.update("))
        assertFalse(source.contains("animate"))
        assertFalse(source.contains("CircularProgressIndicator"))
    }

    @Test
    fun highlightRendererConsumesFadeAndForcesASnapAtTheReducedSeam() {
        val source = File("src/main/java/com/golemreader/ui/reading/ReadingViewScreen.kt").readText()

        assertTrue(source.contains("animateColorAsState("))
        assertTrue(source.contains("durationMillis = effectiveStyle.fadeMillis"))
        assertTrue(source.contains("snap()"))
    }
}
