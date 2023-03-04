package com.github.tarcv.testingteam.surveyor

object Logger {
    fun debug(message: String) {
        onDebugMessage?.invoke(message)
    }

    fun info(message: String) {
        onInfoMessage?.invoke(message)
    }

    var onDebugMessage: ((String) -> Unit)?
        get() = synchronized(lock) { debugConsumer }
        set(value) = synchronized(lock) {
            debugConsumer = value
        }
    var onInfoMessage: ((String) -> Unit)?
        get() = synchronized(lock) { infoConsumer }
        set(value) = synchronized(lock) {
            infoConsumer = value
        }

    private val lock = Any()
    private var debugConsumer: ((String) -> Unit)? = null
    private var infoConsumer: ((String) -> Unit)? = null
}