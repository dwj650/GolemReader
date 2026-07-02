package com.golemreader.playback

import com.golemreader.audio.SynthesisHarness
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import com.golemreader.voice.VoiceEngine

class PlaybackProducer(
    private val sentences: List<SentenceRecord>,
    private val harness: SynthesisHarness,
    private val engine: VoiceEngine,
    private val buffer: StreamingBuffer,
    private val maxLookAheadSeconds: Double,
    private val smallFirstCount: Int = 1,
) {
    @Volatile
    private var stopped = false

    fun stop() {
        stopped = true
    }

    fun renderSmallFirstFrom(target: SentenceIndex): Int {
        stopped = false
        return renderFrom(target, maxSegments = smallFirstCount.coerceAtLeast(1))
    }

    fun renderLookAheadFrom(target: SentenceIndex): Int {
        stopped = false
        var rendered = 0
        var cursor = target
        while (!stopped && buffer.depthSeconds() < maxLookAheadSeconds) {
            val count = renderFrom(cursor, maxSegments = 1)
            if (count == 0) return rendered
            rendered += count
            val currentPosition = sentences.indexOfFirst { it.index == cursor }
            val next = sentences.getOrNull(currentPosition + 1) ?: return rendered
            cursor = next.index
        }
        return rendered
    }

    private fun renderFrom(
        target: SentenceIndex,
        maxSegments: Int,
    ): Int {
        val start = sentences.indexOfFirst { it.index == target }
        if (start == -1) return 0

        var rendered = 0
        for (sentence in sentences.drop(start).take(maxSegments)) {
            if (stopped) return rendered
            buffer.enqueue(harness.synthesize(sentence, engine))
            rendered += 1
        }
        return rendered
    }
}
