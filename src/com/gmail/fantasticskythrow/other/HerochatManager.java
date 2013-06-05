package com.gmail.fantasticskythrow.other;


import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.gmail.fantasticskythrow.PLM;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Herochat;

public class HerochatManager {

    private PLM plugin;
    
    public HerochatManager(PLM plugin) {
	this.plugin = plugin;
    }
    
    public boolean sendMessages(String channelname, String[] messages) {
	Plugin pl = plugin.getServer().getPluginManager().getPlugin("Herochat");
	if (pl != null) {
	    Channel c = Herochat.getChannelManager().getChannel(channelname);
	    if (c != null) {
		for (String s : messages) {
		    c.sendRawMessage(ChatColor.translateAlternateColorCodes('&', s));
		}
		return true;	//Success
	    } else {
		return false;	//No channel with that name
	    }
	}
	else {
	    return false;	//Herochat not found
	}
    }
}
