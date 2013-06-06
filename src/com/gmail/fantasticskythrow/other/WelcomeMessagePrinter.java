package com.gmail.fantasticskythrow.other;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Prints a message for the given player after the given time
 * @author Roman, Morph
 *
 */
public class WelcomeMessagePrinter extends Thread {

	private int time;
	private String[] messages;
	private Player player;

	@Override
	public void run() {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
		}
		for (String m : messages) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
		}
	}

	public void start(int time, String[] messages, Player p) {
		this.time = time;
		this.messages = messages;
		this.player = p;
		super.start();
	}
}
