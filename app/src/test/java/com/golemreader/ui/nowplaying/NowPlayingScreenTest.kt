package com.golemreader.ui.nowplaying

import com.golemreader.playback.PlayState
import com.golemreader.text.SentenceIndex
import com.golemreader.transport.TransportCommands
import com.golemreader.transport.TransportHub
import com.golemreader.transport.TransportIntentWriter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NowPlayingScreenTest {
    @Test
    fun transportControlsForwardToTransportCommandsInOrder() {
        val writer = RecordingIntentWriter()
        val controls = NowPlayingTransportControls(
            commands = TransportCommands(TransportHub.attach(writer)),
        )

        controls.play()
        controls.pause()
        controls.resume()
        controls.stop()

        assertEquals(
            listOf(
                PlayState.Playing,
                PlayState.Paused,
                PlayState.Playing,
                PlayState.Stopped,
            ),
            writer.playStates,
        )
    }

    @Test
    fun bufferingIndicatorHasVisibleTextOnlyWhileBuffering() {
        assertEquals("Catching up...", bufferingStatusText(isBuffering = true))
        assertNull(bufferingStatusText(isBuffering = false))
    }

    private class RecordingIntentWriter : TransportIntentWriter {
        val playStates = mutableListOf<PlayState>()

        override fun play() {
            playStates += PlayState.Playing
        }

        override fun pause() {
            playStates += PlayState.Paused
        }

        override fun resume() {
            playStates += PlayState.Playing
        }

        override fun stop() {
            playStates += PlayState.Stopped
        }

        override fun seekTo(sentenceIndex: SentenceIndex) = Unit
    }
}
