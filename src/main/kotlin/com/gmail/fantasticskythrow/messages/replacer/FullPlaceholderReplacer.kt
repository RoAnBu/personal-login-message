package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.configuration.IAppConfiguration
import com.gmail.fantasticskythrow.configuration.ICountryAlternateNames
import com.gmail.fantasticskythrow.configuration.TimeNames
import com.gmail.fantasticskythrow.configuration.PLMFile
import com.gmail.fantasticskythrow.messages.config.IWorldRenameConfig
import com.gmail.fantasticskythrow.other.IVanishManager
import com.gmail.fantasticskythrow.other.plugins.IPLMPluginConnector
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.Server
import org.bukkit.entity.Player

class FullPlaceholderReplacer(
        chat: Chat?,
        permission: Permission?,
        plmFile: PLMFile,
        countryAlternateNames: ICountryAlternateNames,
        vanishManager: IVanishManager,
        timeNames: TimeNames,
        server: Server,
        appConfiguration: IAppConfiguration,
        pluginConnector: IPLMPluginConnector,
        worldRenameConfig: IWorldRenameConfig?
) : IPlaceholderReplacer {

    private val basicPlaceholderReplacer = BasicPlaceholderReplacer(chat, permission, plmFile, countryAlternateNames, vanishManager,
            timeNames, server, appConfiguration, pluginConnector, worldRenameConfig)

    private val advancedPlayerNameGroupReplacer = AdvancedPlayerNameGroupReplacer(chat, permission, server, vanishManager,
            basicPlaceholderReplacer.playerNameGroupReplacer)

    override fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean): String {
        var modMessage = message
        modMessage = basicPlaceholderReplacer.replacePlaceholders(modMessage, player)
        modMessage = advancedPlayerNameGroupReplacer.replacePlaceholders(modMessage, player)
        return modMessage
    }

}