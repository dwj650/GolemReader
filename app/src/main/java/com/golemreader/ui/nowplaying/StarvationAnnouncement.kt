package com.golemreader.ui.nowplaying

const val STARVATION_ANNOUNCE_HOLD_MILLIS = 500L
const val CATCHING_UP_ANNOUNCEMENT = "Catching up"

class StarvationAnnouncement {
    private var announcedForCurrentHold = false

    fun update(isBuffering: Boolean, heldMillis: Long): String? {
        if (!isBuffering) {
            announcedForCurrentHold = false
            return null
        }
        if (announcedForCurrentHold || heldMillis < STARVATION_ANNOUNCE_HOLD_MILLIS) return null
        announcedForCurrentHold = true
        return CATCHING_UP_ANNOUNCEMENT
    }
}
