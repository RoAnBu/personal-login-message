package com.gmail.fantasticskythrow.configuration

import com.gmail.fantasticskythrow.PLM
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class AppConfiguration(private val configFile: File) : IAdvancedGeneratorAppConfiguration {
    private var cfg: YamlConfiguration? = null

    var pluginEnabled = true
        private set
    var usePermGeneral = false
        private set
    var usePermPM = false
        private set
    var useFakeJoinMsg = false
        private set
    var useFakeQuitMsg = false
        private set
    var replaceVnpFakeMsg = false
        private set
    var advancedStatus = false
        private set
    override var useRandom = false
        private set
    var useEssentialsNick = false
        private set
    var timeNames: TimeNames? = null
        private set
    var delay = 0
        private set

    init {
        loadConfiguration()
    }

    private fun loadConfiguration() {
        try {
            logger.info("Loading config...")
            /*
			 * Set default values if necessary
			 */
            cfg = YamlConfiguration.loadConfiguration(configFile)
            cfg!!.addDefault("general.enabled", "true")
            cfg!!.addDefault("general.usepermissions", "false")
            cfg!!.addDefault("general.useEssentialsNickName", "true")
            cfg!!.addDefault("advancedmessages.enabled", "false")
            cfg!!.addDefault("advancedmessages.second", "second")
            cfg!!.addDefault("advancedmessages.seconds", "seconds")
            cfg!!.addDefault("advancedmessages.minute", "minute")
            cfg!!.addDefault("advancedmessages.minutes", "minutes")
            cfg!!.addDefault("advancedmessages.hour", "hour")
            cfg!!.addDefault("advancedmessages.hours", "hours")
            cfg!!.addDefault("advancedmessages.day", "day")
            cfg!!.addDefault("advancedmessages.days", "days")
            cfg!!.addDefault("advancedmessages.month", "month")
            cfg!!.addDefault("advancedmessages.months", "months")
            cfg!!.addDefault("advancedmessages.no last login", "no last login")
            cfg!!.addDefault("advancedmessages.userandom", "false")
            cfg!!.addDefault("Welcome messages.delayms", "200")
            cfg!!.addDefault("Public messages.usepermissions", "false")
            cfg!!.addDefault("VanishNoPacket.usefakejoinmessage", "false")
            cfg!!.addDefault("VanishNoPacket.usefakequitmessage", "false")
            cfg!!.addDefault("VanishNoPacket.replaceVNPfakemessages", "false")
            cfg!!.options().copyDefaults(true)
            cfg!!.save(configFile)

            /*
			 * Load values
			 */
            pluginEnabled = this.cfg!!.getString("general.enabled").equals("true", ignoreCase = true)
            val second: String? = cfg!!.getString("advancedmessages.second")
            val seconds: String? = cfg!!.getString("advancedmessages.seconds")
            val minute: String? = cfg!!.getString("advancedmessages.minute")
            val minutes: String? = cfg!!.getString("advancedmessages.minutes")
            val hour: String? = cfg!!.getString("advancedmessages.hour")
            val hours: String? = cfg!!.getString("advancedmessages.hours")
            val day: String? = cfg!!.getString("advancedmessages.day")
            val days: String? = cfg!!.getString("advancedmessages.days")
            val month: String? = cfg!!.getString("advancedmessages.month")
            val months: String? = cfg!!.getString("advancedmessages.months")
            val noLastLogin: String? = cfg!!.getString("advancedmessages.no last login")
            if (second != null && seconds != null && minute != null && minutes != null && hour != null && hours != null
                    && day != null && days != null && month != null && months != null && noLastLogin != null) {
                timeNames = TimeNames(second, seconds, minute, minutes, hour, hours, day, days, month, months, noLastLogin)
            }
            usePermGeneral = cfg!!.getString("general.usepermissions").equals("true", ignoreCase = true)
            useEssentialsNick = cfg!!.getString("general.useEssentialsNickName").equals("true", ignoreCase = true)
            usePermPM = cfg!!.getString("messages.usepermissions").equals("true", ignoreCase = true)
            useFakeJoinMsg = cfg!!.getString("VanishNoPacket.usefakejoinmessage").equals("true", ignoreCase = true)
            useFakeQuitMsg = cfg!!.getString("VanishNoPacket.usefakequitmessage").equals("true", ignoreCase = true)
            replaceVnpFakeMsg = cfg!!.getString("VanishNoPacket.replaceVNPfakemessages").equals("true", ignoreCase = true)
            advancedStatus = cfg!!.getString("advancedmessages.enabled").equals("true", ignoreCase = true)
            useRandom = cfg!!.getString("advancedmessages.userandom").equals("true", ignoreCase = true)
            setInternalDelay()

        } catch (e: Exception) {
            logger.error(e)
            logger.error(e.message)
            logger.error("Unable to load config.yml")
            logger.error("Plugin is now disabled!")
            pluginEnabled = false
        }

    }

    fun reloadConfiguration() {
        this.loadConfiguration()
    }

    private fun setInternalDelay() {
        val delaySetting = cfg!!.getString("Welcome messages.delayms")
        delay = try {
            Integer.parseInt(delaySetting)
        } catch (e: NumberFormatException) {
            logger.info("Could not find a number at delayms in the config.yml!")
            logger.info(e.message)
            200
        }
    }

    companion object {
        private val logger = PLM.logger()
    }

}
