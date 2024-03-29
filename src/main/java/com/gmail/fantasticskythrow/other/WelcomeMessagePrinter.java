package com.gmail.fantasticskythrow.other;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;


/**
 * Prints a message for the given player after the given time
 * @author FantasticSkyThrow, Morph
 *
 */
public class WelcomeMessagePrinter extends Thread {

	private int time;
	private List<String> messages;
	private Player player;

	@Override
	public void run() {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (String m : messages) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
		}
	}

	public void start(int time, List<String> messages, Player p) {
		if (time >= 0) {
			this.time = time;
		} else {
			this.time = 100;
		}
		this.messages = messages;
		this.player = p;
		super.start();
	}
}
