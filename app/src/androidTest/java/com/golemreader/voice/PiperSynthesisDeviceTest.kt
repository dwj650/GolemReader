package com.golemreader.voice

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.golemreader.audio.SynthesisHarness
import com.golemreader.identity.BookIdentityHasher
import com.golemreader.identity.EpubStructuralReader
import com.golemreader.text.EpubTextExtractor
import com.golemreader.text.PreCleanStage
import com.golemreader.text.SentenceSegmenter
import com.golemreader.text.TextPipeline
import java.io.File
import kotlin.math.abs
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PiperSynthesisDeviceTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun piperSynthesizesAndPlaysFirstS4Sentence() {
        val modelRoot = File(context.externalMediaDirs.first(), "test-voices/piper")
        assertTrue(
            "Push Piper with: adb push /home/davidt14/AndroidAssets/vits-piper-en_US-lessac-medium/. ${modelRoot.absolutePath}/",
            modelRoot.resolve("en_US-lessac-medium.onnx").exists(),
        )

        val engine = PiperVoiceEngine()
        try {
            engine.load(context, modelRoot)
            engine.release()
            engine.load(context, modelRoot)

            val audio = SynthesisHarness().synthesize(firstS4Sentence(), engine)

            assertTrue("Piper returned empty PCM.", audio.samples.isNotEmpty())
            assertTrue("Piper returned silent PCM.", audio.samples.any { abs(it) > 0.003f })
            SynthesisHarness().play(audio)
            Log.i(TAG, "Piper synthesize-to-first-sample=${audio.synthesizeToFirstSampleMillis}ms samples=${audio.samples.size} sampleRate=${audio.sampleRateHz}")
        } finally {
            engine.release()
        }
    }

    private fun firstS4Sentence() = pipeline().processChapter(
        book = tomSawyer(),
        bookHash = BookIdentityHasher().hash(tomSawyer()),
        chapterOrdinal = 5,
    ).sentences.first()

    private fun pipeline() = TextPipeline(
        extractor = EpubTextExtractor(EpubStructuralReader()),
        preCleanStage = PreCleanStage(),
        segmenter = SentenceSegmenter(maxSegmentCharacters = 800),
    )

    private fun tomSawyer(): File {
        val book = File(context.externalMediaDirs.first(), "fixtures/text/tom-sawyer.epub")
        assertTrue(
            "Push app/src/test/resources/fixtures/text/tom-sawyer.epub to ${book.absolutePath} before running this test.",
            book.exists(),
        )
        return book
    }

    private companion object {
        const val TAG = "PiperSynthesisDevice"
    }
}
