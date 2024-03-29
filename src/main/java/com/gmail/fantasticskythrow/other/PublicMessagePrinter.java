package com.gmail.fantasticskythrow.other;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;


/**
 * Prints a message for the given player after the given time
 * @author Roman, Morph
 *
 */
public class PublicMessagePrinter extends Thread {

	private List<String> messages;
	private List<Player> receivers;

	@Override
	public void run() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			// TODO refactor
		}
		try {
			if (receivers != null && messages != null) {
				for (Player pl : receivers) {
					for (String m : messages) {
						pl.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
					}
				}
			}
		} catch (NullPointerException ex) {
			// TODO refactor
		}
	}

	public void start(List<String> messages, List<Player> receivers) {
		this.messages = messages;
		this.receivers = receivers;
		super.start();
	}
}