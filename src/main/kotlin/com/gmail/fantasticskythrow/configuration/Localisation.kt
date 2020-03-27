package com.gmail.fantasticskythrow.configuration

import com.gmail.fantasticskythrow.PLM
import org.bukkit.configuration.file.YamlConfiguration

class Localisation(private val yamlConfiguration: YamlConfiguration): ICountryAlternateNames, ITimeNames {

    private val logger = PLM.logger()

    override fun getAlternateNameForCountry(englishName: String): String {
        return if (yamlConfiguration.contains("countries.$englishName")) {
            yamlConfiguration.getString("countries.$englishName")!!
        } else {
            englishName
        }
    }

    override val timeNames = readTimeNames()

    private fun readTimeNames(): TimeNames {
        val second: String? = yamlConfiguration.getString("time.second")
        val seconds: String? = yamlConfiguration.getString("time.seconds")
        val minute: String? = yamlConfiguration.getString("time.minute")
        val minutes: String? = yamlConfiguration.getString("time.minutes")
        val hour: String? = yamlConfiguration.getString("time.hour")
        val hours: String? = yamlConfiguration.getString("time.hours")
        val day: String? = yamlConfiguration.getString("time.day")
        val days: String? = yamlConfiguration.getString("time.days")
        val month: String? = yamlConfiguration.getString("time.month")
        val months: String? = yamlConfiguration.getString("time.months")
        val noLastLogin: String? = yamlConfiguration.getString("time.no last login")
        return if (second != null && seconds != null && minute != null && minutes != null && hour != null && hours != null
                && day != null && days != null && month != null && months != null && noLastLogin != null) {
            TimeNames(second, seconds, minute, minutes, hour, hours, day, days, month, months, noLastLogin)
        } else {
            logger.debug("Time localisation is missing at least one argument, using default english names")
            logger.debug("the option 'time' should include the following sub-options:")
            logger.debug("second, seconds, minute, minutes, hour, hours, day, days, month, months, no last login")
            TimeNames.createEnglishTimeNames()
        }
    }
}