package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.other.IVanishManager
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.Server
import org.bukkit.entity.Player

class AdvancedPlayerNameGroupReplacer(private val chat: Chat?,
                                      private val permission: Permission?,
                                      private val server: Server,
                                      private val vanishManager: IVanishManager,
                                      private val playerNameGroupReplacer: PlayerNameGroupReplacer): IPlaceholderReplacer {

    private val logger = PLM.logger()

    override fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean): String {
        var modMessage = message
        modMessage = getReplacedGroupChatPlayerList(modMessage, player)
        modMessage = getReplacedGroupPlayerList(modMessage, player)
        modMessage = getReplacedChatplayerList(modMessage)
        modMessage = getReplacedPlayerList(modMessage)
        return modMessage
    }

    /**
     * Replaces %groupchatplayerlist with the list of players who are currently online in the player's group.
     * The player names will be formatted with Vault's Chat. Note that this doesn't make sense in every case because all names can have the same
     * format.
     * @param text the string which can contain %groupchatplayerlist
     * @param player the concerning player to get the first group
     * @return the replaced %groupchatplayerlist if permission and chat is available. Otherwise -> '&4ERROR'
     */
    private fun getReplacedGroupChatPlayerList(text: String, player: Player): String {
        return if (text.contains("%groupchatplayerlist")) {
            if (permission != null && chat != null) {
                val replacement = server.onlinePlayers
                        .mapNotNull {
                            if(permission.getPrimaryGroup(it) == permission.getPrimaryGroup(player) && !vanishManager.isVanished(it))
                                playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", it)
                            else
                                null
                        }
                        .sorted()
                        .joinToString()
                text.replace("%groupchatplayerlist", replacement)
            } else {
                text.replace("%groupchatplayerlist", "&4ERROR")
            }
        } else {
            text
        }
    }

    /**
     * Replaces %chatplayerlist with the list of players who are currently online in the chatplayername format. Vanished players are hidden
     * @param text the string which can contain %chatplayerlist
     * @return the replaced %chatplayerlist or %playerlist if chat is null
     */
    private fun getReplacedChatplayerList(text: String): String {
        return if (text.contains("chatplayerlist")) {
            if (chat != null) {
                val replacement = server.onlinePlayers
                        .mapNotNull { if(!vanishManager.isVanished(it))
                            playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", it)
                        else null
                        }
                        .sorted()
                        .joinToString()
                text.replace("%chatplayerlist", replacement)
            } else {
                getReplacedPlayerList(text.replace("%chatplayerlist", "%playerlist"))
            }
        } else {
            text
        }
    }

    /**
     * Replaces %groupplayerlist with the list of players who are currently online in the same group like the concerning player.
     * Vanished players are hidden
     * @param text the string which can contain %groupplayerlist
     * @param player the concerning player to get the first group
     * @return the replaced %groupplayerlist if a group was found. Otherwise it will return "&4ERROR"
     */
    private fun getReplacedGroupPlayerList(text: String, player: Player): String {
        return if (text.contains("%groupplayerlist")) {
            if (permission != null) {
                val replacement = server.onlinePlayers
                        .filter { permission.getPrimaryGroup(it) == permission.getPrimaryGroup(player)
                                && !vanishManager.isVanished(it)}
                        .sortedBy { it.name }
                        .joinToString { it.name }
                text.replace("%groupplayerlist", replacement)
            } else {
                text.replace("%groupplayerlist", "&4ERROR")
            }
        } else {
            text
        }
    }

    /**
     * Replaces %playerlist with the list of players who are currently online. Vanished players are hidden
     * @param text the string which can contain %playerlist
     * @return the replaced %playerlist
     */
    private fun getReplacedPlayerList(text: String): String {
        return if (text.contains("%playerlist")) {
            val replacement = server.onlinePlayers
                    .filter { !vanishManager.isVanished(it) }
                    .sortedBy { it.name }
                    .joinToString { it.name }
            text.replace("%playerlist", replacement)
        } else {
            text
        }
    }
}