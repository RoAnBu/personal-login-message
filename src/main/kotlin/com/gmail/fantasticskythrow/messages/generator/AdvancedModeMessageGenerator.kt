package com.gmail.fantasticskythrow.messages.generator

import com.gmail.fantasticskythrow.configuration.IAdvancedGeneratorAppConfiguration
import com.gmail.fantasticskythrow.messages.IPLMFile
import com.gmail.fantasticskythrow.messages.config.IAdvancedMessagesFile
import com.gmail.fantasticskythrow.messages.data.MessageData
import com.gmail.fantasticskythrow.messages.data.SectionSubTypes
import com.gmail.fantasticskythrow.messages.data.SectionTypes
import com.gmail.fantasticskythrow.messages.replacer.IPlaceholderReplacer
import net.milkbowl.vault.permission.Permission
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class AdvancedModeMessageGenerator(private val appConfig: IAdvancedGeneratorAppConfiguration,
                                   private val permission: Permission,
                                   private val advancedMessagesFile: IAdvancedMessagesFile,
                                   private val placeholderReplacer: IPlaceholderReplacer,
                                   private val plmFile: IPLMFile
) : IBasicMessageGenerator, IAdditionalMessagesGenerator {

    override fun getJoinMessageDataForPlayer(player: Player): MessageData {
        val groupName: String? = permission.getPrimaryGroup(player)
        val playerName = player.name
        var message: MessageData? = null
        @Suppress("LiftReturnOrAssignment")
        if (!appConfig.useRandom) {
            message = advancedMessagesFile.getNewPlayerMessage(playerName, groupName)
            if (message == null) {
                message = advancedMessagesFile.getJoinMessage(playerName, groupName, plmFile.getTimeSinceLastLoginMs(player))
            }
        } else {
            var messages = advancedMessagesFile.getAllNewPlayerMessages(playerName, groupName)
            if (messages.isNotEmpty()) {
                message = messages.random()
            } else {
                messages = advancedMessagesFile.getAllJoinMessages(playerName, groupName, plmFile.getLastLogin(player))
                if (messages.isNotEmpty()) {
                    message = messages.random()
                }
            }
        }
        if (message == null) {
            message = MessageData("&e%playername joined the game", mutableListOf(), SectionTypes.ERROR, SectionSubTypes.NO_PATH)
        }
        val finalMessage = replacePlaceholdersAndFormatMessage(message.message, player)
        message = MessageData(finalMessage, message.channels, message.type, message.subType)
        return message
    }

    override fun getQuitMessageDataForPlayer(player: Player): MessageData {
        val groupName: String? = permission.getPrimaryGroup(player)
        val playerName = player.name
        var message: MessageData? = null
        @Suppress("LiftReturnOrAssignment")
        if (!appConfig.useRandom) {
            message = advancedMessagesFile.getQuitMessage(playerName, groupName)
        } else {
            val messages = advancedMessagesFile.getAllQuitMessages(playerName,groupName)
            if (messages.isNotEmpty()) {
                message = messages.random()
            }
        }
        if (message == null) {
            message = MessageData("&e%playername left the game", mutableListOf(), SectionTypes.ERROR, SectionSubTypes.NO_PATH)
        }
        val finalMessage = replacePlaceholdersAndFormatMessage(message.message, player)
        message = MessageData(finalMessage, message.channels, message.type, message.subType)
        return message
    }

    private fun replacePlaceholdersAndFormatMessage(message: String, player: Player): String {
        var finalMessage = placeholderReplacer.replacePlaceholders(message, player)
        finalMessage = ChatColor.translateAlternateColorCodes('&', finalMessage)
        return finalMessage
    }

    fun reload() {
        advancedMessagesFile.reload()
    }

    override fun getWelcomeMessagesForPlayer(player: Player): List<String> {
        val groupName: String? = permission.getPrimaryGroup(player)
        return advancedMessagesFile.getWelcomeMessages(player.name, groupName).map { replacePlaceholdersAndFormatMessage(it, player) }
    }

    override fun getPublicMessagesForPlayer(player: Player): List<String> {
        val groupName: String? = permission.getPrimaryGroup(player)
        return advancedMessagesFile.getPublicMessages(player.name, groupName).map { replacePlaceholdersAndFormatMessage(it, player) }
    }
}