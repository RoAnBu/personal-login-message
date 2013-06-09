package com.gmail.fantasticskythrow.messages;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CommonListener implements Listener {

	private Messages messages;

	public CommonListener(Messages messages) {
		this.messages = messages;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		messages.onPlayerJoinEvent(e);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEarlyQuitEvent(PlayerQuitEvent e) {
		messages.onEarlyQuitEvent(e);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onLatePlayerQuitEvent(PlayerQuitEvent e) {
		messages.onLatePlayerQuitEvent(e);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerKickEvent(PlayerKickEvent e) {
		messages.onPlayerKickEvent(e);
	}
}
