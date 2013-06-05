package com.gmail.fantasticskythrow.other;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.messages.PLMFile;

import uk.org.whoami.geoip.GeoIPLookup;

/**
 * A big arrangement of tools for replacing and checking
 * @author Roman
 *
 */
public class PLMToolbox {
    
    /**
     * Checks whether the player has permission 'plm.join'
     * @param p Player - The player to check
     * @return true, if he has permission
     */
    public static boolean getPermissionJoin(boolean usePermGeneral ,Player p) {
	if (usePermGeneral) {
	    if (p.hasPermission("plm.join")) {
		return true;
	    }
	    else {
		return false;
	    }
	}
	else {
	    return true;
	}
    }
    
    /**
     * Checks whether the player has permission 'plm.quit'
     * @param p Player - The player to check
     * @return true, if he has permission
     */
    public static boolean getPermissionQuit(boolean usePermGeneral, Player p) {
	if (usePermGeneral) {
	    if (p.hasPermission("plm.quit")) {
		return true;
	    }
	    else {
		return false;
	    }
	}
	else {
	    return true;
	}
    }
    
    /**
     * Makes the first letter a capital letter and removes '_'
     * @param word - the string you want to be transformed
     * @return the corrected string
     */
    public static String getCapitalWord(String word) {
	String b = "";
	b = b + word.charAt(0);
	word = word.replaceFirst(b, b.toUpperCase());
	if (word.contains("_")) {
	    word = word.replaceAll("_", " ");
	}
	return word;
    }
    
    
    /**
     * Simple replacement of %name
     * @param text - the text which can contain %name
     * @param player - the player whose name is relevant
     * @return the replaced string
     */
    public static String getReplacedPlayername(String text, Player player) {
	return text.replaceAll("%playername", player.getName());
    }
    
    /**
     * Replaces %chatplayername with the name (prefix + name + suffix) taken from Vault|Chat
     * @param text - the string which can contain %chatplayername
     * @param chat - the Chat object
     * @param player - the concerning player
     * @return the replaced string if chat is available. Otherwise it will return the normal playername. %chatplayername won't exist after this.
     */
    public static String getReplacedChatplayername(String text, Chat chat, Player player) {
	if (chat != null) {
	    String name = (String) (chat.getPlayerPrefix(player) + player.getName() + chat.getPlayerSuffix(player));
	    return text.replaceAll("%chatplayername", name);
	} else if (chat == null && text.contains("%chatplayername")){
	    System.out.println("[PLM] PLM was not able to identify a chat format for this player!");
	    return getReplacedPlayername(text.replaceAll("%chatplayername", "%playername"), player);
	} else {
	    return text;
	}
    }
    
    /**
     * Replaces %group with the first group found if a permissions plugin was hooked
     * @param text - the string which can contain %group
     * @param permission - the Permission object
     * @param player - the concerning player
     * @return replaced %group, "unknown" if permission is null
     */
    public static String getReplacedGroup(String text, Permission permission, Player player) {
	if (text.contains("%group") && permission != null) {
	    return text.replaceAll("%group", permission.getPlayerGroups(player)[0]);
	} else if (text.contains("%group") && permission == null) {
	    return text.replaceAll("%group", "unknown group");
	} else {
	    return text;
	}
    }
    
    /**
     * Replaces %world or %World with the world the player joined in
     * @param text - the string which can contain %world/%World
     * @param player - the player which joined/left
     * @return 
     */
    public static String getReplacedWorld(String text, Player player) {
	text = text.replaceAll("%world", player.getWorld().getName());
	text = text.replaceAll("%World", getCapitalWord(player.getWorld().getName()));
	return text;
    }
    
    /**
     * Replaces %country with the country name the player joined from. Changed country names come from plmfile
     * @param text - the string which can contain %country
     * @param plugin - the plm plugin for getting the geoiplookup instance
     * @param player - the player who joined (getting is address)
     * @param plmFile - the plmfile which contains the information for the country names
     * @return replaced %country if possible. Otherwise it will return unknown
     */
    public static String getReplacedCountry(String text, PLM plugin, Player player, PLMFile plmFile) {
	if (text.contains("%country")) {
	    GeoIPLookup geoIP = plugin.getGeoIPLookup();
	    if (geoIP != null) {
		String country = "";
		country = plmFile.getCountryName(geoIP.getCountry(player.getAddress().getAddress()).getName());
		if (country.equalsIgnoreCase("N/A")) {
		    country = "local network";
		}
		text = text.replaceAll("%country", country);
		return text;
	    }
	    else {
		System.out.println("[PLM] You used %country but GeoIPTools is not installed or no database is initialized");
		System.out.println("[PLM] Use /geoupdate if it's installed");
		text = text.replaceAll("%country", "unknown");
		return text;
	    }
	}
	else {
	    return text;
	}
    }
    
