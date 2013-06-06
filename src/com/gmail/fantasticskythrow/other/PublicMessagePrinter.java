package com.gmail.fantasticskythrow.other;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Prints a message for the given player after the given time
 * @author Roman, Morph
 *
 */
public class PublicMessagePrinter extends Thread{
    
    private String[] messages;
    private Player[] receivers;
    
    @Override
    public void run() {
	try { Thread.sleep(100); } catch (Exception e) { }
	try {
	if (receivers != null && messages != null) {
	    for (Player pl : receivers) {
		for (String m : messages) {
		    pl.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
		}
	    }
	}
	} catch (NullPointerException ex) {
	    
	}
    }
    
    public void start(String[] messages, Player[] receivers) {
	this.messages = messages;
	this.receivers = receivers;
	super.start();
    }
}