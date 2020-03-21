package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.configuration.IAppConfiguration
import com.gmail.fantasticskythrow.other.plugins.IPLMPluginConnector
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player
import java.lang.UnsupportedOperationException

class PlayerNameGroupReplacer(private val chat: Chat?, private val permission: Permission?,
                              private val pluginConnector: IPLMPluginConnector,
                              private val appConfiguration: IAppConfiguration): IPlaceholderReplacer {

    private val logger = PLM.logger()

    override fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean): String {
        var modMessage = message
        modMessage = getReplacedPlayername(modMessage, player)
        modMessage = getReplacedChatplayername(modMessage, player)
        modMessage = getReplacedNickname(modMessage, player)
        modMessage = getReplacedGroup(modMessage, player)
        modMessage = getReplacedPrefix(modMessage, player)
        modMessage = getReplacedSuffix(modMessage, player)
        return modMessage
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
                nickName = essentials.userMap.getUser(player.uniqueId).nickname
                if (nickName != null && nickName != player.name) {
                    nickName = essentials.settings.nicknamePrefix + nickName
                }
            } catch (e: Error) {
                logger.error(e)
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
            val name: String = getEssentialsNick(player) ?: player.name
            text.replace("%chatplayername", (chat.getPlayerPrefix(player) + name + chat.getPlayerSuffix(player)))
        } else if (chat == null && text.contains("%chatplayername")) {
            logger.info("PLM was not able to identify a chat format for player ${player.name}!")
            logger.info("Possible reason: No vault compatible chat plugin is available!")
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
            text.replace("%nickname", name) // IDE shows an error probably due to a bug in
            // the intellij kotlin plugin, version 1.3.70-release-IJ2019.3-1
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
            val group: String? = try {
                permission.getPrimaryGroup(player)
            } catch (e: UnsupportedOperationException) {
                logger.trace(e)
                logger.trace("Could not get primary group for player, unsupported operation")
                null
            }
            if (group != null) {
                text.replace("%group", group)
            } else {
                text.replace("%group", "no group")
            }
        } else if (text.contains("%group") && permission == null) {
            text.replace("%group", "unknown group")
        } else {
            text
        }
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
            logger.info("PLM was not able to identify a prefix for player ${player.name}!")
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
            logger.info("PLM was not able to identify a suffix for player ${player.name}!")
            text.replace("%suffix", "")
        } else {
            text.replace("%suffix", "")
        }
    }
}