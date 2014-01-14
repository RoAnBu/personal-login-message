package com.gmail.fantasticskythrow.other;

import java.util.ArrayList;
import java.util.Random;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
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
	 * @param p Player The player to check
	 * @return true, if he has permission
	 */
	public static boolean getPermissionJoin(boolean usePermGeneral, Player p) {
		if (usePermGeneral) {
			if (p.hasPermission("plm.join")) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks whether the player has permission 'plm.quit'
	 * @param p Player The player to check
	 * @return true, if he has permission
	 */
	public static boolean getPermissionQuit(boolean usePermGeneral, Player p) {
		if (usePermGeneral) {
			if (p.hasPermission("plm.quit")) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Makes the first letter a capital letter and removes '_'
	 * @param word the string you want to be transformed
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
	 * Simple replacement of %playername
	 * @param text the text which can contain %name
	 * @param player the player whose name is relevant
	 * @return the replaced string
	 */
	public static String getReplacedPlayername(String text, Player player) {
		return text.replaceAll("%playername", player.getName());
	}

	/**
	 * Replaces %chatplayername with the name (prefix + name + suffix) taken from Vault|Chat
	 * @param text the string which can contain %chatplayername
	 * @param chat the Chat object
	 * @param player the concerning player
	 * @return the replaced string if chat is available. Otherwise it will return the normal playername. %chatplayername won't exist after this.
	 */
	public static String getReplacedChatplayername(String text, Chat chat, Player player) {
		if (chat != null && text.contains("%chatplayername")) {
			String name = (String) (chat.getPlayerPrefix(player) + player.getName() + chat.getPlayerSuffix(player));
			return text.replaceAll("%chatplayername", name);
		} else if (chat == null && text.contains("%chatplayername")) {
			System.out.println("[PLM] PLM was not able to identify a chat format for this player!");
			return getReplacedPlayername(text.replaceAll("%chatplayername", "%playername"), player);
		} else {
			return text;
		}
	}

	/**
	 * Replaces %group with the first group found if a permissions plugin was hooked
	 * @param text the string which can contain %group
	 * @param permission the Permission object
	 * @param player the concerning player
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
	 * @param text the string which can contain %country
	 * @param plugin the plm plugin for getting the geoiplookup instance
	 * @param player the player who joined (getting is address)
	 * @param plmFile the plmfile which contains the information for the country names
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
			} else {
				plugin.getPLMLogger().logWarning("[PLM] You used %country but GeoIPTools is not installed or no database is initialized");
				plugin.getPLMLogger().logWarning("[PLM] Use /geoupdate if it's installed");
				text = text.replaceAll("%country", "unknown");
				return text;
			}
		} else {
			return text;
		}
	}

	/**
	 * Replaces %playerlist with the list of players who are currently online. Vanished players are hidden
	 * @param text the string which can contain %playerlist
	 * @param vnpHandler the VanishNoPacketManager which provides isVanished()
	 * @param server the server taken from the main plugin (PLM - JavaPlugin)
	 * @return the replaced %playerlist
	 */
	public static String getReplacedPlayerlist(String text, VanishNoPacketManager vnpHandler, Server server) {
		if (text.contains("%playerlist")) {
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
		} else {
			return text;
		}
	}

	/**
	 * Replaces %chatplayerlist with the list of players who are currently online in the chatplayername format. Vanished players are hidden
	 * @param text the string which can contain %chatplayerlist
	 * @param chat the Chat object
	 * @param vnpHandler the VanishNoPacketManager which provides isVanished()
	 * @param server the server taken from the main plugin (PLM - JavaPlugin)
	 * @return the replaced %chatplayerlist or %playerlist if chat is null
	 */
	public static String getReplacedChatplayerlist(String text, Chat chat, VanishNoPacketManager vnpHandler, Server server) {
		if (text.contains("chatplayerlist")) {
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
		} else {
			return text;
		}
	}

	/**
	 * Replaces %groupplayerlist with the list of players who are currently online in the same group like the concerning player.
	 * Vanished players are hidden
	 * @param text the string which can contain %groupplayerlist
	 * @param vnpHandler the VanishNoPacketManager which provides isVanished()
	 * @param permission the Permission object taken from Vault
	 * @param server the server taken from the main plugin (PLM - JavaPlugin)
	 * @param player the concerning player to get the first group
	 * @return the replaced %groupplayerlist if a group was found. Otherwise it will return "&4ERROR"
	 */
	public static String getReplacedGroupplayerlist(String text, VanishNoPacketManager vnpHandler, Permission permission, Server server, Player player) {
		if (text.contains("%groupplayerlist")) {
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
		} else {
			return text;
		}
	}

	/**
	 * Replaces %groupchatplayerlist with the list of players who are currently online in the player's group.
	 * The player names will be formatted with Vault's Chat. Note that this doesn't make sense in every case because all names can have the same
	 * format.
	 * @param text the string which can contain %groupchatplayerlist
	 * @param vnpHandler the VanishNoPacketManager which provides isVanished()
	 * @param permission the Permission object taken from Vault
	 * @param chat the Chat object taken from Vault
	 * @param server the server taken from the main plugin (PLM - JavaPlugin)
	 * @param player the concerning player to get the first group
	 * @return the replaced %groupchatplayerlist if permission and chat is available. Otherwise -> '&4ERROR'
	 */
	public static String getReplacedGroupchatplayerlist(String text, VanishNoPacketManager vnpHandler, Permission permission, Chat chat,
			Server server, Player player) {
		if (text.contains("%groupchatplayerlist")) {
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
		} else {
			return text;
		}
	}

	/**
	 * Replaces %logins with the number of times the concerning player joined (The current join, too)
	 * @param text the string which can contain %logins
	 * @param playername the name of the player in lowercase!
	 * @param plmFile
	 * @return the string with replaced %logins
	 */
	public static String getReplacedPlayerLogins(String text, String playername, PLMFile plmFile) {
		if (text.contains("%logins")) {
			text = text.replaceAll("%logins", String.valueOf(plmFile.getPlayerLogins(playername)));
		}
		return text;
	}

	/**
	 * Replaces %totallogins with the total number of logins after started counting
	 * @param text the string which can contain %totallogins
	 * @param plmFile
	 * @return the string with replaced %totallogins
	 */
	public static String getReplacedTotalLogins(String text, PLMFile plmFile) {
		if (text.contains("%totallogins")) {
			text = text.replaceAll("%totallogins", String.valueOf(plmFile.getTotalLogins()));
		}
		return text;
	}

	/**
	 * Replaces %uniqueplayers with the total number of unique logins after started counting
	 * @param text the string which can contain %totallogins
	 * @param plmFile
	 * @return the string with replaced %uniqueplayers
	 */
	public static String getReplacedUniquePlayers(String text, PLMFile plmFile) {
		if (text.contains("%uniqueplayers")) {
			text = text.replaceAll("%uniqueplayers", String.valueOf(plmFile.getUniquePlayerLogins()));
		}
		return text;
	}

	/**
	 * Replaces %onlineplayers with the number of online players (Apart from vanished ones)
	 * @param text The string which can contain %onlineplayers
	 * @param server the server (taken by JavaPlugin)
	 * @param vnpHandler The VanishNoPacketManager for checking vanish status
	 * @return
	 */
	public static String getReplacedOnlinePlayerNumber(String text, Server server, VanishNoPacketManager vnpHandler, boolean isQuitting) {
		if (text.contains("%onlineplayers")) {
			Player[] playerlist = server.getOnlinePlayers();
			int number = 0;
			for (Player p : playerlist) {
				if (!vnpHandler.isVanished(p.getName())) {
					number++;
				}
			}
			if (isQuitting) {
				number--;
			}
			text = text.replaceAll("%onlineplayers", String.valueOf(number));
		}
		return text;
	}

	public static String getReplacedPrefix(String text, Chat chat, Player player) {
		if (chat != null && text.contains("%prefix")) {
			String prefix = chat.getPlayerPrefix(player);
			return text.replaceAll("%prefix", prefix);
		} else if (chat == null && text.contains("%prefix")) {
			System.out.println("[PLM] PLM was not able to identify a prefix for this player!");
			return text.replaceAll("%prefix", "");
		} else {
			return text.replaceAll("%prefix", "");
		}
	}

	public static String getReplacedSuffix(String text, Chat chat, Player player) {
		if (chat != null && text.contains("%suffix")) {
			String suffix = chat.getPlayerSuffix(player);
			return text.replaceAll("%suffix", suffix);
		} else if (chat == null && text.contains("%suffix")) {
			System.out.println("[PLM] PLM was not able to identify a suffix for this player!");
			return text.replaceAll("%suffix", "");
		} else {
			return text.replaceAll("%suffix", "");
		}
	}

	public static String getReplacedStandardPlaceholders(String text, Player player, Chat chat, Permission permission, PLM plugin, PLMFile plmFile,
			VanishNoPacketManager vnpHandler) {
		text = getReplacedPlayername(text, player);
		text = getReplacedChatplayername(text, chat, player);
		text = getReplacedGroup(text, permission, player);
		text = getReplacedWorld(text, player);
		text = getReplacedCountry(text, plugin, player, plmFile);
		text = getReplacedTotalLogins(text, plmFile);
		text = getReplacedUniquePlayers(text, plmFile);
		text = getReplacedPlayerLogins(text, player.getName().toLowerCase(), plmFile);
		text = getReplacedOnlinePlayerNumber(text, plugin.getServer(), vnpHandler, false);
		text = getReplacedPrefix(text, chat, player);
		text = getReplacedSuffix(text, chat, player);
		return text;
	}

	public static String getReplacedComplexPlaceholders(String text, Player player, Chat chat, PLM plugin, PLMFile plmFile,
			VanishNoPacketManager vnpHandler, Permission permission) {
		text = getReplacedPlayername(text, player);
		text = getReplacedChatplayername(text, chat, player);
		text = getReplacedWorld(text, player);
		text = getReplacedCountry(text, plugin, player, plmFile);
		text = getReplacedPlayerlist(text, vnpHandler, plugin.getServer());
		text = getReplacedChatplayerlist(text, chat, vnpHandler, plugin.getServer());
		text = getReplacedGroupplayerlist(text, vnpHandler, permission, plugin.getServer(), player);
		text = getReplacedGroupchatplayerlist(text, vnpHandler, permission, chat, plugin.getServer(), player);
		text = getReplacedGroup(text, permission, player);
		text = getReplacedTotalLogins(text, plmFile);
		text = getReplacedUniquePlayers(text, plmFile);
		text = getReplacedPlayerLogins(text, player.getName().toLowerCase(), plmFile);
		text = getReplacedOnlinePlayerNumber(text, plugin.getServer(), vnpHandler, false);
		text = getReplacedPrefix(text, chat, player);
		text = getReplacedSuffix(text, chat, player);
		return text;
	}

	public static String[] getChannels(String path, YamlConfiguration yml) {
		if (yml.contains(path + ".CH")) {
			String[] channels = yml.getString(path + ".CH").split(", ");
			return channels;
		} else {
			return null;
		}
	}

	public static String getMessage(String path, YamlConfiguration yml) {
		int count = 2;
		while (yml.contains(path + count)) {
			count++;
		}
		Random r = new Random();
		int n = r.nextInt(count - 1) + 1;
		return yml.getString(path + n);
	}

	public static ArrayList<String> getAllMessages(String path, YamlConfiguration yml) {
		ArrayList<String> messages = new ArrayList<String>();
		int count = 1;
		while (yml.contains(path + count)) {
			messages.add(yml.getString(path + count));
			count++;
		}
		return messages;
	}

	public static String getBackMessage(final YamlConfiguration yml, final String path, long difference) {
		String returnMessage = null;
		if (yml.contains(path + ".BM1")) {
			int backMessageCount = 2;
			while (yml.contains(path + ".BM" + backMessageCount)) {
				backMessageCount++;
			}
			boolean a = false;
			int i = 1;
			while (i < backMessageCount && a == false && difference > 0) {
				String currentPath = path + ".BM" + i + "T";
				long time = 0;
				if (yml.contains(currentPath)) {
					try {
						time = Long.parseLong(yml.getString(currentPath)) * 60000;
					} catch (NumberFormatException e) {
						System.out.println("[PLM] Number format at " + currentPath + " is invalid!!");
						time = 0L;
					}
					if (time > 0 && time <= difference) {
						returnMessage = yml.getString(path + ".BM" + i);
						a = true;
					} else if (time < 0 && (time * -1) >= difference) {
						returnMessage = yml.getString(path + ".BM" + i);
						a = true;
					}
				} else {
					//Quit while
					a = true;
					System.out.println("[PLM] Couldn't find the time path for back message " + i + " at " + currentPath + "'s personal section!");
				}
				i++;
			}
		}
		return returnMessage;
	}
}
