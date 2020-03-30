package com.gmail.fantasticskythrow.configuration

import com.gmail.fantasticskythrow.PLM
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader

class AppConfigurationFile(private val configFile: File) {

    private val logger = PLM.logger()

    val yamlConfiguration: YamlConfiguration = loadFileContent()

    private fun loadFileContent(): YamlConfiguration {
        return if (!configFile.exists()) {
            logger.debug("config.yml file does not exist, creating new file with default content")
            val defaultYaml = loadDefaultContent()
            defaultYaml.save(configFile)
            defaultYaml
        } else {
            logger.debug("config.yml file exists, loading file...")
            val yaml = YamlConfiguration.loadConfiguration(configFile)
            addMissingSettingsAndSave(yaml)
            yaml
        }
    }

    private fun addMissingSettingsAndSave(yaml: YamlConfiguration) {
        var contentChanged = false

        if (!yaml.contains("general.enabled")) {
            yaml["general.enabled"] = true
            contentChanged = true
        }
        if (!yaml.contains("general.usePermissions")) {
            yaml["general.usePermissions"] = false
            contentChanged = true
        }
        if (!yaml.contains("general.useEssentialsNickName")) {
            yaml["general.useEssentialsNickName"] = true
            contentChanged = true
        }
        if (!yaml.contains("general.useRandomMessageSelection")) {
            yaml["advancedmessages.userandom"] = false
            contentChanged = true
        }
        if (!yaml.contains("welcomeMessages.delayMS")) {
            yaml["welcomeMessages.delayMS"] = 200
            contentChanged = true
        }
        if (!yaml.contains("publicMessages.usePermissions")) {
            yaml["publicMessages.usePermissions"] = false
            contentChanged = true
        }
        if (!yaml.contains("vanish.usefakejoinmessage")) {
            yaml["vanish.usefakejoinmessage"] = false
            contentChanged = true
        }
        if (!yaml.contains("vanish.usefakequitmessage")) {
            yaml["vanish.usefakequitmessage"] = false
            contentChanged = true
        }
        if (!yaml.contains("vanish.replaceVNPfakemessages")) {
            yaml["vanish.replaceVNPfakemessages"] = false
            contentChanged = true
        }

        if (contentChanged)
            yaml.save(configFile)
    }

    private fun loadDefaultContent(): YamlConfiguration {
        return YamlConfiguration.loadConfiguration(InputStreamReader(javaClass.getResourceAsStream("/config.yml")))
    }
}