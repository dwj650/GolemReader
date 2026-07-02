package com.golemreader.playback

import com.golemreader.text.SentenceIndex
import com.golemreader.voice.SynthesizedAudio
import java.util.ArrayDeque

data class BufferedAudio(
    val audio: SynthesizedAudio,
) {
    val sentenceIndex: SentenceIndex = audio.sentenceIndex
}

class StreamingBuffer {
    private val queue = ArrayDeque<SynthesizedAudio>()

    @Synchronized
    fun enqueue(audio: SynthesizedAudio) {
        queue.addLast(audio.deepCopy())
    }

    @Synchronized
    fun poll(): BufferedAudio? {
        val audio = queue.pollFirst() ?: return null
        return BufferedAudio(audio.deepCopy())
    }

    @Synchronized
    fun flush() {
        queue.clear()
    }

    @Synchronized
    fun isEmpty(): Boolean = queue.isEmpty()

    @Synchronized
    fun depthSeconds(): Double =
        queue.sumOf { audio ->
            if (audio.sampleRateHz <= 0) 0.0 else audio.samples.size.toDouble() / audio.sampleRateHz.toDouble()
        }

    private fun SynthesizedAudio.deepCopy(): SynthesizedAudio =
        copy(samples = samples.copyOf())
}
