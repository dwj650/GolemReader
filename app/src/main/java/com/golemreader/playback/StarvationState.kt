package com.golemreader.playback

import com.golemreader.text.SentenceIndex

class StarvationState {
    var isBuffering: Boolean = false
        private set

    var latestIntentDuringHold: SentenceIndex? = null
        private set

    fun onConsumerOutrunsProducer() {
        isBuffering = true
    }

    fun recordIntentDuringHold(sentenceIndex: SentenceIndex) {
        latestIntentDuringHold = sentenceIndex
    }

    fun onAudioRefilled() {
        isBuffering = false
    }
}
