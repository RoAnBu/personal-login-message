package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.other.IVanishManager
import org.bukkit.Server
import org.bukkit.entity.Player

class ServerStatsReplacer(private val server: Server, private val vanishManager: IVanishManager): IPlaceholderReplacer {

    override fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean): String {
        var modMessage = message
        modMessage = getReplacedOnlinePlayerNumber(modMessage, player, isQuitting)
        modMessage = getReplacedSlots(modMessage)
        return modMessage
    }

    /**
     * Replaces %onlineplayers with the number of online players (Apart from vanished ones)
     * @param inputText The string which can contain %onlineplayers
     * @return
     */
    private fun getReplacedOnlinePlayerNumber(inputText: String, player: Player, isQuitting: Boolean): String {
        var text = inputText
        if (text.contains("%onlineplayers")) {
            val playerCount = server.onlinePlayers
                    .filter { !vanishManager.isVanished(it.name) && !( isQuitting && it == player )  }
                    .size
            text = text.replace("%onlineplayers", playerCount.toString())
        }
        return text
    }

    /**
     * Replaces %slots by the number of slots
     * @param inputText The string which could contain %slots
     * @return The modified string
     */
    private fun getReplacedSlots(inputText: String): String {
        var text = inputText
        if (text.contains("%slots")) {
            text = text.replace("%slots", server.maxPlayers.toString())
        }
        return text
    }
}