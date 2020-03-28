package com.gmail.fantasticskythrow.messages

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.commands.PLMCommandHandler
import com.gmail.fantasticskythrow.configuration.*
import com.gmail.fantasticskythrow.messages.config.AdvancedMessagesConfiguration
import com.gmail.fantasticskythrow.configuration.YAMLFileLoader
import com.gmail.fantasticskythrow.messages.config.StandardMessagesFile
import com.gmail.fantasticskythrow.messages.generator.AdvancedModeMessageGenerator
import com.gmail.fantasticskythrow.messages.generator.IAdditionalMessagesGenerator
import com.gmail.fantasticskythrow.messages.generator.IBasicMessageGenerator
import com.gmail.fantasticskythrow.messages.generator.StandardModeMessageGenerator
import com.gmail.fantasticskythrow.messages.replacer.BasicPlaceholderReplacer
import com.gmail.fantasticskythrow.messages.replacer.FullPlaceholderReplacer
import com.gmail.fantasticskythrow.other.VanishManager
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import java.io.File

class MessagesModeManager(private val plugin: PLM, advancedStatus: Boolean) {

    private val logger = PLM.logger()

    private val chat: Chat? = plugin.chat
    private val permission: Permission? = plugin.permission
    private val appConfiguration: AppConfiguration = plugin.appConfiguration
    val plmFile: PLMFile = PLMFile(PLMSavableYaml(File(plugin.dataFolder, "PLM.yml"), plugin.server.scheduler, plugin))
    private val basicMessageGenerator: IBasicMessageGenerator
    private val additionalMessagesGenerator: IAdditionalMessagesGenerator?

    val messageEventProcessor: MessageEventProcessor

    init {
        val localisation = Localisation(YAMLFileLoader(File(plugin.dataFolder, "localisation.yml"), "/localisation.yml").yamlConfiguration)
        val commandHandler = PLMCommandHandler(plugin, advancedStatus)
        plugin.getCommand("plm")!!.setExecutor(commandHandler)

        if (!advancedStatus) { // StandardMessages
            basicMessageGenerator = createStandardModeMessageGenerator(localisation)
        } else { // Advanced messages mode
            basicMessageGenerator = try {
                val advancedMessagesConfiguration = AdvancedMessagesConfiguration(
                        YAMLFileLoader(File(plugin.dataFolder, "messages.yml"), "/messages.yml").yamlConfiguration
                )

                createAdvancedModeMessageGenerator(advancedMessagesConfiguration, localisation)
            } catch (e: Exception) {
                logger.error("Could not initialize Advanced Messages Mode, using Standard Mode instead")
                logger.error(e)
                createStandardModeMessageGenerator(localisation)
            }
        }
        additionalMessagesGenerator = if (basicMessageGenerator is AdvancedModeMessageGenerator) {
            basicMessageGenerator
        } else {
            null
        }
        messageEventProcessor = MessageEventProcessor(
                appConfiguration = appConfiguration,
                playerLogins = plmFile,
                server = plugin.server,
                basicMessageGenerator = basicMessageGenerator,
                additionalMessagesGenerator = additionalMessagesGenerator
        )
        YAMLFileLoader.loadAndSaveResourceToFileIfNotExists("/messages_examples.yml", File(plugin.dataFolder, "messages_examples.yml"))
    }

    private fun createBasicPlaceholderReplacer(localisation: Localisation) =
            BasicPlaceholderReplacer(
                    chat = chat,
                    permission = permission,
                    playerLogins = plmFile,
                    countryAlternateNames = localisation,
                    vanishManager = VanishManager(),
                    timeNames = localisation.timeNames,
                    server = plugin.server,
                    appConfiguration = appConfiguration,
                    pluginConnector = plugin.plmPluginConnector,
                    worldRenameConfig = null
            )

    private fun createFullPlaceholderReplacer(localisation: Localisation, advancedMessagesFile: AdvancedMessagesConfiguration) =
            FullPlaceholderReplacer(
                    chat = chat,
                    permission = permission,
                    plmFile = plmFile,
                    countryAlternateNames = localisation,
                    vanishManager = VanishManager(),
                    timeNames = localisation.timeNames,
                    server = plugin.server,
                    appConfiguration = appConfiguration,
                    pluginConnector = plugin.plmPluginConnector,
                    worldRenameConfig = advancedMessagesFile
            )

    private fun createStandardModeMessageGenerator(localisation: Localisation): StandardModeMessageGenerator {
        return StandardModeMessageGenerator(
                plm = plugin,
                standardMessagesFile = StandardMessagesFile(File(plugin.dataFolder, "messages.txt")),
                placeholderReplacer = createBasicPlaceholderReplacer(localisation))
    }

    private fun createAdvancedModeMessageGenerator(advancedMessagesConfiguration: AdvancedMessagesConfiguration, localisation: Localisation): AdvancedModeMessageGenerator {
        return AdvancedModeMessageGenerator(
                appConfig = appConfiguration,
                permission = permission!!,
                advancedMessagesConfiguration = advancedMessagesConfiguration,
                placeholderReplacer = createFullPlaceholderReplacer(localisation, advancedMessagesConfiguration),
                playerLogins = plmFile
        )
    }

    fun reloadMessageConfigFiles() {
        if (basicMessageGenerator is StandardModeMessageGenerator) {
            basicMessageGenerator.reload()
        } else if (basicMessageGenerator is AdvancedModeMessageGenerator) {
            // TODO basicMessageGenerator.reload()
        }
    }
}