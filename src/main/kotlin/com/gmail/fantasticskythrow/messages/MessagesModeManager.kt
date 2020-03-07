package com.gmail.fantasticskythrow.messages

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.commands.PLMCommandHandler
import com.gmail.fantasticskythrow.configuration.AppConfiguration
import com.gmail.fantasticskythrow.configuration.TimeNames
import com.gmail.fantasticskythrow.messages.config.AdvancedMessagesFile
import com.gmail.fantasticskythrow.messages.config.StandardMessagesFile
import com.gmail.fantasticskythrow.messages.generator.AdvancedModeMessageGenerator
import com.gmail.fantasticskythrow.messages.generator.IAdditionalMessagesGenerator
import com.gmail.fantasticskythrow.messages.generator.IBasicMessageGenerator
import com.gmail.fantasticskythrow.messages.generator.StandardModeMessageGenerator
import com.gmail.fantasticskythrow.messages.replacer.BasicPlaceholderReplacer
import com.gmail.fantasticskythrow.messages.replacer.FullPlaceholderReplacer
import com.gmail.fantasticskythrow.other.*
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.kitteh.vanish.event.VanishStatusChangeEvent
import java.io.File
import java.util.*

/**
 * Provides event listeners and creates the instances which are needed to get
 * the wanted string
 */
class MessagesModeManager(private val plugin: PLM, private val advancedStatus: Boolean) {
    private val chat: Chat? = plugin.chat
    private val permission: Permission? = plugin.permission
    private val appConfiguration: AppConfiguration = plugin.cfg
    val plmFile: IPLMFile
    val vanishNoPacketManager: VanishNoPacketManager
    private val vnpFakeMsg: MutableList<String> = ArrayList()
    private val basicMessageGenerator: IBasicMessageGenerator
    private val additionalMessagesGenerator: IAdditionalMessagesGenerator?

    init {
        plmFile = PLMFile(plugin)
        vanishNoPacketManager = VanishNoPacketManager(plugin)
        val commandHandler = PLMCommandHandler(plugin, advancedStatus)
        plugin.getCommand("plm")!!.setExecutor(commandHandler)

        if (!advancedStatus) { // StandardMessages
            basicMessageGenerator = StandardModeMessageGenerator(plugin,
                    StandardMessagesFile(File(plugin.dataFolder, "messages.txt")),
                    BasicPlaceholderReplacer(plugin, chat, permission, plmFile, vanishNoPacketManager, getTimeNames()))
        } else { // Advanced messages mode
            basicMessageGenerator = try {
                AdvancedModeMessageGenerator(appConfiguration, permission!!,
                        AdvancedMessagesFile(File(plugin.dataFolder, "AdvancedMessages.yml"), plmFile),
                        FullPlaceholderReplacer(plugin, chat, permission, plmFile, vanishNoPacketManager, getTimeNames()),
                        plmFile)
            } catch (e: Exception) {
                logger.error("Could not initialize Advanced Messages Mode, using Standard Mode instead")
                logger.error(e)
                StandardModeMessageGenerator(plugin,
                        StandardMessagesFile(File(plugin.dataFolder, "messages.txt")),
                        BasicPlaceholderReplacer(plugin, chat, permission, plmFile, vanishNoPacketManager, getTimeNames()))
            }
        }
        additionalMessagesGenerator = if (basicMessageGenerator is AdvancedModeMessageGenerator) {
            basicMessageGenerator
        } else {
            null
        }
    }

    /**
     * Reloads the messages of AMM or SM
     */
    fun reloadMessageConfigFiles() {
        if (basicMessageGenerator is StandardModeMessageGenerator) {
            basicMessageGenerator.reload()
        } else if (basicMessageGenerator is AdvancedModeMessageGenerator) {
            basicMessageGenerator.reload()
        }
    }

    fun onPlayerJoinEvent(playerJoinEvent: PlayerJoinEvent) {
        playerJoinEvent.joinMessage = getFinalJoinMessageAndSendAdditionalMessages(playerJoinEvent.player, false)
    }

    fun onEarlyQuitEvent(playerQuitEvent: PlayerQuitEvent) {
        try {
            alreadyQuit = false
            val playerName = playerQuitEvent.player.name
            val isVanished = vanishNoPacketManager.isVanished(playerName)
            if (isVanished) {
                alreadyQuit = true
                logger.debug("No quit message for player $playerName because the player was vanished!")
            }
            vanishNoPacketManager.removeJoinedPlayer(playerName)
        } catch (e: Exception) {
            logger.error("Unexpected error in onEarlyQuitEvent", e)
        }
    }

