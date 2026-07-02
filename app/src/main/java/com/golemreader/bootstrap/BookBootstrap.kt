package com.golemreader.bootstrap

import android.content.Context
import com.golemreader.audio.SynthesisHarness
import com.golemreader.highlight.HighlightClock
import com.golemreader.highlight.HighlightIndexMapper
import com.golemreader.highlight.HighlightStateEmitter
import com.golemreader.identity.BookIdentityDao
import com.golemreader.identity.BookIdentityEntity
import com.golemreader.identity.BookIdentityService
import com.golemreader.identity.EpubStructuralReader
import com.golemreader.playback.AbortController
import com.golemreader.playback.AudioSink
import com.golemreader.playback.IntentLoop
import com.golemreader.playback.PlaybackConsumer
import com.golemreader.playback.PlaybackProducer
import com.golemreader.playback.PlaybackSession
import com.golemreader.playback.StarvationState
import com.golemreader.playback.StreamingBuffer
import com.golemreader.playback.asDriver
import com.golemreader.text.EpubTextExtractor
import com.golemreader.text.PreCleanStage
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import com.golemreader.text.SentenceSegmenter
import com.golemreader.text.TextPipeline
import com.golemreader.transport.TransportHub
import com.golemreader.transport.TransportCommands
import com.golemreader.ui.nowplaying.NowPlayingTransportControls
import com.golemreader.voice.PiperVoiceEngine
import com.golemreader.voice.VoiceEngine
import java.io.File

data class BookBootstrapResult(
    val bookTitle: String,
    val sentences: List<SentenceRecord>,
    val highlightEmitter: HighlightStateEmitter,
    val starvationState: StarvationState,
    val transportControls: NowPlayingTransportControls,
    val transportHub: TransportHub,
    val attachedTransportHub: TransportHub,
    val session: PlaybackSession,
    val sessionStarvationState: StarvationState,
)

class BookBootstrap(
    private val context: Context,
    private val fixtureBook: File = defaultFixtureBook(context),
    private val modelRoot: File = defaultPiperModelRoot(context),
    private val voiceEngineFactory: () -> VoiceEngine = { PiperVoiceEngine() },
    private val audioSinkFactory: () -> AudioSink = { SynthesisHarness().createAudioSink() },
    private val tickMillis: Long = 10L,
) {
    fun start(): BookBootstrapResult {
        require(fixtureBook.exists()) { "Missing bootstrap EPUB fixture: ${fixtureBook.absolutePath}" }

        val identity = BookIdentityService(InMemoryBookIdentityDao()).computeIdentity(fixtureBook)
        val sentences = textPipeline().processChapterWithReadAhead(
            book = fixtureBook,
            bookHash = identity,
            chapterOrdinal = BOOTSTRAP_CHAPTER_ORDINAL,
            lookAheadChapterCount = BOOTSTRAP_LOOK_AHEAD_CHAPTERS,
        ).orderedSentences
        require(sentences.isNotEmpty()) { "Bootstrap EPUB produced no sentences." }

        val engine = voiceEngineFactory()
        engine.load(context, modelRoot)

        val buffer = StreamingBuffer()
        val starvation = StarvationState()
        val emitter = HighlightStateEmitter()
        val mapper = HighlightIndexMapper(sentences)
        val clock = HighlightClock()
        val harness = SynthesisHarness()
        val producer = PlaybackProducer(
            sentences = sentences,
            harness = harness,
            engine = engine,
            buffer = buffer,
            maxLookAheadSeconds = MAX_LOOK_AHEAD_SECONDS,
            smallFirstCount = 1,
        )
        val consumer = PlaybackConsumer(
            buffer = buffer,
            audioSink = audioSinkFactory(),
            starvationState = starvation,
            onSegmentStarted = { audio ->
                mapper.targetFor(audio.sentenceIndex)?.let { target ->
                    emitter.emit(target = target, timing = clock.segmentStarted(audio))
                }
            },
        )
        var target = sentences.first().index
        val abort = AbortController(
            stopProducer = producer::stop,
            flushBuffer = buffer::flush,
            flushAudioSink = consumer::flushSink,
            setTarget = { target = it },
            rerenderSmallFirst = { producer.renderSmallFirstFrom(target) },
            onTargetChanged = { starvation.recordIntentDuringHold(it) },
        )
        val intentLoop = IntentLoop(debounceMillis = INTENT_DEBOUNCE_MILLIS)
        intentLoop.pause()
        val session = PlaybackSession(
            intentLoop = intentLoop,
            producer = producer.asDriver(),
            consumer = consumer.asDriver(),
            abortController = abort.asDriver(),
            starvationState = starvation,
            initialTarget = target,
            flushBuffer = buffer::flush,
            nextAfter = nextAfter(sentences),
            tickMillis = tickMillis,
        )
        val attachedHub = TransportHub.attach(session)
        session.start()

        return BookBootstrapResult(
            bookTitle = fixtureBook.nameWithoutExtension,
            sentences = sentences,
            highlightEmitter = emitter,
            starvationState = starvation,
            transportControls = NowPlayingTransportControls(TransportCommands(attachedHub)),
            transportHub = attachedHub,
            attachedTransportHub = attachedHub,
            session = session,
            sessionStarvationState = starvation,
        )
    }

    private fun textPipeline() = TextPipeline(
        extractor = EpubTextExtractor(EpubStructuralReader()),
        preCleanStage = PreCleanStage(),
        segmenter = SentenceSegmenter(maxSegmentCharacters = 800),
    )

    private fun nextAfter(sentences: List<SentenceRecord>): (SentenceIndex) -> SentenceIndex? {
        val nextIndexes = sentences.zipWithNext { current, next -> current.index to next.index }.toMap()
        return { index -> nextIndexes[index] }
    }

    private class InMemoryBookIdentityDao : BookIdentityDao {
        private val records = linkedMapOf<String, BookIdentityEntity>()

        override fun insertIfAbsent(entity: BookIdentityEntity): Long {
            if (records.containsKey(entity.hash)) return -1L
            records[entity.hash] = entity
            return records.size.toLong()
        }

        override fun get(hash: String): BookIdentityEntity? = records[hash]

        override fun count(): Int = records.size
    }

    companion object {
        private const val BOOTSTRAP_CHAPTER_ORDINAL = 5
        private const val BOOTSTRAP_LOOK_AHEAD_CHAPTERS = 1
        private const val MAX_LOOK_AHEAD_SECONDS = 12.0
        private const val INTENT_DEBOUNCE_MILLIS = 20L

        fun defaultFixtureBook(context: Context): File =
            File(externalMediaRoot(context), "fixtures/text/tom-sawyer.epub")

        fun defaultPiperModelRoot(context: Context): File =
            File(externalMediaRoot(context), "test-voices/piper")

        private fun externalMediaRoot(context: Context): File =
            context.externalMediaDirs.firstOrNull() ?: context.filesDir
    }
}
