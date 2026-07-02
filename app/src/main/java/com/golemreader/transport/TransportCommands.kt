package com.golemreader.transport

class TransportCommands(
    private val hub: TransportHub = TransportHub.instance(),
) {
    fun play() {
        hub.play()
    }

    fun pause() {
        hub.pause()
    }

    fun resume() {
        hub.resume()
    }

    fun stop() {
        hub.stop()
    }
}