    fun onLatePlayerQuitEvent(e: PlayerQuitEvent) {
        if (!alreadyQuit) {
            e.quitMessage = getFinalQuitMessageIfAvailable(e.player)
        }
    }

    fun onPlayerKickEvent(playerKickEvent: PlayerKickEvent) {
        getFinalQuitMessageIfAvailable(playerKickEvent.player)?.let { playerKickEvent.leaveMessage = it }
    }

    fun onVanishStatusChangeEvent(event: VanishStatusChangeEvent) {
        try {
            val player = event.player
            val playerName = player.name
            if (!vanishNoPacketManager.isJustJoinedPlayer(playerName)) {
                var vnpFakeCmdUser = false
                if (vnpFakeMsg.contains(playerName)) {
                    vnpFakeCmdUser = true
                    vnpFakeMsg.remove(playerName)
                }
                if (event.isVanishing && (appConfiguration.useFakeQuitMsg || vnpFakeCmdUser)) { // -> Quit message (Fake)
                    val fakeQuitMessage = getFinalQuitMessageIfAvailable(player)
                    if (fakeQuitMessage != null) {
                        plugin.server.broadcastMessage(fakeQuitMessage)
                    }
                    plmFile.setPlayerQuitTime(player)
                } else if (!event.isVanishing && (appConfiguration.useFakeJoinMsg || vnpFakeCmdUser)) { // Join  message (Fake)
                    val fakeJoinMessage = getFinalJoinMessageAndSendAdditionalMessages(player, true)
                    if (fakeJoinMessage != null) {
                        plugin.server.broadcastMessage(fakeJoinMessage)
                    }
                }
            }
        } catch (ex: Exception) {
            logger.error(ex)
            logger.error("An unknown error has occurred at VanishStatusChangeEvent!")
            logger.error("Please make sure that all configuration files are available")
        }
    }

    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        try {
            val cmd = event.message.replace(Regex("/"), "")
            if (cmd == "v fq" || cmd == "vanish fq") {
                if (!vanishNoPacketManager.isVanished(event.player.name)) {
                    vnpFakeMsg.add(event.player.name)
                    event.message = "/vanish"
                } else {
                    event.isCancelled = true
                    event.player.sendMessage(ChatColor.RED.toString() + "Already invisible :)")
                }
            } else if (cmd == "v fj" || cmd == "vanish fj") {
                if (vanishNoPacketManager.isVanished(event.player.name)) {
                    vnpFakeMsg.add(event.player.name)
                    event.message = "/vanish"
                } else {
                    event.isCancelled = true
                    event.player.sendMessage(ChatColor.RED.toString() + "Already visible :)")
                }
            }
        } catch (ex: Exception) {
            logger.error(ex)
            logger.error("An error has occurred at onPlayerCommandPreprocessEvent!")
        }
    }

    private fun getFinalJoinMessageAndSendAdditionalMessages(player: Player, ignoreVanish: Boolean): String? {
        try {
            vanishNoPacketManager.addJoinedPlayer(player.name)
            plmFile.setPlayerLogin(player)
            val isVanished = if (ignoreVanish) {
                false
            } else {
                vanishNoPacketManager.isVanished(player.name)
            }
            if (!PLMToolbox.getPermissionJoin(appConfiguration.usePermGeneral, player) || isVanished) {
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
            plmFile.setPlayerQuitTime(player)
            if (!PLMToolbox.getPermissionQuit(appConfiguration.usePermGeneral, player)) {
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
        return plugin.server.onlinePlayers.filter { it.uniqueId != player.uniqueId }
    }

    private fun sendPublicMessages(messages: List<String>, receivers: List<Player>) {
        val pmPrinter = PublicMessagePrinter()
        pmPrinter.start(messages, receivers)
    }

    private fun sendWelcomeMessages(messages: List<String>, player: Player, delayMs: Int = appConfiguration.delay) {
        val wmPrinter = WelcomeMessagePrinter()
        wmPrinter.start(delayMs, messages, player)
    }

    private fun getTimeNames(): TimeNames {
        var timeNames = appConfiguration.timeNames
        if (timeNames == null) {
            timeNames = TimeNames("second", "seconds", "minute", "minutes",
                    "hour", "hours", "day", "days", "month", "months",
                    "No last login")
        }
        return timeNames
    }

    companion object {
        private var alreadyQuit = false
        private val logger = PLM.logger()
    }
}