package com.gmail.fantasticskythrow.messages.listener;

import com.gmail.fantasticskythrow.messages.MessagesModeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kitteh.vanish.event.VanishStatusChangeEvent;

public class VanishStatusChangeEventFakeMessageListener implements Listener {

	private MessagesModeManager messagesModeManager;

	public VanishStatusChangeEventFakeMessageListener(MessagesModeManager messagesModeManager) {
		this.messagesModeManager = messagesModeManager;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		messagesModeManager.onPlayerJoinEvent(e);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEarlyQuitEvent(PlayerQuitEvent e) {
		messagesModeManager.onEarlyQuitEvent(e);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onLatePlayerQuitEvent(PlayerQuitEvent e) {
		messagesModeManager.onLatePlayerQuitEvent(e);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerKickEvent(PlayerKickEvent e) {
		messagesModeManager.onPlayerKickEvent(e);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVanishStatusChangeEvent(VanishStatusChangeEvent e) {
		messagesModeManager.onVanishStatusChangeEvent(e);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
		messagesModeManager.onPlayerCommandPreprocessEvent(e);
	}

}
