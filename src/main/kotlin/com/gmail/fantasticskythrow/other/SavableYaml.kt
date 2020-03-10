package com.gmail.fantasticskythrow.other

import org.bukkit.configuration.file.YamlConfiguration

interface SavableYaml {
    val yamlConfiguration: YamlConfiguration

    fun save()
}