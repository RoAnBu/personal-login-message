package com.gmail.fantasticskythrow.messages.data

data class MessageData @JvmOverloads constructor (val message: String, val channels: MutableList<String> = mutableListOf(), val type: SectionTypes? = null, val subType: SectionSubTypes? = null)