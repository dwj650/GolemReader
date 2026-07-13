package com.golemreader.ui.nowplaying

import com.golemreader.playback.PlayState
import com.golemreader.highlight.HighlightGlowParameters
import com.golemreader.highlight.HighlightState
import com.golemreader.text.ClauseTag
import com.golemreader.text.SegmentType
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
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

    @Test
    fun previewStripUsesCurrentHighlightedSentenceDisplayText() {
        val first = SentenceRecord(
            index = SentenceIndex(bookHash = "book", chapterOrdinal = 2, sentenceOrdinal = 0),
            display = "First sentence.",
            spoken = "First sentence.",
            segmentType = SegmentType.SentenceTerminal,
            clauseTag = ClauseTag(parentSentenceOrdinal = 0, clauseOrdinal = 0),
        )
        val current = SentenceRecord(
            index = SentenceIndex(bookHash = "book", chapterOrdinal = 2, sentenceOrdinal = 1),
            display = "Current sentence.",
            spoken = "Current sentence.",
            segmentType = SegmentType.SentenceTerminal,
            clauseTag = ClauseTag(parentSentenceOrdinal = 1, clauseOrdinal = 0),
        )

        assertEquals(
            "Current sentence.",
            previewSentenceText(
                sentences = listOf(first, current),
                highlightState = HighlightState(
                    sentenceIndex = current.index,
                    clauseTag = current.clauseTag,
                    glowParameters = HighlightGlowParameters(),
                    startedAtMillis = null,
                    durationSeconds = null,
                    expectedEndMillis = null,
                ),
            ),
        )
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
