package com.golemreader.text

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.golemreader.identity.BookIdentityHasher
import com.golemreader.identity.EpubStructuralReader
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TextPipelineDeviceTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun tomSawyerChapterOneExtractsCleansSegmentsAndForksOnDevice() {
        val book = File(context.externalMediaDirs.first(), "fixtures/text/tom-sawyer.epub")
        assertTrue(
            "Push app/src/test/resources/fixtures/text/tom-sawyer.epub to ${book.absolutePath} before running this test.",
            book.exists(),
        )
        val pipeline = TextPipeline(
            extractor = EpubTextExtractor(EpubStructuralReader()),
            preCleanStage = PreCleanStage(),
            segmenter = SentenceSegmenter(maxSegmentCharacters = 800),
        )
        val bookHash = BookIdentityHasher().hash(book)
        val runtime = Runtime.getRuntime()

        System.gc()
        val beforeBytes = runtime.totalMemory() - runtime.freeMemory()
        val startedAtNanos = System.nanoTime()
        val result = pipeline.processChapter(book, bookHash, chapterOrdinal = 5)
        val elapsedMillis = (System.nanoTime() - startedAtNanos) / 1_000_000
        System.gc()
        val afterBytes = runtime.totalMemory() - runtime.freeMemory()

        assertTrue(result.sentences.size > 120)
        assertEquals("\"Tom!\"", result.sentences.first().display)
        assertEquals("Tom!", result.sentences.first().spoken)
        Log.i(
            TAG,
            "S4 Tom Sawyer chapter-1 pipeline time=${elapsedMillis}ms memory before=$beforeBytes after=$afterBytes delta=${afterBytes - beforeBytes}",
        )
    }

    private companion object {
        const val TAG = "TextPipelineDevice"
    }
}
