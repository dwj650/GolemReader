package com.golemreader.highlight

import com.golemreader.text.ClauseTag
import com.golemreader.text.SegmentType
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import com.golemreader.voice.SynthesizedAudio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HighlightCoreTest {
    @Test
    fun clockUsesRenderedSampleDuration() {
        val clock = HighlightClock(nowMillis = { 1_000L })

        val timing = clock.segmentStarted(audio(sentence = 2, sampleCount = 12_000, sampleRateHz = 24_000))

        assertEquals(index(2), timing.sentenceIndex)
        assertEquals(1_000L, timing.startedAtMillis)
        assertEquals(0.5, timing.durationSeconds, 0.0001)
        assertEquals(1_500L, timing.expectedEndMillis)
    }

    @Test
    fun mapperResolvesClauseTagFromSharedSentenceRecord() {
        val mapper = HighlightIndexMapper(
            listOf(
                sentence(0),
                sentence(
                    sentence = 1,
                    segmentType = SegmentType.ClauseSubSplit,
                    clauseTag = ClauseTag(parentSentenceOrdinal = 1, clauseOrdinal = 0),
                ),
                sentence(
                    sentence = 2,
                    segmentType = SegmentType.ClauseSubSplit,
                    clauseTag = ClauseTag(parentSentenceOrdinal = 1, clauseOrdinal = 1),
                ),
            ),
        )

        val firstClause = mapper.targetFor(index(1))
        val secondClause = mapper.targetFor(index(2))

        assertEquals(index(1), firstClause?.sentenceIndex)
        assertEquals(ClauseTag(parentSentenceOrdinal = 1, clauseOrdinal = 0), firstClause?.clauseTag)
        assertEquals(index(2), secondClause?.sentenceIndex)
        assertEquals(ClauseTag(parentSentenceOrdinal = 1, clauseOrdinal = 1), secondClause?.clauseTag)
        assertNull(mapper.targetFor(index(99)))
    }

    @Test
    fun emitterPublishesReadableStateWithGlowParameters() {
        val emitter = HighlightStateEmitter()
        val mapper = HighlightIndexMapper(listOf(sentence(3)))
        val timing = HighlightClock(nowMillis = { 2_000L })
            .segmentStarted(audio(sentence = 3, sampleCount = 24_000, sampleRateHz = 24_000))

        emitter.emit(requireNotNull(mapper.targetFor(index(3))), timing)

        val state = requireNotNull(emitter.currentState())
        assertEquals(index(3), state.sentenceIndex)
        assertEquals(ClauseTag(parentSentenceOrdinal = 3, clauseOrdinal = 0), state.clauseTag)
        assertEquals(HighlightGlowParameters(), state.glowParameters)
        assertEquals(1.0, requireNotNull(state.durationSeconds), 0.0001)

        val adjusted = HighlightGlowParameters(size = 1.25f, contrast = 1.4f, fadeMillis = 90L)
        emitter.updateGlowParameters(adjusted)
        emitter.emit(requireNotNull(mapper.targetFor(index(3))), timing)

        assertEquals(adjusted, emitter.currentState()?.glowParameters)
    }

    @Test
    fun syncSnapsEmitterToAbortTarget() {
        val emitter = HighlightStateEmitter()
        val sync = HighlightSync(
            mapper = HighlightIndexMapper(listOf(sentence(4), sentence(5))),
            emitter = emitter,
        )

        sync.snapTo(index(5))

        val state = requireNotNull(emitter.currentState())
        assertEquals(index(5), state.sentenceIndex)
        assertEquals(ClauseTag(parentSentenceOrdinal = 5, clauseOrdinal = 0), state.clauseTag)
        assertNull(state.durationSeconds)
    }

    private fun sentence(
        sentence: Int,
        segmentType: SegmentType = SegmentType.SentenceTerminal,
        clauseTag: ClauseTag = ClauseTag(parentSentenceOrdinal = sentence, clauseOrdinal = 0),
    ) = SentenceRecord(
        index = index(sentence),
        display = "display $sentence",
        spoken = "spoken $sentence",
        segmentType = segmentType,
        clauseTag = clauseTag,
    )

    private fun audio(
        sentence: Int,
        sampleCount: Int,
        sampleRateHz: Int,
    ) = SynthesizedAudio(
        sentenceIndex = index(sentence),
        samples = FloatArray(sampleCount) { 0.1f },
        sampleRateHz = sampleRateHz,
        synthesizeToFirstSampleMillis = 1,
    )

    private fun index(sentence: Int) =
        SentenceIndex(bookHash = "book", chapterOrdinal = 5, sentenceOrdinal = sentence)
}
