package com.gmail.fantasticskythrow

import com.gmail.fantasticskythrow.configuration.AppConfiguration
import com.gmail.fantasticskythrow.configuration.AppConfigurationFile
import com.gmail.fantasticskythrow.messages.MessagesModeManager
import com.gmail.fantasticskythrow.messages.listener.CommonListener
import com.gmail.fantasticskythrow.other.logging.BukkitLoggerWrapper
import com.gmail.fantasticskythrow.other.logging.ILoggerWrapper
import com.gmail.fantasticskythrow.other.plugins.PLMPluginConnector
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class PLM : JavaPlugin() {
    private lateinit var messagesModeManager: MessagesModeManager
    private var isVaultUnavailable = false
    var chat: Chat? = null
        private set
    var permission: Permission? = null
        private set
    lateinit var appConfiguration: AppConfiguration
        private set
    lateinit var plmPluginConnector: PLMPluginConnector
        private set
    private var pluginEnabled = true

    override fun onEnable() {
        setupLogging()
        if (!isMinecraftVersionSupported) {
            Companion.logger.error("Minecraft version below 1.7.8 are no longer supported, your version is: $minecraftVersion")
            Companion.logger.error("Disabling PLM")
            return
        }
        // TODO refactor exception handling
        try {
            loadAppConfiguration()
            this.plmPluginConnector = PLMPluginConnector(server.pluginManager)
            if (appConfiguration.pluginEnabled) {
                initAdvancedMode()
            } else {
                Companion.logger.info("Personal Login Message is not enabled in config")
                pluginEnabled = false
            }
        } catch (e: Exception) { // Not handled exceptions
            Companion.logger.error("An unexpected error has occurred while setting up PLM!")
            Companion.logger.error("PLM is disabled")
            Companion.logger.error(e)
            pluginEnabled = false
        }
    }

    override fun onDisable() {
        if (pluginEnabled) {
            messagesModeManager.plmFile.save()
        }
        Companion.logger.info("Personal Login Message disabled")
    }

    private fun setupLogging() {
        Companion.logger.logger = this.logger
    }

    private fun initAdvancedMode() {
        setupProviders()
        if (isVaultUnavailable || permission == null) { //If vault or permission/chat plugin is not available -> Standard setup
            Companion.logger.warn("Sorry, you need Vault and a compatible permissions plugin to use the advanced messages mode!")
            // TODO handle this or remove check
        } else { //Activate AdvancedMessages, because vault is active and it's enabled
            messagesModeManager = MessagesModeManager(this)
            registerEventListeners()
            Companion.logger.info("Advanced messages mode is enabled")
        }
    }

    private fun setupProviders() {
        setupChatProvider()
        setupPermissionProvider()
    }

    private fun registerEventListeners() {
        server.pluginManager.registerEvents(CommonListener(messagesModeManager.messageEventProcessor), this)
    }

    private fun setupChatProvider() {
        try {
            val chatProvider = server.servicesManager.getRegistration(Chat::class.java)
            if (chatProvider != null) {
                chat = chatProvider.provider
            } else {
                Companion.logger.info("Found no chat plugin. Standard player format will be used.")
            }
        } catch (er: Error) {
            Companion.logger.warn("PLM was not able to find 'Vault'. Is it installed?")
            Companion.logger.warn("Using chat format is now disabled")
            Companion.logger.trace(er)
            isVaultUnavailable = true
        }
    }

    /**
     * Tries to find Vault and setup the hooked permission plugin. This is only called if setupChatProvider() was successful.
     */
    private fun setupPermissionProvider() {
        try {
            val permissionProvider = server.servicesManager.getRegistration(
                    Permission::class.java)
            if (permissionProvider != null) {
                permission = permissionProvider.provider
            } else {
                Companion.logger.warn("Found no permission plugin!")
            }
        } catch (er: Error) {
            if (!isVaultUnavailable) {
                Companion.logger.error("An unknown error has occurred concerning Vault")
                throw er
            }
        }
    }

    private val isMinecraftVersionSupported: Boolean
        get() = minecraftVersion >= 178

    /**
     * Use this function to compare server versions
     * @return the version as an integer e.g.: 1.6.4-R2.0 -> 164
     */
    private val minecraftVersion: Int
        get() {
            var version = server.bukkitVersion.split("-").toTypedArray()[0]
            version = version.replace("\\.".toRegex(), "")
            var versionNumber = 0
            try {
                versionNumber = version.toInt()
            } catch (ne: NumberFormatException) {
                Companion.logger.error("An error occurred while analysing the Minecraft server version!")
            }
            return versionNumber
        }

    fun reloadMessages() {
        loadAppConfiguration()
        messagesModeManager.reloadMessageConfigFiles()
    }

    private fun loadAppConfiguration() {
        val appConfigurationFile = AppConfigurationFile(File(dataFolder, "config.yml"))
        appConfiguration = AppConfiguration(appConfigurationFile.yamlConfiguration)
    }

    companion object {
        private val logger = BukkitLoggerWrapper(null)
        fun logger(): ILoggerWrapper {
            return logger
        }
    }
}