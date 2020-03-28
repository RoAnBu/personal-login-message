package com.gmail.fantasticskythrow.messages

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.configuration.IAppConfiguration
import com.gmail.fantasticskythrow.configuration.IPlayerLogins
import com.gmail.fantasticskythrow.messages.generator.IAdditionalMessagesGenerator
import com.gmail.fantasticskythrow.messages.generator.IBasicMessageGenerator
import com.gmail.fantasticskythrow.other.PLMToolbox
import com.gmail.fantasticskythrow.other.PublicMessagePrinter
import com.gmail.fantasticskythrow.other.WelcomeMessagePrinter
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent

class MessageEventProcessor(
        private val appConfiguration: IAppConfiguration,
        private val playerLogins: IPlayerLogins,
        private val server: Server,
        private val basicMessageGenerator: IBasicMessageGenerator,
        private val additionalMessagesGenerator: IAdditionalMessagesGenerator? = null
) : ISimpleMessageEventProcessor {

    private val logger = PLM.logger()

    override fun onPlayerJoinEvent(playerJoinEvent: PlayerJoinEvent) {
        playerJoinEvent.joinMessage = getFinalJoinMessageAndSendAdditionalMessages(playerJoinEvent.player)
    }

    override fun onPlayerQuitEvent(playerQuitEvent: PlayerQuitEvent) {
        playerQuitEvent.quitMessage = getFinalQuitMessageIfAvailable(playerQuitEvent.player)
    }

    override fun onPlayerKickEvent(playerKickEvent: PlayerKickEvent) {
        getFinalQuitMessageIfAvailable(playerKickEvent.player)?.let { playerKickEvent.leaveMessage = it }
    }

    private fun getFinalJoinMessageAndSendAdditionalMessages(player: Player): String? {
        try {
            playerLogins.addPlayerLogin(player)
            if (!PLMToolbox.getPermissionJoin(appConfiguration.usePermissions, player)) {
                logger.trace("Displaying no join message for player ${player.name} because of missing permission or" +
                        " player is vanished")
                return null
            }
            val joinMessageData = basicMessageGenerator.getJoinMessageDataForPlayer(player)
            additionalMessagesGenerator?.getPublicMessagesForPlayer(player)?.let {
                if (it.isNotEmpty())
                    sendPublicMessages(it, getAllOnlinePlayersApartFromPlayer(player))
            }
            additionalMessagesGenerator?.getWelcomeMessagesForPlayer(player)?.let {
                if (it.isNotEmpty()) {
                    sendWelcomeMessages(it, player)
                }
            }
            if (joinMessageData.message.equals("off", ignoreCase = true)) {
                logger.debug("Displaying no join message for player ${player.name} because message was set to 'off'")
                return null
            }
            if (joinMessageData.type == null || joinMessageData.subType == null) {
                logger.debug("PLM join message for player ${player.name} is: ${joinMessageData.message}")
            } else {
                logger.debug("PLM join message for player ${player.name} is: ${joinMessageData.message} " +
                        "Path: ${joinMessageData.type} | ${joinMessageData.subType}")
            }
            return joinMessageData.message
        } catch (ex: Exception) {
            logger.error(ex)
            logger.error("An unknown error has occurred while fetching the join message for player ${player.name}!")
            logger.error("Please make sure that all configuration files are available")
            return null
        }
    }

    private fun getFinalQuitMessageIfAvailable(player: Player): String? {
        try {
            playerLogins.setPlayerQuitTimeToCurrentTime(player)
            if (!PLMToolbox.getPermissionQuit(appConfiguration.usePermissions, player)) {
                logger.trace("Displaying no quit message for player ${player.name} because of missing permission")
                return null
            }
            val quitMessageData = basicMessageGenerator.getQuitMessageDataForPlayer(player)
            if (quitMessageData.type == null) {
                logger.debug("PLM quit message for player ${player.name} is: ${quitMessageData.message}")
            } else {
                logger.debug("PLM quit message for player ${player.name} is: ${quitMessageData.message} " +
                        "Path: ${quitMessageData.type} | ${quitMessageData.subType}")
            }
            return quitMessageData.message
        } catch (ex: Exception) {
            logger.error(ex)
            logger.error("An unknown error has occurred while fetching the quit message for player ${player.name}!")
            logger.error("Please make sure that all configuration files are available")
            return null
        }
    }

    private fun getAllOnlinePlayersApartFromPlayer(player: Player): List<Player> {
        return server.onlinePlayers.filter { it.uniqueId != player.uniqueId }
    }

    private fun sendPublicMessages(messages: List<String>, receivers: List<Player>) {
        val pmPrinter = PublicMessagePrinter()
        pmPrinter.start(messages, receivers)
    }

    private fun sendWelcomeMessages(messages: List<String>, player: Player, delayMs: Int = appConfiguration.welcomeMessagesDelayMs) {
        val wmPrinter = WelcomeMessagePrinter()
        wmPrinter.start(delayMs, messages, player)
    }
}