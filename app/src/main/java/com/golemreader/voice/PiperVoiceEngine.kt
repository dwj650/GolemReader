package com.golemreader.voice

import android.content.Context
import com.k2fsa.sherpa.onnx.OfflineTts
import com.k2fsa.sherpa.onnx.getOfflineTtsConfig
import java.io.File

class PiperVoiceEngine : VoiceEngine {
    private var tts: OfflineTts? = null
    private var capabilities = VoiceEngineCapabilities(
        engineName = ENGINE_NAME,
        supportsAbort = true,
        supportsMultipleSpeakers = false,
        supportsSpeed = true,
    )

    override fun load(context: Context, modelRoot: File) {
        require(modelRoot.exists()) { "Missing Piper model root: ${modelRoot.absolutePath}" }
        require(modelRoot.resolve("tokens.txt").exists()) {
            "Missing sherpa-packaged Piper tokens.txt: ${modelRoot.resolve("tokens.txt").absolutePath}"
        }
        val config = getOfflineTtsConfig(
            modelDir = modelRoot.absolutePath,
            modelName = "en_US-lessac-medium.onnx",
            acousticModelName = "",
            vocoder = "",
            voices = "",
            lexicon = "",
            dataDir = modelRoot.resolve("espeak-ng-data").absolutePath,
            dictDir = "",
            ruleFsts = "",
            ruleFars = "",
            numThreads = 2,
        )
        tts = OfflineTts(null, config)
        val loaded = requireNotNull(tts)
        capabilities = capabilities.copy(
            sampleRateHz = loaded.sampleRate(),
            speakerCount = loaded.numSpeakers(),
        )
    }

    override fun speak(request: VoiceSynthesisRequest): SynthesizedAudio {
        val loaded = requireNotNull(tts) { "PiperVoiceEngine must be loaded before speak()." }
        if (request.shouldAbort()) {
            return SynthesizedAudio(request.sentenceIndex, FloatArray(0), loaded.sampleRate(), 0)
        }
        val startedAt = System.nanoTime()
        val generated = loaded.generateWithCallback(
            request.text,
            request.speakerId,
            request.speed,
            PiperAbortCallback(request.shouldAbort),
        )
        val elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000
        return SynthesizedAudio(
            sentenceIndex = request.sentenceIndex,
            samples = generated.samples.copyOf(),
            sampleRateHz = generated.sampleRate,
            synthesizeToFirstSampleMillis = elapsedMillis,
        )
    }

    override fun stop() {
        // Offline generation is synchronous; abort is handled through the speak callback.
    }

    override fun release() {
        tts?.release()
        tts = null
    }

    override fun reportCapabilities(): VoiceEngineCapabilities = capabilities

    private companion object {
        const val ENGINE_NAME = "Piper"
    }
}

private class PiperAbortCallback(
    private val shouldAbort: () -> Boolean,
) : Function1<FloatArray, Int> {
    override fun invoke(samples: FloatArray): Int = if (shouldAbort()) 0 else 1
}
