package com.gmail.fantasticskythrow.configuration

import org.bukkit.configuration.file.YamlConfiguration

class Localisation(private val yamlConfiguration: YamlConfiguration): ICountryAlternateNames, ITimeNames {

    override fun getAlternateNameForCountry(englishName: String): String {
        return if (yamlConfiguration.contains("countries.$englishName")) {
            yamlConfiguration.getString("countries.$englishName")!!
        } else {
            englishName
        }
    }

    override val timeNames = readTimeNames()

    private fun readTimeNames(): TimeNames {
        val second: String? = yamlConfiguration.getString("advancedmessages.second")
        val seconds: String? = yamlConfiguration.getString("advancedmessages.seconds")
        val minute: String? = yamlConfiguration.getString("advancedmessages.minute")
        val minutes: String? = yamlConfiguration.getString("advancedmessages.minutes")
        val hour: String? = yamlConfiguration.getString("advancedmessages.hour")
        val hours: String? = yamlConfiguration.getString("advancedmessages.hours")
        val day: String? = yamlConfiguration.getString("advancedmessages.day")
        val days: String? = yamlConfiguration.getString("advancedmessages.days")
        val month: String? = yamlConfiguration.getString("advancedmessages.month")
        val months: String? = yamlConfiguration.getString("advancedmessages.months")
        val noLastLogin: String? = yamlConfiguration.getString("advancedmessages.no last login")
        return if (second != null && seconds != null && minute != null && minutes != null && hour != null && hours != null
                && day != null && days != null && month != null && months != null && noLastLogin != null) {
            TimeNames(second, seconds, minute, minutes, hour, hours, day, days, month, months, noLastLogin)
        } else {
            TimeNames.createEnglishTimeNames()
        }
    }
}