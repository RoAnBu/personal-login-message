package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.configuration.IPlayerLogins
import org.bukkit.entity.Player

class LoginStatsReplacer(private val logins: IPlayerLogins): IPlaceholderReplacer {
    override fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean): String {
        var modMessage = message
        modMessage = getReplacedPlayerLogins(modMessage, player)
        modMessage = getReplacedTotalLogins(modMessage)
        modMessage = getReplacedUniquePlayers(modMessage)
        return modMessage
    }

    /**
     * Replaces %logins with the number of times the concerning player joined (The current join, too)
     * @param inputText the string which can contain %logins
     * @return the string with replaced %logins
     */
    private fun getReplacedPlayerLogins(inputText: String, player: Player): String {
        var text = inputText
        if (text.contains("%logins")) {
            text = text.replace("%logins", logins.getPlayerLogins(player).toString())
        }
        return text
    }

    /**
     * Replaces %totallogins with the total number of logins after started counting
     * @param inputText the string which can contain %totallogins
     * @return the string with replaced %totallogins
     */
    private fun getReplacedTotalLogins(inputText: String): String {
        var text = inputText
        if (text.contains("%totallogins")) {
            text = text.replace("%totallogins", logins.totalLogins.toString())
        }
        return text
    }

    /**
     * Replaces %uniqueplayers with the total number of unique logins after started counting
     * @param inputText the string which can contain %totallogins
     * @return the string with replaced %uniqueplayers
     */
    private fun getReplacedUniquePlayers(inputText: String): String {
        var text = inputText
        if (text.contains("%uniqueplayers")) {
            text = text.replace("%uniqueplayers", logins.uniquePlayerLogins.toString())
        }
        return text
    }
}