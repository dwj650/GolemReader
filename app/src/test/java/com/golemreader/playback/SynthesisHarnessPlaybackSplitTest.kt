package com.golemreader.playback

import com.golemreader.audio.SynthesisHarness
import org.junit.Assert.assertNotNull
import org.junit.Test

class SynthesisHarnessPlaybackSplitTest {
    @Test
    fun harnessExposesAudioSinkForStreamingConsumer() {
        val sink = SynthesisHarness().createAudioSink()

        assertNotNull(sink)
    }
}
