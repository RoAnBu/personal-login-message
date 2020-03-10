package com.gmail.fantasticskythrow.other

import com.gmail.fantasticskythrow.PLM
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import java.io.File
import java.io.IOException

class PLMSavableYaml(private val file: File, scheduler: BukkitScheduler, plugin: Plugin): SavableYaml, Runnable {

    private val logger = PLM.logger()

    init {
            scheduler.scheduleSyncRepeatingTask(plugin, this, 12000, 12000)
    }

    override val yamlConfiguration: YamlConfiguration = loadFileContent()

    override fun save() {
        this.run()
    }

    override fun run() {
        try {
            yamlConfiguration.save(file)
            logger.debug("[PLM] PLM.yml has been saved successfully.")
        } catch (e: IOException) {
            logger.error(e.message)
            logger.warn("PLM.yml could not be saved!")
            logger.warn("Please check whether PLM is permitted to write in PLM.yml!")
        } catch (e: Exception) {
            logger.error("PLM.yml could not be loaded or saved. Login and logout information will be lost")
            logger.error(e)
        }
    }

    private fun loadFileContent(): YamlConfiguration {
        val configuration: YamlConfiguration = YamlConfiguration.loadConfiguration(file)
        if (!configuration.contains("firstenabled")) {
            configuration["firstenabled"] = "false"
        }
        if (!configuration.contains("Countries")) {
            configuration["Countries.United States"] = "United States"
            configuration["Countries.France"] = "France"
            configuration["Countries.Germany"] = "Germany"
            configuration["Countries.Brazil"] = "Brazil"
            configuration["Countries.Netherlands"] = "Netherlands"
            configuration["Countries.United Kingdom"] = "United Kingdom"
            configuration["Countries.Slovenia"] = "Slovenia"
            configuration["Countries.Bulgaria"] = "Bulgaria"
            configuration["Countries.Canada"] = "Canada"
            configuration["Countries.Mexico"] = "Mexico"
            configuration["Countries.Italy"] = "Italy"
            configuration["Countries.Spain"] = "Spain"
            configuration["Countries.Australia"] = "Australia"
            configuration["Countries.India"] = "India"
            configuration["Countries.Russian Federation"] = "Russian Federation"
            configuration["Countries.Your Country"] = "Your Country"
        }
        if (!configuration.contains("totallogins")) {
            configuration["totallogins"] = 0L
        }
        if (!configuration.contains("uniqueplayers")) {
            configuration["uniqueplayers"] = 0
        }
        try {
            configuration.save(file)
            return configuration
        } catch (ex: IOException) {
            logger.error(ex.message)
            logger.error("PLM.yml is not available!")
            logger.error("Please check whether PLM is permitted to write in PLM.yml!")
            throw ex
        }
    }
}