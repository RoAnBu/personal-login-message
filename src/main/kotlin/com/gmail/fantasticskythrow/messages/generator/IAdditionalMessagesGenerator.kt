package com.gmail.fantasticskythrow.messages.generator

import org.bukkit.entity.Player

interface IAdditionalMessagesGenerator {
    fun getWelcomeMessagesForPlayer(player: Player): List<String>
    fun getPublicMessagesForPlayer(player: Player): List<String>
}