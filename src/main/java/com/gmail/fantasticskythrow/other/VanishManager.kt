package com.gmail.fantasticskythrow.other

import org.bukkit.entity.Player

class VanishManager: IVanishManager {
    override fun isVanished(name: String?): Boolean {
        return false
    }

    override fun isVanished(player: Player?): Boolean {
        return false
    }

    override fun isPluginInstalled(): Boolean {
        return false
    }
}