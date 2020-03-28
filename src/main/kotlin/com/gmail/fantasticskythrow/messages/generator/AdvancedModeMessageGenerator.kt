package com.gmail.fantasticskythrow.messages.generator

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.configuration.IAdvancedGeneratorAppConfiguration
import com.gmail.fantasticskythrow.configuration.IPlayerLogins
import com.gmail.fantasticskythrow.messages.config.IAdvancedMessagesConfiguration
import com.gmail.fantasticskythrow.messages.data.MessageData
import com.gmail.fantasticskythrow.messages.data.SectionSubTypes
import com.gmail.fantasticskythrow.messages.data.SectionTypes
import com.gmail.fantasticskythrow.messages.replacer.IPlaceholderReplacer
import net.milkbowl.vault.permission.Permission
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.lang.UnsupportedOperationException

class AdvancedModeMessageGenerator(private val appConfig: IAdvancedGeneratorAppConfiguration,
                                   private val permission: Permission,
                                   private val advancedMessagesConfiguration: IAdvancedMessagesConfiguration,
                                   private val placeholderReplacer: IPlaceholderReplacer,
                                   private val playerLogins: IPlayerLogins
) : IBasicMessageGenerator, IAdditionalMessagesGenerator {

    private val logger = PLM.logger()

    override fun getJoinMessageDataForPlayer(player: Player): MessageData {
        val groupName: String? = getPrimaryGroupIfAvailable(player)
        val playerName = player.name
        var message: MessageData? = null
        @Suppress("LiftReturnOrAssignment")
        if (!appConfig.useRandom) {
            message = advancedMessagesConfiguration.getNewPlayerMessage(playerName, groupName)
            if (message == null) {
                message = advancedMessagesConfiguration.getJoinMessage(playerName, groupName, playerLogins.getTimeSinceLastLoginMs(player))
            }
        } else {
            var messages = advancedMessagesConfiguration.getAllNewPlayerMessages(playerName, groupName)
            if (messages.isNotEmpty()) {
                message = messages.random()
            } else {
                messages = advancedMessagesConfiguration.getAllJoinMessages(playerName, groupName, playerLogins.getLastLoginTimeMs(player))
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
        val groupName: String? = getPrimaryGroupIfAvailable(player)
        val playerName = player.name
        var message: MessageData? = null
        @Suppress("LiftReturnOrAssignment")
        if (!appConfig.useRandom) {
            message = advancedMessagesConfiguration.getQuitMessage(playerName, groupName)
        } else {
            val messages = advancedMessagesConfiguration.getAllQuitMessages(playerName,groupName)
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

    override fun getWelcomeMessagesForPlayer(player: Player): List<String> {
        val groupName: String? = getPrimaryGroupIfAvailable(player)
        return advancedMessagesConfiguration.getWelcomeMessages(player.name, groupName).map { replacePlaceholdersAndFormatMessage(it, player) }
    }

    override fun getPublicMessagesForPlayer(player: Player): List<String> {
        val groupName: String? = getPrimaryGroupIfAvailable(player)
        return advancedMessagesConfiguration.getPublicMessages(player.name, groupName).map { replacePlaceholdersAndFormatMessage(it, player) }
    }

    private fun getPrimaryGroupIfAvailable(player: Player): String? {
        return try {
            permission.getPrimaryGroup(player)
        } catch (e: UnsupportedOperationException) {
            logger.debug("Could not get primary group for player ${player.name}")
            logger.debug(e)
            null
        }
    }
}