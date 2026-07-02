package com.golemreader.playback

import com.golemreader.text.SentenceIndex
import com.golemreader.voice.SynthesizedAudio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackConsumerTest {
    @Test
    fun consumerHoldsWhenBufferIsEmptyAndRecoversOnRefill() {
        val buffer = StreamingBuffer()
        val sink = RecordingAudioSink()
        val starvation = StarvationState()
        val consumer = PlaybackConsumer(buffer, sink, starvation)

        assertFalse(consumer.playNext())
        assertTrue(starvation.isBuffering)

        buffer.enqueue(audio(sentence = 0))

        assertTrue(consumer.playNext())
        assertFalse(starvation.isBuffering)
        assertEquals(listOf(SentenceIndex("book", 5, 0)), sink.played)
    }

    @Test
    fun consumerOwnsInterSentenceGapAfterSinkPlayback() {
        val buffer = StreamingBuffer()
        val sink = RecordingAudioSink()
        val sleeps = mutableListOf<Long>()
        val consumer = PlaybackConsumer(
            buffer = buffer,
            audioSink = sink,
            starvationState = StarvationState(),
            interSentenceGapMillis = 75,
            sleep = { sleeps += it },
        )

        buffer.enqueue(audio(sentence = 0))

        assertTrue(consumer.playNext())
        assertEquals(listOf(75L), sleeps)
    }

    @Test
    fun consumerReportsSegmentStartBeforeSinkPlayback() {
        val buffer = StreamingBuffer()
        val events = mutableListOf<String>()
        val sink = RecordingAudioSink(onPlay = { events += "sink:${it.sentenceIndex.sentenceOrdinal}" })
        val consumer = PlaybackConsumer(
            buffer = buffer,
            audioSink = sink,
            starvationState = StarvationState(),
            onSegmentStarted = { events += "highlight:${it.sentenceIndex.sentenceOrdinal}" },
        )

        buffer.enqueue(audio(sentence = 2))

        assertTrue(consumer.playNext())
        assertEquals(listOf("highlight:2", "sink:2"), events)
    }

    private class RecordingAudioSink(
        private val onPlay: (SynthesizedAudio) -> Unit = {},
    ) : AudioSink {
        val played = mutableListOf<SentenceIndex>()

        override fun play(audio: SynthesizedAudio) {
            onPlay(audio)
            played += audio.sentenceIndex
        }

        override fun flush() = Unit
    }

    private fun audio(sentence: Int) = SynthesizedAudio(
        sentenceIndex = SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = sentence),
        samples = FloatArray(10) { 0.1f },
        sampleRateHz = 10,
        synthesizeToFirstSampleMillis = 1,
    )
}
