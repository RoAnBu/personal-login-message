package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.messages.config.IWorldRenameConfig
import org.apache.commons.lang.WordUtils
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Damageable
import org.bukkit.entity.Player

class PlayerStatsReplacer(private val worldRenameConfig: IWorldRenameConfig? = null): IPlaceholderReplacer {
    override fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean): String {
        var modMessage = message
        modMessage = getReplacedLevels(modMessage, player)
        modMessage = getReplacedFood(modMessage, player)
        modMessage = getReplacedGamemode(modMessage, player)
        modMessage = getReplacedHealth(modMessage, player)
        modMessage = getReplacedWorld(modMessage, player)
        return modMessage
    }

    /**
     * Replaces %levels by the number of levels
     * @param inputText The string which could contain %levels
     * @param player The concerning player
     * @return The modified string
     */
    private fun getReplacedLevels(inputText: String, player: Player): String {
        var text = inputText
        if (text.contains("%levels")) {
            text = text.replace("%levels", player.level.toString())
        }
        return text
    }

    /**
     * Replaces %health and %comparedHealth by the concerning values. Compared health looks like this "health/maximum health"
     * @param inputText The text which could contain %health or %comparedHealth
     * @param player The concerning player
     * @return The modified string
     */
    private fun getReplacedHealth(inputText: String, player: Player): String {
        var text = inputText
        if (text.contains("%health")) {
            val damageable = player as Damageable

            text = text.replace("%health", damageable.health.toString())
        }
        if (text.contains("%comparedHealth")) {
            val damageable = player as Damageable
            text = text.replace("%comparedHealth", damageable.health.toString() + "/" +
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).toString())
        }
        return text
    }

    /**
     * Replaces %gamemode by the current gamemode. Just using the English expressions like "Survival", "Creative" and "Adventure".
     * @param inputText The text which could contain %gamemode
     * @param player The concerning player
     * @return The modified string
     */
    private fun getReplacedGamemode(inputText: String, player: Player): String {
        var text = inputText
        if (text.contains("%gamemode")) {
            text = text.replace("%gamemode", WordUtils.capitalize(player.gameMode.toString().toLowerCase()))
        }
        return text
    }

    /**
     * Replaces %food by the current food level
     * @param inputText The text which could contain %food
     * @param player The concerning player
     * @return The modified string
     */
    private fun getReplacedFood(inputText: String, player: Player): String {
        var text = inputText
        if (text.contains("%food")) {
            text = text.replace("%food", player.foodLevel.toString())
        }
        return text
    }

    /**
     * Replaces %world or %World with the world the player joined in
     * @param inputText - the string which can contain %world/%World
     * @param player - the player who joined/left
     * @return
     */
    private fun getReplacedWorld(inputText: String, player: Player): String {
        if (!inputText.contains("%world|%World".toRegex())) {
            return inputText
        }
        var text = inputText
        val worldName = player.world.name
        if (worldRenameConfig != null) {
            worldRenameConfig.getRenamedWorld(worldName)?.let { text = text.replace("%world|%World", it) }
        } else {
            text = text.replace("%world", worldName)
            text = text.replace("%World", getCapitalWord(worldName))
        }
        return text
    }

    /**
     * Makes the first letter a capital letter and removes '_'
     * @param inputWord the string you want to be transformed
     * @return the corrected string
     */
    private fun getCapitalWord(inputWord: String): String {
        var word = inputWord
        var b = ""
        b += word[0]
        word = word.replaceFirst(b, b.toUpperCase())
        if (word.contains("_")) {
            word = word.replace("_", " ")
        }
        return word
    }
}