package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.configuration.TimeNames
import com.gmail.fantasticskythrow.messages.PLMFile
import com.gmail.fantasticskythrow.other.VanishNoPacketManager
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player

class FullPlaceholderReplacer(
        private val plugin: PLM,
        private val chat: Chat?,
        private val permission: Permission?,
        plmFile: PLMFile,
        private val vanishNoPacketManager: VanishNoPacketManager,
        timeNames: TimeNames
) : IPlaceholderReplacer {

    private val basicPlaceholderReplacer = BasicPlaceholderReplacer(plugin, chat, permission, plmFile, vanishNoPacketManager, timeNames, server = plugin.server)

    override fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean): String {
        var modMessage = message
        modMessage = basicPlaceholderReplacer.replacePlaceholders(modMessage, player)
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
                var m = ""
                val playerList = plugin.server.onlinePlayers
                        .toTypedArray()
                for (i in 0 until playerList.size - 1) {
                    val p = playerList[i]
                    if (permission.getPlayerGroups(p)[0] == permission.getPlayerGroups(player)[0] && !vanishNoPacketManager.isVanished(p.name)) {
                        m = m + basicPlaceholderReplacer.getReplacedChatplayername("%chatplayername", p) + ", "
                    }
                }
                val p = playerList[playerList.size - 1]
                if (permission.getPlayerGroups(p)[0] == permission.getPlayerGroups(player)[0] && !vanishNoPacketManager.isVanished(p.name)) {
                    m += (chat.getPlayerPrefix(p) + p.name + chat.getPlayerSuffix(p))
                } else {
                    val s1 = StringBuffer()
                    s1.append(m)
                    m = s1.reverse().toString()
                    m = m.replaceFirst(" ,".toRegex(), "")
                    val s2 = StringBuffer()
                    s2.append(m)
                    m = s2.reverse().toString()
                }
                text.replace("%groupchatplayerlist".toRegex(), m)
            } else {
                text.replace("%groupchatplayerlist".toRegex(), "&4ERROR")
            }
        } else {
            text
        }
    }


    /**
     * Replaces %chatplayerlist with the list of players who are currently online in the chatplayername format. Vanished players are hidden
     * @param text the string which can contain %chatplayerlist
     * @param chat the Chat object
     * @param vnpHandler the VanishNoPacketManager which provides isVanished()
     * @param server the server taken from the main plugin (PLM - JavaPlugin)
     * @return the replaced %chatplayerlist or %playerlist if chat is null
     */
    private fun getReplacedChatplayerList(text: String): String {
        return if (text.contains("chatplayerlist")) {
            if (chat != null) {
                var m = ""
                val playerlist = plugin.server.onlinePlayers
                        .toTypedArray()
                for (i in 0 until playerlist.size - 1) {
                    val p = playerlist[i]
                    if (!vanishNoPacketManager.isVanished(p.name)) {
                        m = m + basicPlaceholderReplacer.getReplacedChatplayername("%chatplayername", p) + ", "
                    }
                }
                val p = playerlist[playerlist.size - 1]
                if (!vanishNoPacketManager.isVanished(p.name)) {
                    m += (chat.getPlayerPrefix(p) + p.name + chat.getPlayerSuffix(p))
                } else {
                    val s1 = StringBuffer()
                    s1.append(m)
                    m = s1.reverse().toString()
                    m = m.replaceFirst(" ,".toRegex(), "")
                    val s2 = StringBuffer()
                    s2.append(m)
                    m = s2.reverse().toString()
                }
                text.replace("%chatplayerlist".toRegex(), m)
            } else {
                getReplacedPlayerList(text.replace("%chatplayerlist".toRegex(), "%playerlist"))
            }
        } else {
            text
        }
    }


    /**
     * Replaces %groupplayerlist with the list of players who are currently online in the same group like the concerning player.
     * Vanished players are hidden
     * @param text the string which can contain %groupplayerlist
     * @param vnpHandler the VanishNoPacketManager which provides isVanished()
     * @param permission the Permission object taken from Vault
     * @param server the server taken from the main plugin (PLM - JavaPlugin)
     * @param player the concerning player to get the first group
     * @return the replaced %groupplayerlist if a group was found. Otherwise it will return "&4ERROR"
     */
    private fun getReplacedGroupPlayerList(text: String, player: Player): String {
        return if (text.contains("%groupplayerlist")) {
            if (permission != null) {
                var m = ""
                val playerList = plugin.server.onlinePlayers
                        .toTypedArray()
                for (i in 0 until playerList.size - 1) {
                    val p = playerList[i]
                    if (permission.getPlayerGroups(p)[0] == permission.getPlayerGroups(player)[0] && !vanishNoPacketManager.isVanished(p.name)) {
                        m = m + p.name + ", "
                    }
                }
                val p = playerList[playerList.size - 1]
                if (permission.getPlayerGroups(p)[0] == permission.getPlayerGroups(player)[0] && !vanishNoPacketManager.isVanished(p.name)) {
                    m += p.name
                } else {
                    val s1 = StringBuffer()
                    s1.append(m)
                    m = s1.reverse().toString()
                    m = m.replaceFirst(" ,".toRegex(), "")
                    val s2 = StringBuffer()
                    s2.append(m)
                    m = s2.reverse().toString()
                }
                text.replace("%groupplayerlist".toRegex(), m)
            } else {
                text.replace("%groupplayerlist".toRegex(), "&4ERROR")
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
            var m = ""
            val playerList = plugin.server.onlinePlayers
                    .toTypedArray()
            for (i in 0 until playerList.size - 1) {
                val p = playerList[i]
                if (!vanishNoPacketManager.isVanished(p.name)) {
                    m = m + p.name + ", "
                }
            }
            val p = playerList[playerList.size - 1]
            if (!vanishNoPacketManager.isVanished(p.name)) {
                m += p.name
            } else {
                val s1 = StringBuffer()
                s1.append(m)
                m = s1.reverse().toString()
                m = m.replaceFirst(" ,".toRegex(), "")
                val s2 = StringBuffer()
                s2.append(m)
                m = s2.reverse().toString()
            }
            text.replace("%playerlist".toRegex(), m)
        } else {
            text
        }
    }

}