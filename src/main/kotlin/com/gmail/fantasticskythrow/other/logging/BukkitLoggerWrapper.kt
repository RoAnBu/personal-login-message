package com.gmail.fantasticskythrow.other.logging

import java.util.logging.Level
import java.util.logging.Logger

class BukkitLoggerWrapper(var logger: Logger?) : ILoggerWrapper {

    override fun trace(message: String?) {
        sendLogMessage(Level.FINER, message)
    }

    override fun trace(thrown: Throwable?) {
        sendLogMessage(Level.FINER, null, thrown)
    }

    override fun trace(message: String?, thrown: Throwable?) {
        sendLogMessage(Level.FINER, message, thrown)
    }

    override fun debug(message: String?) {
        sendLogMessage(Level.FINE, message)
    }

    override fun debug(thrown: Throwable?) {
        sendLogMessage(Level.FINE, null, thrown)
    }

    override fun debug(message: String?, thrown: Throwable?) {
        sendLogMessage(Level.FINE, message, thrown)
    }

    override fun info(message: String?) {
        sendLogMessage(Level.INFO, message)
    }

    override fun info(thrown: Throwable?) {
        sendLogMessage(Level.INFO, null, thrown)
    }

    override fun info(message: String?, thrown: Throwable?) {
        sendLogMessage(Level.INFO, message, thrown)
    }

    override fun warn(message: String?) {
        sendLogMessage(Level.WARNING, message)
    }

    override fun warn(thrown: Throwable?) {
        sendLogMessage(Level.WARNING, null, thrown)
    }

    override fun warn(message: String?, thrown: Throwable?) {
        sendLogMessage(Level.WARNING, message, thrown)
    }

    override fun error(message: String?) {
        sendLogMessage(Level.SEVERE, message)
    }

    override fun error(thrown: Throwable?) {
        sendLogMessage(Level.SEVERE, null, thrown)
    }

    override fun error(message: String?, thrown: Throwable?) {
        sendLogMessage(Level.SEVERE, message, thrown)
    }

    private fun sendLogMessage(logLevel: Level, message: String?) {
        val currentLogger = logger
        if (currentLogger != null) {
            currentLogger.log(logLevel, message)
        } else {
            println(message)
        }
    }

    private fun sendLogMessage(logLevel: Level, message: String?, thrown: Throwable?) {
        val currentLogger = logger
        if (currentLogger != null) {
            currentLogger.log(logLevel, message, thrown)
        } else {
            println(message)
            thrown?.printStackTrace()
        }
    }
}