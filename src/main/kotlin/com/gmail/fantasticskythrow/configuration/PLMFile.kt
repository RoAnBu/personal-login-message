package com.gmail.fantasticskythrow.configuration

import com.gmail.fantasticskythrow.other.SavableYaml
import org.bukkit.entity.Player

class PLMFile(private val savableYaml: SavableYaml) : IPluginFirstEnabled, ICountryAlternateNames, IPlayerLogins {
    private val yamlConfiguration = savableYaml.yamlConfiguration

    override fun setPlayerQuitTimeToCurrentTime(player: Player) {
        val path = String.format("Players.%s", player.uniqueId.toString())
        if (yamlConfiguration.contains("Players." + player.name.toLowerCase())) {
            yamlConfiguration["Players." + player.name.toLowerCase()] = null
        }
        yamlConfiguration[path] = System.currentTimeMillis()
    }

    override fun getLastLoginTimeMs(player: Player): Long {
        val path = String.format("Players.%s", player.uniqueId.toString())
        val oldLogin: Long
        if (!yamlConfiguration.contains(path) && yamlConfiguration.contains("Players." + player.name.toLowerCase())) {
            oldLogin = yamlConfiguration.getLong("Players." + player.name.toLowerCase())
            yamlConfiguration["Players." + player.name.toLowerCase()] = null
            yamlConfiguration[path] = oldLogin
        }
        return yamlConfiguration.getLong(path)
    }

    override fun getTimeSinceLastLoginMs(player: Player): Long {
        val lastLogin = getLastLoginTimeMs(player)
        return if (lastLogin != 0L) {
            (System.currentTimeMillis() - lastLogin)
        } else {
            0L
        }
    }

    override fun getPlayerLogins(player: Player): Int {
        val path = String.format("logins.%s", player.uniqueId.toString())
        val oldLogins: Long
        if (yamlConfiguration.contains("logins." + player.name.toLowerCase())) {
            oldLogins = yamlConfiguration.getLong("logins." + player.name.toLowerCase())
            yamlConfiguration["logins." + player.name.toLowerCase()] = null
            yamlConfiguration[path] = oldLogins
        }
        return yamlConfiguration.getInt(path)
    }

    override val totalLogins: Long
        get() = yamlConfiguration.getLong("totallogins")

    override val uniquePlayerLogins: Int
        get() = yamlConfiguration.getInt("uniqueplayers")

    override fun addPlayerLogin(player: Player) {
        val path = String.format("logins.%s", player.uniqueId.toString())
        if (!yamlConfiguration.contains("logins." + player.uniqueId.toString())
                && !yamlConfiguration.contains("logins." + player.name.toLowerCase())) {
            val newUniqueValue = if (yamlConfiguration.contains("uniqueplayers")) {
                yamlConfiguration.getInt("uniqueplayers") + 1
            } else {
                1
            }
            yamlConfiguration["uniqueplayers"] = newUniqueValue
        } else if (yamlConfiguration.contains("logins." + player.name.toLowerCase())) {
            val oldValue = yamlConfiguration.getInt("logins." + player.name.toLowerCase())
            yamlConfiguration["logins." + player.name.toLowerCase()] = null
            yamlConfiguration[path] = oldValue
        }
        val newValue = yamlConfiguration.getInt(path) + 1
        yamlConfiguration[path] = newValue
        val newTotalValue = yamlConfiguration.getLong("totallogins") + 1L
        yamlConfiguration["totallogins"] = newTotalValue
    }

    override val isPluginFirstEnabled: Boolean
        get() = yamlConfiguration.getString("firstenabled").equals("true", ignoreCase = true)

    override fun setFirstEnabled(b: Boolean) {
        if (b) yamlConfiguration["firstenabled"] = "true" else yamlConfiguration["firstenabled"] = "false"
    }

    override fun getAlternateNameForCountry(englishName: String): String {
        return if (yamlConfiguration.contains("Countries$englishName")) {
            yamlConfiguration.getString("Countries$englishName")!!
        } else {
            englishName
        }
    }

    fun save() {
        savableYaml.save()
    }
}