package com.golemreader.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.golemreader.text.SentenceRecord
import com.golemreader.voice.SynthesizedAudio
import com.golemreader.voice.VoiceEngine
import com.golemreader.voice.VoiceSynthesisRequest
import kotlin.math.roundToInt

class SynthesisHarness(
    private val trimmer: EdgeSilenceTrimmer = EdgeSilenceTrimmer(),
) {
    fun synthesize(
        sentence: SentenceRecord,
        engine: VoiceEngine,
    ): SynthesizedAudio {
        val cleaned = TerminalCueHygiene.clean(sentence)
        val audio = engine.speak(
            VoiceSynthesisRequest(
                sentenceIndex = cleaned.index,
                text = cleaned.spoken,
            ),
        )
        return trimmer.trim(audio)
    }

    fun play(audio: SynthesizedAudio) {
        if (audio.samples.isEmpty()) return
        val pcm16 = audio.samples.toPcm16()
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(audio.sampleRateHz)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setBufferSizeInBytes(pcm16.size)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        try {
            track.write(pcm16, 0, pcm16.size)
            track.play()
            val durationMillis = (audio.samples.size * 1000L) / audio.sampleRateHz
            Thread.sleep(durationMillis.coerceAtLeast(1L))
            track.stop()
        } finally {
            track.release()
        }
    }

    private fun FloatArray.toPcm16(): ByteArray {
        val bytes = ByteArray(size * 2)
        forEachIndexed { index, sample ->
            val clamped = sample.coerceIn(-1f, 1f)
            val value = (clamped * Short.MAX_VALUE).roundToInt().toShort()
            bytes[index * 2] = (value.toInt() and 0xFF).toByte()
            bytes[index * 2 + 1] = ((value.toInt() shr 8) and 0xFF).toByte()
        }
        return bytes
    }
}
