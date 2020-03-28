package com.gmail.fantasticskythrow.messages.listener;

import com.gmail.fantasticskythrow.messages.ISimpleMessageEventProcessor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CommonListener implements Listener {

	private ISimpleMessageEventProcessor messagesModeManager;

	public CommonListener(ISimpleMessageEventProcessor messagesModeManager) {
		this.messagesModeManager = messagesModeManager;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		messagesModeManager.onPlayerJoinEvent(e);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onLatePlayerQuitEvent(PlayerQuitEvent e) {
		messagesModeManager.onPlayerQuitEvent(e);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerKickEvent(PlayerKickEvent e) {
		messagesModeManager.onPlayerKickEvent(e);
	}
}
