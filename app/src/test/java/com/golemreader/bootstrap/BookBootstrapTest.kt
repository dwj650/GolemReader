package com.golemreader.bootstrap

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.golemreader.playback.AudioSink
import com.golemreader.text.SentenceIndex
import com.golemreader.voice.SynthesizedAudio
import com.golemreader.voice.VoiceEngine
import com.golemreader.voice.VoiceEngineCapabilities
import com.golemreader.voice.VoiceSynthesisRequest
import java.io.File
import kotlin.math.sin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BookBootstrapTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun bootstrapBuildsRealPipelineAndSharedPlaybackState() {
        val sink = RecordingAudioSink()
        val bootstrap = BookBootstrap(
            context = context,
            fixtureBook = fixture("tom-sawyer.epub"),
            voiceEngineFactory = { ToneVoiceEngine() },
            modelRoot = File("."),
            audioSinkFactory = { sink },
            tickMillis = 5L,
        )

        val result = bootstrap.start()

        assertTrue(result.sentences.isNotEmpty())
        assertSame(result.transportHub, result.attachedTransportHub)
        assertSame(result.starvationState, result.sessionStarvationState)
        Thread.sleep(100)
        assertTrue(result.session.isRunning)
        assertTrue(sink.playedSnapshot().isEmpty())

        result.transportControls.play()

        assertTrue(await { sink.playedSnapshot().isNotEmpty() })
        val highlighted = result.highlightEmitter.currentState()
        assertNotNull(highlighted)
        assertEquals(sink.playedSnapshot().last(), highlighted?.sentenceIndex)

        result.transportControls.stop()
        result.session.join(timeoutMillis = 2_000)
    }

    private class RecordingAudioSink : AudioSink {
        private val played = mutableListOf<SentenceIndex>()

        @Synchronized
        override fun play(audio: SynthesizedAudio) {
            played += audio.sentenceIndex
            Thread.sleep(SEGMENT_MILLIS)
        }

        override fun flush() = Unit

        @Synchronized
        fun playedSnapshot(): List<SentenceIndex> = played.toList()
    }

    private class ToneVoiceEngine : VoiceEngine {
        override fun load(context: Context, modelRoot: File) = Unit

        override fun speak(request: VoiceSynthesisRequest): SynthesizedAudio {
            val sampleRateHz = 1_000
            val samples = FloatArray(SEGMENT_MILLIS.toInt()) { index ->
                (sin(index.toDouble() / 4.0) * 0.12).toFloat()
            }
            return SynthesizedAudio(
                sentenceIndex = request.sentenceIndex,
                samples = samples,
                sampleRateHz = sampleRateHz,
                synthesizeToFirstSampleMillis = 1,
            )
        }

        override fun stop() = Unit

        override fun release() = Unit

        override fun reportCapabilities() = VoiceEngineCapabilities(
            engineName = "tone",
            supportsAbort = true,
            supportsMultipleSpeakers = false,
            supportsSpeed = false,
            sampleRateHz = 1_000,
        )
    }

    private fun fixture(name: String): File {
        val resource = requireNotNull(
            javaClass.classLoader?.getResource("fixtures/text/$name"),
        ) { "Missing text fixture: $name" }
        return File(resource.toURI())
    }

    private fun await(condition: () -> Boolean): Boolean {
        val deadline = System.currentTimeMillis() + 3_000
        while (System.currentTimeMillis() < deadline) {
            if (condition()) return true
            Thread.sleep(10)
        }
        return condition()
    }

    private companion object {
        const val SEGMENT_MILLIS = 20L
    }
}
