package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.configuration.*
import com.gmail.fantasticskythrow.messages.config.IWorldRenameConfig
import com.gmail.fantasticskythrow.other.IVanishManager
import com.gmail.fantasticskythrow.other.plugins.IPLMPluginConnector
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.Server
import org.bukkit.entity.Player

class BasicPlaceholderReplacer(chat: Chat?,
                               permission: Permission?,
                               playerLogins: IPlayerLogins,
                               countryAlternateNames: ICountryAlternateNames,
                               vanishManager: IVanishManager,
                               timeNames: TimeNames,
                               server: Server,
                               appConfiguration: IAppConfiguration,
                               pluginConnector: IPLMPluginConnector,
                               worldRenameConfig: IWorldRenameConfig? = null
): IPlaceholderReplacer {

    private val geoInfoReplacer = GeoInfoReplacer(null, countryAlternateNames)
    private val loginStatsReplacer = LoginStatsReplacer(playerLogins)
    private val playerStatsReplacer = PlayerStatsReplacer(worldRenameConfig)
    private val serverStatsReplacer = ServerStatsReplacer(server, vanishManager)
    private val timeReplacer = TimeReplacer(timeNames, playerLogins)

    val playerNameGroupReplacer = PlayerNameGroupReplacer(chat, permission, pluginConnector, appConfiguration)

    override fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean): String {
        var modMessage = message
        modMessage = geoInfoReplacer.replacePlaceholders(modMessage, player, isQuitting)
        modMessage = loginStatsReplacer.replacePlaceholders(modMessage, player, isQuitting)
        modMessage = playerNameGroupReplacer.replacePlaceholders(modMessage, player, isQuitting)
        modMessage = playerStatsReplacer.replacePlaceholders(modMessage, player, isQuitting)
        modMessage = serverStatsReplacer.replacePlaceholders(modMessage, player, isQuitting)
        modMessage = timeReplacer.replacePlaceholders(modMessage, player, isQuitting)
        return modMessage
    }
}