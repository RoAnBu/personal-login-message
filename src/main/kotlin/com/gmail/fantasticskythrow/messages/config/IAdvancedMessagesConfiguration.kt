package com.gmail.fantasticskythrow.messages.config

import com.gmail.fantasticskythrow.messages.data.MessageData

interface IAdvancedMessagesConfiguration {
    fun getNewPlayerMessage(playerName: String, groupName: String? = null): MessageData?
    fun getAllNewPlayerMessages(playerName: String, groupName: String? = null): List<MessageData>
    fun getJoinMessage(playerName: String, groupName: String? = null, timeSinceLastLoginMs: Long? = null): MessageData?
    fun getAllJoinMessages(playerName: String, groupName: String? = null, timeSinceLastLoginMs: Long? = null): List<MessageData>
    fun getQuitMessage(playerName: String, groupName: String? = null): MessageData?
    fun getAllQuitMessages(playerName: String, groupName: String? = null): List<MessageData>
    fun getWelcomeMessages(playerName: String, groupName: String?): List<String>
    fun getPublicMessages(playerName: String, groupName: String?): List<String>
}