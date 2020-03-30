package com.gmail.fantasticskythrow.messages

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.commands.PLMCommandHandler
import com.gmail.fantasticskythrow.configuration.*
import com.gmail.fantasticskythrow.messages.config.AdvancedMessagesConfiguration
import com.gmail.fantasticskythrow.messages.generator.AdvancedModeMessageGenerator
import com.gmail.fantasticskythrow.messages.generator.IFullMessagesGenerator
import com.gmail.fantasticskythrow.messages.replacer.FullPlaceholderReplacer
import com.gmail.fantasticskythrow.other.VanishManager
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import java.io.File

class MessagesModeManager(private val plugin: PLM) {

    private val logger = PLM.logger()

    private val chat: Chat? = plugin.chat
    private val permission: Permission? = plugin.permission
    private val appConfiguration: AppConfiguration = plugin.appConfiguration
    val plmFile: PLMFile = PLMFile(PLMSavableYaml(File(plugin.dataFolder, "PLM.yml"), plugin.server.scheduler, plugin))
    private var messageGenerator: IFullMessagesGenerator

    val messageEventProcessor: MessageEventProcessor

    init {
        val commandHandler = PLMCommandHandler(plugin, true)
        plugin.getCommand("plm")!!.setExecutor(commandHandler)

        messageGenerator = createAdvancedModeMessageGenerator()
        messageEventProcessor = MessageEventProcessor(
                appConfiguration = appConfiguration,
                playerLogins = plmFile,
                server = plugin.server,
                basicMessageGenerator = messageGenerator,
                additionalMessagesGenerator = messageGenerator
        )
        YAMLFileLoader.loadAndSaveResourceToFileIfNotExists("/messages_examples.yml", File(plugin.dataFolder, "messages_examples.yml"))
    }

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


    private fun createAdvancedModeMessageGenerator(): AdvancedModeMessageGenerator {
        val localisation = Localisation(YAMLFileLoader(File(plugin.dataFolder, "localisation.yml"), "/localisation.yml").yamlConfiguration)
        val advancedMessagesConfiguration = AdvancedMessagesConfiguration(
                YAMLFileLoader(File(plugin.dataFolder, "messages.yml"), "/messages.yml").yamlConfiguration
        )
        return AdvancedModeMessageGenerator(
                appConfig = appConfiguration,
                permission = permission!!,
                advancedMessagesConfiguration = advancedMessagesConfiguration,
                placeholderReplacer = createFullPlaceholderReplacer(localisation, advancedMessagesConfiguration),
                playerLogins = plmFile
        )
    }

    fun reloadMessageConfigFiles() {
        messageGenerator = createAdvancedModeMessageGenerator()
        logger.info("Successfully reloaded PLM message configs")
    }
}