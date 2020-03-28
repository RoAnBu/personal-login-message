package com.gmail.fantasticskythrow.messages

import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent

interface ISimpleMessageEventProcessor {
    fun onPlayerJoinEvent(playerJoinEvent: PlayerJoinEvent)
    fun onPlayerQuitEvent(playerQuitEvent: PlayerQuitEvent)
    fun onPlayerKickEvent(playerKickEvent: PlayerKickEvent)
}