    /**
     * 
     * @param text
     * @param vnpHandler
     * @param server
     * @return
     */
    public static String getReplacedPlayerlist(String text, VanishNoPacketManager vnpHandler, Server server) {
	String m = "";
	Player[] playerlist = server.getOnlinePlayers();
	for (int i = 0; i < (playerlist.length - 1); i++) {
	    Player p = playerlist[i];
	    if (!vnpHandler.isVanished(p.getName())) {
		m = m + p.getName() + ", ";
	    }
	}
	Player p = playerlist[playerlist.length - 1];
	if (!vnpHandler.isVanished(p.getName())) {
	    m = m + p.getName();
	} else {
	    StringBuffer s1 = new StringBuffer();
	    s1.append(m);
	    m = s1.reverse().toString();
	    m = m.replaceFirst(" ,", "");
	    StringBuffer s2 = new StringBuffer();
	    s2.append(m);
	    m = s2.reverse().toString();
	}
	return text.replaceAll("%playerlist", m);
    }
    
    /**
     * 
     * @param text
     * @param chat
     * @param vnpHandler
     * @param server
     * @return
     */
    public static String getReplacedChatplayerlist(String text, Chat chat, VanishNoPacketManager vnpHandler, Server server) {
	if (chat != null) {
	    String m = "";
	    Player[] playerlist = server.getOnlinePlayers();
	    for (int i = 0; i < (playerlist.length - 1); i++) {
		Player p = playerlist[i];
		if (!vnpHandler.isVanished(p.getName())) {
		    m = m + (chat.getPlayerPrefix(p) + p.getName() + chat.getPlayerSuffix(p)) + ", ";
		}
	    }
	    Player p = playerlist[playerlist.length - 1];
	    if (!vnpHandler.isVanished(p.getName())) {
		m = m + (chat.getPlayerPrefix(p) + p.getName() + chat.getPlayerSuffix(p));
	    } else {
		StringBuffer s1 = new StringBuffer();
		s1.append(m);
		m = s1.reverse().toString();
		m = m.replaceFirst(" ,", "");
		StringBuffer s2 = new StringBuffer();
		s2.append(m);
		m = s2.reverse().toString();
	    }
	    return text.replaceAll("%chatplayerlist", m);
	} else {
	    return getReplacedPlayerlist(text.replaceAll("%chatplayerlist", "%playerlist"), vnpHandler, server);
	}
    }
    
    /**
     * 
     * @param text
     * @param vnpHandler
     * @param permission
     * @param server
     * @param player
     * @return
     */
    public static String getReplacedGroupplayerlist(String text, VanishNoPacketManager vnpHandler , Permission permission, Server server, Player player) {
	if (permission != null) {
	    String m = "";
	    Player[] playerlist = server.getOnlinePlayers();
	    for (int i = 0; i < (playerlist.length - 1); i++) {
		Player p = playerlist[i];
		if (permission.getPlayerGroups(p)[0] == permission.getPlayerGroups(player)[0] && !vnpHandler.isVanished(p.getName())) {
		    m = m + p.getName() + ", ";
		}
	    }
	    Player p = playerlist[playerlist.length - 1];
	    if (permission.getPlayerGroups(p)[0] == permission.getPlayerGroups(player)[0] && !vnpHandler.isVanished(p.getName())) {
		m = m + p.getName();
	    } else {
		StringBuffer s1 = new StringBuffer();
		s1.append(m);
		m = s1.reverse().toString();
		m = m.replaceFirst(" ,", "");
		StringBuffer s2 = new StringBuffer();
		s2.append(m);
		m = s2.reverse().toString();
	    }
	    return text.replaceAll("%groupplayerlist", m);
	} else {
	    return text.replaceAll("%groupplayerlist", "&4ERROR");
	}
    }
    
/**
 * 
 * @param text
 * @param vnpHandler
 * @param permission
 * @param chat
 * @param server
 * @param player
 * @return
 */
    public static String getReplacedGroupchatplayerlist(String text, VanishNoPacketManager vnpHandler , Permission permission, Chat chat, Server server, Player player) {
	if (permission != null && chat != null) {
	    String m = "";
	    Player[] playerlist = server.getOnlinePlayers();
	    for (int i = 0; i < (playerlist.length - 1); i++) {
		Player p = playerlist[i];
		if (permission.getPlayerGroups(p)[0] == permission.getPlayerGroups(player)[0] && !vnpHandler.isVanished(p.getName())) {
		    m = m + (chat.getPlayerPrefix(p) + p.getName() + chat.getPlayerSuffix(p)) + ", ";
		}
	    }
	    Player p = playerlist[playerlist.length - 1];
	    if (permission.getPlayerGroups(p)[0] == permission.getPlayerGroups(player)[0] && !vnpHandler.isVanished(p.getName())) {
		m = m + (chat.getPlayerPrefix(p) + p.getName() + chat.getPlayerSuffix(p));
	    } else {
		StringBuffer s1 = new StringBuffer();
		s1.append(m);
		m = s1.reverse().toString();
		m = m.replaceFirst(" ,", "");
		StringBuffer s2 = new StringBuffer();
		s2.append(m);
		m = s2.reverse().toString();
	    }
	    return text.replaceAll("%groupchatplayerlist", m);
	} else {
	    return text.replaceAll("%groupchatplayerlist", "&4ERROR");
	}
    }
}
