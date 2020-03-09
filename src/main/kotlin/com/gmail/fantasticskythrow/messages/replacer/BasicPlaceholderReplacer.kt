package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.configuration.IAppConfiguration
import com.gmail.fantasticskythrow.configuration.TimeNames
import com.gmail.fantasticskythrow.messages.IPLMFile
import com.gmail.fantasticskythrow.messages.config.IWorldRenameConfig
import com.gmail.fantasticskythrow.other.IVanishManager
import com.gmail.fantasticskythrow.other.plugins.IPLMPluginConnector
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.apache.commons.lang.WordUtils
import org.bukkit.Server
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Damageable
import org.bukkit.entity.Player

class BasicPlaceholderReplacer(private val chat: Chat?,
                               private val permission: Permission?,
                               private val plmFile: IPLMFile,
                               private val vanishNoPacketManager: IVanishManager,
                               private val timeNames: TimeNames,
                               private val worldRenameConfig: IWorldRenameConfig? = null,
                               private val server: Server,
                               private val appConfiguration: IAppConfiguration,
                               private val pluginConnector: IPLMPluginConnector
): IPlaceholderReplacer {


    override fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean): String {
        var modMessage = message
        modMessage = getReplacedPlayername(modMessage, player)
        modMessage = getReplacedChatplayername(modMessage, player)
        modMessage = getReplacedNickname(modMessage, player)
        modMessage = getReplacedGroup(modMessage, player)
        modMessage = getReplacedWorld(modMessage, player)
        modMessage = getReplacedCountry(modMessage, player)
        modMessage = getReplacedTotalLogins(modMessage)
        modMessage = getReplacedUniquePlayers(modMessage)
        modMessage = getReplacedPlayerLogins(modMessage, player)
        modMessage = getReplacedOnlinePlayerNumber(modMessage, false)
        modMessage = getReplacedPrefix(modMessage, player)
        modMessage = getReplacedSuffix(modMessage, player)
        modMessage = getReplacedSlots(modMessage)
        modMessage = getReplacedLevels(modMessage, player)
        modMessage = getReplacedHealth(modMessage, player)
        modMessage = getReplacedIP(modMessage, player)
        modMessage = getReplacedGamemode(modMessage, player)
        modMessage = getReplacedFood(modMessage, player)
        modMessage = getReplacedTime(modMessage, player)
        return modMessage
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

    /**
     * Gets the Essentials nick name like "~Sam" if Essentials is installed and using the nick name is activated in the config
     * @param player The concerning player
     * @return The nick name if available (if the player doesn't have one it returns the normal name) or null in case of disabled Essentials
     */
    private fun getEssentialsNick(player: Player): String? {
        var nickName: String? = null
        val essentials = pluginConnector.essentials
        if (essentials != null && appConfiguration.useEssentialsNick) {
            try {
                nickName = essentials.userMap.getUser(player.name).nickname
                if (nickName != null && nickName != player.name) {
                    nickName = essentials.settings.nicknamePrefix + nickName
                }
            } catch (e: Error) {
                logger.error("Could not connect to Essentials! Please use a newer version of Essentials or disable the " +
                        "Essentials connection")
            }
        }
        return nickName
    }

    /**
     * Simple replacement of %playername
     * @param text the text which can contain %name
     * @param player the player whose name is relevant
     * @return the replaced string
     */
    private fun getReplacedPlayername(text: String, player: Player): String {
        return text.replace("%playername", player.name)
    }

    /**
     * Replaces %chatplayername with the name (prefix + name + suffix) taken from Vault|Chat
     * @param text the string which can contain %chatplayername
     * @param player the concerning player
     * @return the replaced string if chat is available. Otherwise it will return the normal playername. %chatplayername won't exist after this.
     */
    internal fun getReplacedChatplayername(text: String, player: Player): String {
        return if (chat != null && text.contains("%chatplayername")) {
            var name = getEssentialsNick(player)
            if (name == null) //Use the normal name if no essentials name was available
                name = player.name
            text.replace("%chatplayername", (chat.getPlayerPrefix(player) + name + chat.getPlayerSuffix(player)))
        } else if (chat == null && text.contains("%chatplayername")) {
            println("[PLM] PLM was not able to identify a chat format for this player!")
            println("[PLM] Possible reason: No vault compatible chat plugin is available!")
            getReplacedPlayername(text.replace("%chatplayername", "%playername"), player)
        } else {
            text
        }
    }

    /**
     * Replaces %nickname with the standard name or Essentials nick name (no prefix and suffix!)
     * @param text the string which can contain %nickname
     * @param player the concerning player
     * @return the replaced string
     */
    private fun getReplacedNickname(text: String, player: Player): String {
        return if (text.contains("%nickname")) {
            var name = getEssentialsNick(player)
            if (name == null) { //Use the normal name if no essentials name was available
                name = player.name
            }
            text.replace("%nickname", name)
        } else {
            text
        }
    }

    /**
     * Replaces %group with the first group found if a permissions plugin was hooked
     * @param text the string which can contain %group
     * @param player the concerning player
     * @return replaced %group, "unknown" if permission is null
     */
    private fun getReplacedGroup(text: String, player: Player): String {
        return if (text.contains("%group") && permission != null) {
            text.replace("%group", permission.getPlayerGroups(player)[0])
        } else if (text.contains("%group") && permission == null) {
            text.replace("%group", "unknown group")
        } else {
            text
        }
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
     * Replaces %country with the country name the player joined from. Changed country names come from plmfile
     * @param inputText the string which can contain %country
     * @param player the player who joined (getting is address)
     * @return replaced %country if possible. Otherwise it will return unknown
     */
    private fun getReplacedCountry(inputText: String, player: Player): String {
        var text = inputText
        return if (text.contains("%country")) {
            val geoIP = pluginConnector.ipLookup
            if (geoIP != null) {
                var country: String
                country = plmFile.getAlternateNameForCountry(geoIP.getCountry(player.address!!.address).name)
                if (country.equals("N/A", ignoreCase = true)) {
                    country = "local network"
                }
                text = text.replace("%country", country)
                text
            } else {
                logger.warn("You used %country but GeoIPTools is not installed or no database is initialized")
                logger.warn("Use /geoupdate if it's installed")
                text = text.replace("%country", "unknown")
                text
            }
        } else {
            text
        }
    }

    /**
     * Replaces %logins with the number of times the concerning player joined (The current join, too)
     * @param inputText the string which can contain %logins
     * @return the string with replaced %logins
     */
    private fun getReplacedPlayerLogins(inputText: String, player: Player): String {
        var text = inputText
        if (text.contains("%logins")) {
            text = text.replace("%logins", plmFile.getPlayerLogins(player).toString())
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
            text = text.replace("%totallogins", plmFile.totalLogins.toString())
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
            text = text.replace("%uniqueplayers", plmFile.uniquePlayerLogins.toString())
        }
        return text
    }

    /**
     * Replaces %onlineplayers with the number of online players (Apart from vanished ones)
     * @param inputText The string which can contain %onlineplayers
     * @return
     */
    private fun getReplacedOnlinePlayerNumber(inputText: String, isQuitting: Boolean): String {
        var text = inputText
        if (text.contains("%onlineplayers")) {
            val playerList = server.onlinePlayers
            var number = 0
            for (p in playerList) {
                if (!vanishNoPacketManager.isVanished(p.name)) {
                    number++
                }
            }
            if (isQuitting) {
                number--
            }
            text = text.replace("%onlineplayers", number.toString())
        }
        return text
    }

    /**
     * Replaces the %prefix placeholder with the vault prefix if available. In case of Vault is not enabled it replaces %prefix by "" and outputs a warning
     * @param text The string which could contain %prefix
     * @param player The concerning player
     * @return The modified string
     */
    private fun getReplacedPrefix(text: String, player: Player): String {
        return if (chat != null && text.contains("%prefix")) {
            val prefix = chat.getPlayerPrefix(player)
            text.replace("%prefix", prefix)
        } else if (chat == null && text.contains("%prefix")) {
            println("[PLM] PLM was not able to identify a prefix for this player!")
            text.replace("%prefix", "")
        } else {
            text.replace("%prefix", "")
        }
    }

    /**
     * Replaces the %suffix placeholder with the vault prefix if available. In case of Vault is not enabled it replaces %suffix by "" and outputs a warning
     * @param text The string which could contain %suffix
     * @param player The concerning player
     * @return The modified string
     */
    private fun getReplacedSuffix(text: String, player: Player): String {
        return if (chat != null && text.contains("%suffix")) {
            val suffix = chat.getPlayerSuffix(player)
            text.replace("%suffix", suffix)
        } else if (chat == null && text.contains("%suffix")) {
            println("[PLM] PLM was not able to identify a suffix for this player!")
            text.replace("%suffix", "")
        } else {
            text.replace("%suffix", "")
        }
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
     * Replaces %IP by the player's address. The / in front of it will be deleted
     * @param inputText The text which could contain %IP
     * @param player The concerning player
     * @return The modified string
     */
    private fun getReplacedIP(inputText: String, player: Player): String {
        var text = inputText
        if (text.contains("%IP")) {
            text = text.replace("%IP", player.address.toString().replace("/".toRegex(), ""))
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

    private fun getReplacedTime(inputText: String, player: Player): String {
        var message = inputText
        if (!message.contains("%time")) {
            return message
        }
        val timeSinceLastLoginMs: Long
        if (plmFile.getLastLogin(player) == 0L) {
            return message.replace("%time", timeNames.noLastLogin)
        } else {
            timeSinceLastLoginMs = System.currentTimeMillis() - plmFile.getLastLogin(player)
        }
        // Less than 1 minute and is not 0
        if (timeSinceLastLoginMs < 60000L && timeSinceLastLoginMs != 0L) {
            val a = timeSinceLastLoginMs / 1000L
            message = if (a == 1L) {
                message.replace("%time", "$a ${timeNames.second}")
            } else {
                message.replace("%time", "$a ${timeNames.seconds}")
            }
        }
        // More than 1 minute and less than 1 hour
        if (timeSinceLastLoginMs in 60000L..3599999) {
            val a = timeSinceLastLoginMs / 60000L
            message = if (a == 1L) {
                message.replace("%time", "$a ${timeNames.minute}")
            } else {
                message.replace("%time", "$a ${timeNames.minutes}")
            }
        }
        // More than 1 hour and less than 1 day
        if (timeSinceLastLoginMs in 3600000L..86399999) {
            var a = timeSinceLastLoginMs / 60000L
            val rest = a % 60
            a /= 60
            message = if (a == 1L && rest == 0L) {
                message.replace("%time", "$a ${timeNames.hour}")
            } else if (rest == 0L) {
                message.replace("%time", "$a ${timeNames.hours}")
            } else if (a == 1L && rest == 1L) {
                message.replace("%time", "$a ${timeNames.hour} $rest ${timeNames.minute}")
            } else if (a == 1L) {
                message.replace("%time", "$a ${timeNames.hour} $rest ${timeNames.minutes}")
            } else if (rest == 1L) {
                message.replace("%time", "$a ${timeNames.hours} $rest ${timeNames.minute}")
            } else {
                message.replace("%time", "$a ${timeNames.hours} $rest ${timeNames.minutes}")
            }
        }
        // More than 1 day and less than 10 days
        if (timeSinceLastLoginMs in 86400000L..863999999) {
            var a = timeSinceLastLoginMs / 3600000L
            val rest = a % 24
            a /= 24
            message = if (a == 1L && rest == 0L) {
                message.replace("%time", "$a ${timeNames.day}")
            } else if (rest == 0L) {
                message.replace("%time", "$a ${timeNames.days}")
            } else if (a == 1L && rest == 1L) {
                message.replace("%time", "$a ${timeNames.day} $rest ${timeNames.hour}")
            } else if (a == 1L) {
                message.replace("%time", "$a ${timeNames.day} $rest ${timeNames.hours}")
            } else if (rest == 1L) {
                message.replace("%time", "$a ${timeNames.days} $rest ${timeNames.hour}")
            } else {
                message.replace("%time", "$a ${timeNames.days} $rest ${timeNames.hours}")
            }
        }
        // More than 10 days and less than 30 days
        if (timeSinceLastLoginMs in 864000000L..2591999999) {
            val a = timeSinceLastLoginMs / 86400000L
            message = if (a == 1L) {
                message.replace("%time", "$a ${timeNames.day}")
            } else {
                message.replace("%time", "$a ${timeNames.days}")
            }
        }
        // More than 1 month (30 days)
        if (timeSinceLastLoginMs >= 2592000000L) {
            val a = timeSinceLastLoginMs / 2592000000L
            message = if (a == 1L) {
                message.replace("%time", "$a ${timeNames.month}")
            } else {
                message.replace("%time", "$a ${timeNames.months}")
            }
        }
        return message
    }

    companion object {
        private val logger = PLM.logger()
    }
}