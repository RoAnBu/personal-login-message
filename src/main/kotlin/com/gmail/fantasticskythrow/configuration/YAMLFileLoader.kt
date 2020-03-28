package com.gmail.fantasticskythrow.configuration

import com.gmail.fantasticskythrow.PLM
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

class YAMLFileLoader(private val file: File, private val backupResourceFilePath: String? = null) {

    private val logger = PLM.logger()

    val yamlConfiguration: YamlConfiguration = loadFileContent()

    private fun loadFileContent(): YamlConfiguration {
        return if (!file.exists()) {
            if (backupResourceFilePath != null) {
                logger.debug("${file.name} file does not exist, creating new file with default content")
                val defaultYaml = loadDefaultContent(backupResourceFilePath)
                defaultYaml.save(file)
                defaultYaml
            } else {
                throw FileNotFoundException("File ${file.absoluteFile} could not be loaded because it doesn't exist" +
                        " and no default file content was given")
            }
        } else {
            logger.debug("${file.name} file exists, loading file...")
            YamlConfiguration.loadConfiguration(file)
        }
    }

    private fun loadDefaultContent(backupResourceFilePath: String): YamlConfiguration {
        return loadYAMLResource(backupResourceFilePath)
    }

    companion object {
        fun loadYAMLResource(resourceFilePath: String): YamlConfiguration {
            return YamlConfiguration.loadConfiguration(InputStreamReader(YAMLFileLoader::class.java.getResourceAsStream(resourceFilePath)))
        }

        fun loadAndSaveResourceToFile(resourceFilePath: String, targetFile: File) {
            loadYAMLResource(resourceFilePath).save(targetFile)
        }

        fun loadAndSaveResourceToFileIfNotExists(resourceFilePath: String, targetFile: File) {
            if (!targetFile.exists()) {
                loadAndSaveResourceToFile(resourceFilePath, targetFile)
            }
        }
    }
}