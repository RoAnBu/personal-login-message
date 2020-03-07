package com.gmail.fantasticskythrow.messages.replacer

import org.bukkit.entity.Player

interface IPlaceholderReplacer {
    fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean = false): String
}