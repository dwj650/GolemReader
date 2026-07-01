package com.golemreader.voice

import android.content.Context
import java.io.File

interface VoiceEngine {
    fun load(context: Context, modelRoot: File)
    fun speak(request: VoiceSynthesisRequest): SynthesizedAudio
    fun stop()
    fun release()
    fun reportCapabilities(): VoiceEngineCapabilities
}
