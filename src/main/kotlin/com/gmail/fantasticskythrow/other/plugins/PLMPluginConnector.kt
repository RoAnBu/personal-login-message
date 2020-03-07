package com.gmail.fantasticskythrow.other.plugins

import com.earth2me.essentials.Essentials
import com.gmail.fantasticskythrow.PLM
import org.bukkit.plugin.PluginManager
import uk.org.whoami.geoip.GeoIPTools


class PLMPluginConnector(pluginManager: PluginManager) : IPLMPluginConnector {
    override val essentials: Essentials?
    override val ipLookup: IIPAddressLookup?

    init {
        essentials = try {
            pluginManager.getPlugin("Essentials") as Essentials?
        } catch (e: Exception) {
            logger.info("EssentialsX was not found, not using Essentials features")
            logger.debug(e)
            null
        }
        ipLookup = try {
            val geoIPTools = pluginManager.getPlugin("GeoIPTools") as GeoIPTools?
            IPLookupWrapper(geoIPTools?.geoIPLookup!!)
        } catch (e: Exception) {
            logger.info("GeoIPTools was not found, not using Geo IP features")
            logger.debug(e)
            null
        }
    }

    companion object {
        private val logger = PLM.logger()
    }
}