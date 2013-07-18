package com.gmail.fantasticskythrow.other;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.gmail.fantasticskythrow.PLM;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Herochat;

public class HerochatManager {

	private Plugin herochat = null;

	public HerochatManager(PLM plugin) {
		herochat = plugin.getServer().getPluginManager().getPlugin("Herochat");
	}

	public boolean sendMessages(String channelname, String[] messages) {
		if (herochat != null) {
			Channel c = Herochat.getChannelManager().getChannel(channelname);
			if (c != null) {
				for (String s : messages) {
					c.sendRawMessage(ChatColor.translateAlternateColorCodes('&', s));
				}
				return true; //Success
			} else {
				return false; //No channel with that name
			}
		} else {
			return false; //Herochat not found
		}
	}

	public boolean sendMessage(String channelname, String message) {
		if (herochat != null) {
			Channel c = Herochat.getChannelManager().getChannel(channelname);
			if (c != null) {
				c.sendRawMessage(ChatColor.translateAlternateColorCodes('&', message));
				return true; //Success
			} else {
				return false; //No channel with that name
			}
		} else {
			return false; //Herochat not found
		}
	}

	public boolean isHerochatInstalled() {
		if (herochat == null)
			return false;
		else
			return true;
	}
}
