package com.gmail.fantasticskythrow.configuration

import com.gmail.fantasticskythrow.PLM
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader

class LocalisationFile(private val localisationFile: File) {

    private val logger = PLM.logger()

    val yamlConfiguration: YamlConfiguration = loadFileContent()

    private fun loadFileContent(): YamlConfiguration {
        return if (!localisationFile.exists()) {
            logger.debug("Localisation file does not exist, creating new file with default content")
            val defaultYaml = loadDefaultContent()
            defaultYaml.save(localisationFile)
            defaultYaml
        } else {
            logger.debug("Localisation file exists, loading file...")
            YamlConfiguration.loadConfiguration(localisationFile)
        }
    }

    private fun loadDefaultContent(): YamlConfiguration {
        return YamlConfiguration.loadConfiguration(InputStreamReader(javaClass.getResourceAsStream("/localisation.yml")))
    }
}