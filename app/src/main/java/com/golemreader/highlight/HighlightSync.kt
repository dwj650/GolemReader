package com.golemreader.highlight

import com.golemreader.text.SentenceIndex

class HighlightSync(
    private val mapper: HighlightIndexMapper,
    private val emitter: HighlightStateEmitter,
) {
    fun snapTo(target: SentenceIndex) {
        mapper.targetFor(target)?.let { emitter.emit(it) }
    }
}
