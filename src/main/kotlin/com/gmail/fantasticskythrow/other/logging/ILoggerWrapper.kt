package com.gmail.fantasticskythrow.other.logging

interface ILoggerWrapper {
    fun trace(message: String?)
    fun debug(message: String?)
    fun info(message: String?)
    fun warn(message: String?)
    fun error(message: String?)

    fun trace(thrown: Throwable?)
    fun debug(thrown: Throwable?)
    fun info(thrown: Throwable?)
    fun warn(thrown: Throwable?)
    fun error(thrown: Throwable?)

    fun trace(message: String?, thrown: Throwable?)
    fun debug(message: String?, thrown: Throwable?)
    fun info(message: String?, thrown: Throwable?)
    fun warn(message: String?, thrown: Throwable?)
    fun error(message: String?, thrown: Throwable?)
}