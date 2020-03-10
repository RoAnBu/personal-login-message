package com.gmail.fantasticskythrow.configuration

import org.bukkit.entity.Player

interface IPlayerLogins {
    fun setPlayerQuitTimeToCurrentTime(player: Player)
    fun getLastLoginTimeMs(player: Player): Long
    fun getTimeSinceLastLoginMs(player: Player): Long
    fun getPlayerLogins(player: Player): Int
    fun addPlayerLogin(player: Player)
    val totalLogins: Long
    val uniquePlayerLogins: Int
}