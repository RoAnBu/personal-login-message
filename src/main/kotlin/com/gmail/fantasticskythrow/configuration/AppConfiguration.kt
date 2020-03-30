package com.gmail.fantasticskythrow.configuration

import org.bukkit.configuration.file.YamlConfiguration

class AppConfiguration(yamlConfiguration: YamlConfiguration) : IAdvancedGeneratorAppConfiguration, IAppConfiguration {

    override val pluginEnabled = yamlConfiguration.getBoolean("general.enabled")
    override val usePermissions = yamlConfiguration.getBoolean("general.usePermissions")
    override val useEssentialsNickName = yamlConfiguration.getBoolean("general.useEssentialsNickName")
    override val usePermissionsForPublicMessages = yamlConfiguration.getBoolean("publicMessages.usePermissions")
    override val useRandom = yamlConfiguration.getBoolean("general.useRandomMessageSelection")
    override val useFakeJoinMsg = yamlConfiguration.getBoolean("vanish.usefakejoinmessage")
    override val useFakeQuitMsg = yamlConfiguration.getBoolean("vanish.usefakequitmessage")
    override val replaceVnpFakeMsg = yamlConfiguration.getBoolean("vanish.replaceVNPfakemessages")
    override val welcomeMessagesDelayMs = yamlConfiguration.getInt("welcomeMessages.delayMS")

}
