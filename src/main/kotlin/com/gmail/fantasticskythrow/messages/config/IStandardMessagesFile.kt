package com.gmail.fantasticskythrow.messages.config

interface IStandardMessagesFile {
    fun reload()

    val joinMessage: String
    val quitMessage: String
}