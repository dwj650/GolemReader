package com.golemreader.playback

import com.golemreader.voice.SynthesizedAudio

interface AudioSink {
    fun play(audio: SynthesizedAudio)
    fun flush()
}

class PlaybackConsumer(
    private val buffer: StreamingBuffer,
    private val audioSink: AudioSink,
    private val starvationState: StarvationState,
    private val interSentenceGapMillis: Long = 0,
    private val sleep: (Long) -> Unit = { Thread.sleep(it) },
) {
    fun playNext(): Boolean {
        val item = buffer.poll()
        if (item == null) {
            starvationState.onConsumerOutrunsProducer()
            return false
        }

        starvationState.onAudioRefilled()
        audioSink.play(item.audio)
        if (interSentenceGapMillis > 0) {
            sleep(interSentenceGapMillis)
        }
        return true
    }

    fun flushSink() {
        audioSink.flush()
    }
}
