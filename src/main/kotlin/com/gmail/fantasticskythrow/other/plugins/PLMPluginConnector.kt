package com.gmail.fantasticskythrow.other.plugins

import com.earth2me.essentials.Essentials
import com.gmail.fantasticskythrow.PLM
import org.bukkit.plugin.PluginManager


class PLMPluginConnector(pluginManager: PluginManager) : IPLMPluginConnector {
    override val essentials: Essentials?

    init {
        essentials = try {
            pluginManager.getPlugin("Essentials") as Essentials?
        } catch (e: Exception) {
            logger.info("EssentialsX was not found, not using Essentials features")
            logger.debug(e)
            null
        }
    }

    companion object {
        private val logger = PLM.logger()
    }
}