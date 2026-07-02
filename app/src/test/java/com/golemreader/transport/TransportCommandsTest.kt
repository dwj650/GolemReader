package com.golemreader.transport

import com.golemreader.playback.PlayState
import com.golemreader.playback.PlaybackIntent
import com.golemreader.text.SentenceIndex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Test

class TransportCommandsTest {
    @Test
    fun commandsWriteDesiredPlayStateThroughSingleHub() {
        val loop = RecordingIntentLoop()
        val hub = TransportHub.attach(loop)
        val commands = TransportCommands(hub)

        commands.pause()
        commands.resume()
        commands.stop()
        commands.play()

        assertEquals(
            listOf(
                PlayState.Paused,
                PlayState.Playing,
                PlayState.Stopped,
                PlayState.Playing,
            ),
            loop.playStates,
        )
        assertSame(hub, TransportHub.instance())
    }

    @Test
    fun callersUsingSameHubProduceIdenticalState() {
        val loop = RecordingIntentLoop()
        val hub = TransportHub.attach(loop)
        val firstCaller = TransportCommands(hub)
        val secondCaller = TransportCommands(TransportHub.instance())

        firstCaller.pause()
        secondCaller.pause()

        assertEquals(listOf(PlayState.Paused, PlayState.Paused), loop.playStates)
        assertFalse(loop.targets.any { it.hasTargetChange })
    }

    private class RecordingIntentLoop : TransportIntentWriter {
        val playStates = mutableListOf<PlayState>()
        val targets = mutableListOf<PlaybackIntent>()

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

        override fun seekTo(sentenceIndex: SentenceIndex) {
            targets += PlaybackIntent(sentenceIndex, PlayState.Playing, hasTargetChange = true)
        }
    }
}
