package com.gmail.fantasticskythrow.other;

import com.earth2me.essentials.Essentials;
import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.configuration.AppConfiguration;
import com.gmail.fantasticskythrow.configuration.TimeNames;
import com.gmail.fantasticskythrow.messages.PLMFile;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import uk.org.whoami.geoip.GeoIPLookup;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * A big arrangement of tools for replacing and checking
 * @author FantasticSkyThrow
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
	 * Overwrites messages.txt from Standard mode
	 * @param plugin The main PLM object to find the location of the file
	 * @return true if successful, false in case of an error
	 */
	public static boolean overwriteMessagesFile(PLM plugin) {
		try {
			File messagesFile = new File(plugin.getDataFolder(), "messages.txt");
			FileWriter fw = new FileWriter(messagesFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Join message:");
			bw.newLine();
			bw.write("&e%playername joined the game");
			bw.newLine();
			bw.newLine();
			bw.write("Quit message:");
			bw.newLine();
			bw.write("&e%playername left the game");
			bw.newLine();
			bw.newLine();
			bw.write("How to write own messages:");
			bw.newLine();
			bw.write("Visit http://dev.bukkit.org/bukkit-plugins/personal-login-message/pages/standard-mode/");
			bw.newLine();
			bw.write("NOTE: Please don't move the lines. Otherwise the plugin will return wrong values!!");
			bw.close();
			return true;
		} catch (Exception e) {
			System.out.println("[PLM] Editing 'messages.txt' was not possible! Check the plugin's folder");
			return false;
		}
	}

	/**
	 * Makes the first letter a capital letter and removes '_'
	 * @param word the string you want to be transformed
	 * @return the corrected string
	 */
	private static String getCapitalWord(String word) {
		String b = "";
		b = b + word.charAt(0);
		word = word.replaceFirst(b, b.toUpperCase());
		if (word.contains("_")) {
			word = word.replaceAll("_", " ");
		}
		return word;
	}

	/**
	 * Gets the Essentials nick name like "~Sam" if Essentials is installed and using the nick name is activated in the config
	 * @param player The concerning player
	 * @param plugin The main PLM class for the PLMPluginConnector
	 * @return The nick name if available (if the player doesn't have one it returns the normal name) or null in case of disabled Essentials
	 */
	private static String getEssentialsNick(Player player, PLM plugin) {
		String nickName = null;
		Plugin pl = plugin.getPLMPluginConnector().getEssentials();
		if (pl != null && plugin.getCfg().getUseEssentialsNick()) {
			Essentials essentials = (Essentials) pl;
			try {
				nickName = essentials.getUserMap().getUser(player.getName()).getNickname();

				if (nickName != null && !nickName.equals(player.getName())) {
					nickName = essentials.getSettings().getNicknamePrefix() + nickName;
				}
			} catch (Error e) {
				plugin.getPLMLogger().logError(
						"Could not connect to Essentials! Please use a newer version of Essentials or disable the Essentials connection");
			}
		}
		return nickName;
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
	 * @param plugin the PLM object for essentials
	 * @return the replaced string if chat is available. Otherwise it will return the normal playername. %chatplayername won't exist after this.
	 */
	public static String getReplacedChatplayername(String text, Chat chat, Player player, PLM plugin) {
		if (chat != null && text.contains("%chatplayername")) {
			String name = getEssentialsNick(player, plugin);
			if (name == null) //Use the normal name if no essentials name was available
				name = player.getName();
			String nameResult = (String) (chat.getPlayerPrefix(player) + name + chat.getPlayerSuffix(player));
			return text.replaceAll("%chatplayername", nameResult);
		} else if (chat == null && text.contains("%chatplayername")) {
			System.out.println("[PLM] PLM was not able to identify a chat format for this player!");
			System.out.println("[PLM] Possible reason: No vault compatible chat plugin is available!");
			return getReplacedPlayername(text.replaceAll("%chatplayername", "%playername"), player);
		} else {
			return text;
		}
	}

	/**
	 * Replaces %nickname with the standard name or Essentials nick name (no prefix and suffix!)
	 * @param text the string which can contain %nickname
	 * @param player the concerning player
	 * @param plugin the PLM object for essentials
	 * @return the replaced string
	 */
	public static String getReplacedNickname(String text, Player player, PLM plugin) {
		if (text.contains("%nickname")) {
			String name = getEssentialsNick(player, plugin);
			if (name == null) { //Use the normal name if no essentials name was available
				name = player.getName();
			}
			return text.replaceAll("%nickname", name);
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
			GeoIPLookup geoIP = plugin.getPLMPluginConnector().getGeoIPLookup();
			if (geoIP != null) {
				String country = "";
				country = plmFile.getCountryName(geoIP.getCountry(player.getAddress().getAddress()).getName());
				if (country.equalsIgnoreCase("N/A")) {
					country = "local network";
				}
				text = text.replaceAll("%country", country);
				return text;
			} else {
				plugin.getPLMLogger().logWarning("You used %country but GeoIPTools is not installed or no database is initialized");
				plugin.getPLMLogger().logWarning("Use /geoupdate if it's installed");
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
			Player[] playerlist = server.getOnlinePlayers()
			                            .toArray(new Player[0]);
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
	public static String getReplacedChatplayerlist(String text, Chat chat, VanishNoPacketManager vnpHandler, Server server, PLM plugin) {
		if (text.contains("chatplayerlist")) {
			if (chat != null) {
				String m = "";
				Player[] playerlist = server.getOnlinePlayers()
				                            .toArray(new Player[0]);
				for (int i = 0; i < (playerlist.length - 1); i++) {
					Player p = playerlist[i];
					if (!vnpHandler.isVanished(p.getName())) {
						m = m + getReplacedChatplayername("%chatplayername", chat, p, plugin) + ", ";
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
				Player[] playerlist = server.getOnlinePlayers()
				                            .toArray(new Player[0]);
				for (int i = 0; i < (playerlist.length - 1); i++) {
					Player p = playerlist[i];
					if (permission.getPlayerGroups(p)[0].equals(permission.getPlayerGroups(player)[0]) && !vnpHandler.isVanished(p.getName())) {
						m = m + p.getName() + ", ";
					}
				}
				Player p = playerlist[playerlist.length - 1];
				if (permission.getPlayerGroups(p)[0].equals(permission.getPlayerGroups(player)[0]) && !vnpHandler.isVanished(p.getName())) {
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
			Server server, Player player, PLM plugin) {
		if (text.contains("%groupchatplayerlist")) {
			if (permission != null && chat != null) {
				String m = "";
				Player[] playerlist = server.getOnlinePlayers()
				                            .toArray(new Player[0]);
				for (int i = 0; i < (playerlist.length - 1); i++) {
					Player p = playerlist[i];
					if (permission.getPlayerGroups(p)[0].equals(permission.getPlayerGroups(player)[0]) && !vnpHandler.isVanished(p.getName())) {
						m = m + getReplacedChatplayername("%chatplayername", chat, p, plugin) + ", ";
					}
				}
				Player p = playerlist[playerlist.length - 1];
				if (permission.getPlayerGroups(p)[0].equals(permission.getPlayerGroups(player)[0]) && !vnpHandler.isVanished(p.getName())) {
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
	 * @param
	 * @param
	 * @return the string with replaced %logins
	 */
	public static String getReplacedPlayerLogins(String text, Player player, PLMFile plmFile) {
		if (text.contains("%logins")) {
			text = text.replaceAll("%logins", String.valueOf(plmFile.getPlayerLogins(player)));
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
			Collection<? extends Player> playerlist = server.getOnlinePlayers();
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

	/**
	 * Replaces the %prefix placeholder with the vault prefix if available. In case of Vault is not enabled it replaces %prefix by "" and outputs a warning
	 * @param text The string which could contain %prefix
	 * @param chat The vault chat plugin
	 * @param player The concerning player
	 * @return The modified string
	 */
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

	/**
	 * Replaces the %suffix placeholder with the vault prefix if available. In case of Vault is not enabled it replaces %suffix by "" and outputs a warning
	 * @param text The string which could contain %suffix
	 * @param chat The vault chat plugin
	 * @param player The concerning player
	 * @return The modified string
	 */
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

	/**
	 * Replaces %slots by the number of slots
	 * @param text The string which could contain %slots
	 * @param server The server the plugin is running on
	 * @return The modified string
	 */
	public static String getReplacedSlots(String text, Server server) {
		if (text.contains("%slots")) {
			text = text.replaceAll("%slots", String.valueOf(server.getMaxPlayers()));
		}
		return text;
	}

	/**
	 * Replaces %levels by the number of levels
	 * @param text The string which could contain %levels
	 * @param player The concerning player
	 * @return The modified string
	 */
	public static String getReplacedLevels(String text, Player player) {
		if (text.contains("%levels")) {
			text = text.replaceAll("%levels", String.valueOf(player.getLevel()));
		}
		return text;
	}

	/**
	 * Replaces %health and %comparedHealth by the concerning values. Compared health looks like this "health/maximum health"
	 * @param text The text which could contain %health or %comparedHealth
	 * @param player The concerning player
	 * @return The modified string
	 */
	public static String getReplacedHealth(String text, Player player) {
		if (text.contains("%health")) {
			Damageable d = (Damageable) player;
			text = text.replaceAll("%health", String.valueOf(d.getHealth()));
		}
		if (text.contains("%comparedHealth")) {
			Damageable d = (Damageable) player;
			text = text.replaceAll("%comparedHealth", String.valueOf(d.getHealth()) + "/" + String.valueOf(d.getMaxHealth()));
		}
		return text;
	}

	/**
	 * Replaces %IP by the player's address. The / in front of it will be deleted
	 * @param text The text which could contain %IP
	 * @param player The concerning player
	 * @return The modified string
	 */
	public static String getReplacedIP(String text, Player player) {
		if (text.contains("%IP")) {
			text = text.replaceAll("%IP", player.getAddress().toString().replaceAll("/", ""));
		}
		return text;
	}

	/**
	 * Replaces %gamemode by the current gamemode. Just using the English expressions like "Survival", "Creative" and "Adventure".
	 * @param text The text which could contain %gamemode
	 * @param player The concerning player
	 * @return The modified string
	 */
	public static String getReplacedGamemode(String text, Player player) {
		if (text.contains("%gamemode")) {
			text = text.replaceAll("%gamemode", WordUtils.capitalize(player.getGameMode().toString().toLowerCase()));
		}
		return text;
	}

	/**
	 * Replaces %food by the current food level
	 * @param text The text which could contain %food
	 * @param player The concerning player
	 * @return The modified string
	 */
	public static String getReplacedFood(String text, Player player) {
		if (text.contains("%food")) {
			text = text.replaceAll("%food", String.valueOf(player.getFoodLevel()));
		}
		return text;
	}

	/**
	 * Replaces the placeholders %playername, %chatplayername, %group, %world, %country, %totallogins, %uniqueplayers, %logins, %onlineplayers, %prefix, %suffix, %levels, %slots, %health, %comparedHealth, %food, %IP, %gamemode
	 * @param text the text which can contain the placeholders
	 * @param player the concerning player
	 * @param chat the chat hook by vault. Can be null if not available
	 * @param permission the permission hook by vault. Can be null if not available
	 * @param plugin The main PLM class
	 * @param plmFile The plmFile for information about quit time...
	 * @param vnpHandler The VanishNoPacketManager for the lists
	 * @return
	 */
	public static String getReplacedStandardPlaceholders(String text, Player player, Chat chat, Permission permission, PLM plugin, PLMFile plmFile,
			VanishNoPacketManager vnpHandler) {
		text = getReplacedPlayername(text, player);
		text = getReplacedChatplayername(text, chat, player, plugin);
		text = getReplacedNickname(text, player, plugin);
		text = getReplacedGroup(text, permission, player);
		text = getReplacedWorld(text, player);
		text = getReplacedCountry(text, plugin, player, plmFile);
		text = getReplacedTotalLogins(text, plmFile);
		text = getReplacedUniquePlayers(text, plmFile);
		text = getReplacedPlayerLogins(text, player, plmFile);
		text = getReplacedOnlinePlayerNumber(text, plugin.getServer(), vnpHandler, false);
		text = getReplacedPrefix(text, chat, player);
		text = getReplacedSuffix(text, chat, player);
		text = getReplacedSlots(text, plugin.getServer());
		text = getReplacedLevels(text, player);
		text = getReplacedHealth(text, player);
		text = getReplacedIP(text, player);
		text = getReplacedGamemode(text, player);
		text = getReplacedFood(text, player);
		return text;
	}

	/**
	 * Replaces the placeholders %playername, %chatplayername, %group, %world, %country, %totallogins, %uniqueplayers, %logins, %onlineplayers, %prefix, %suffix, , %levels, %slots, %health, %comparedHealth, %food, %IP, %gamemode, 
	 *  %playerlist, %chatplayerlist, %groupplayerlist, %groupchatplayerlist
	 * @param text the text which can contain the placeholders
	 * @param player the concerning player
	 * @param chat the chat hook by vault. Can be null if not available
	 * @param plugin The main PLM class
	 * @param plmFile The plmFile for information about quit time...
	 * @param vnpHandler The VanishNoPacketManager for the lists
	 * @param permission the permission hook by vault. Can be null if not available
	 * @return
	 */
	public static String getReplacedComplexPlaceholders(String text, Player player, Chat chat, PLM plugin, PLMFile plmFile,
			VanishNoPacketManager vnpHandler, Permission permission) {
		text = getReplacedPlayername(text, player);
		text = getReplacedChatplayername(text, chat, player, plugin);
		text = getReplacedNickname(text, player, plugin);
		text = getReplacedWorld(text, player);
		text = getReplacedCountry(text, plugin, player, plmFile);
		text = getReplacedPlayerlist(text, vnpHandler, plugin.getServer());
		text = getReplacedChatplayerlist(text, chat, vnpHandler, plugin.getServer(), plugin);
		text = getReplacedGroupplayerlist(text, vnpHandler, permission, plugin.getServer(), player);
		text = getReplacedGroupchatplayerlist(text, vnpHandler, permission, chat, plugin.getServer(), player, plugin);
		text = getReplacedGroup(text, permission, player);
		text = getReplacedTotalLogins(text, plmFile);
		text = getReplacedUniquePlayers(text, plmFile);
		text = getReplacedPlayerLogins(text, player, plmFile);
		text = getReplacedOnlinePlayerNumber(text, plugin.getServer(), vnpHandler, false);
		text = getReplacedPrefix(text, chat, player);
		text = getReplacedSuffix(text, chat, player);
		text = getReplacedSlots(text, plugin.getServer());
		text = getReplacedLevels(text, player);
		text = getReplacedHealth(text, player);
		text = getReplacedIP(text, player);
		text = getReplacedGamemode(text, player);
		text = getReplacedFood(text, player);
		return text;
	}

	/**
	 * Looks for channels in the given section
	 * @param path the path e.g. "Groups.Admin" without dot and additional endings
	 * @param yml The concerning file
	 * @return The channels or null if no channels have been found
	 */
	public static String[] getChannels(String path, YamlConfiguration yml) {
		if (yml.contains(path + ".CH")) {
			String[] channels = yml.getString(path + ".CH").split(", ");
			return channels;
		} else {
			return null;
		}
	}

	/**
	 * Looks for a string (here: message) in the given section. If there is more than one string it chooses on randomly.
	 * Note: Do not call this method if you haven't checked whether any message is available
	 * @param path the path e.g. "Groups.Admin.JM" without numbers at the ending
	 * @param yml The concerning file
	 * @return the string with the message
	 */
	public static String getMessage(String path, YamlConfiguration yml) {
		int count = 2;
		while (yml.contains(path + count)) {
			count++;
		}
		Random r = new Random();
		int n = r.nextInt(count - 1) + 1;
		return yml.getString(path + n);
	}

	/**
	 * Looks for strings (here: messages) in the given section and returns any possible message in an array
	 * @param path the basic path with endings but no numbers
	 * @param yml the concerning file
	 * @return the string array containing all messages
	 */
	public static ArrayList<String> getAllMessages(String path, YamlConfiguration yml) {
		ArrayList<String> messages = new ArrayList<String>();
		int count = 1;
		while (yml.contains(path + count)) {
			messages.add(yml.getString(path + count));
			count++;
		}
		return messages;
	}

	/**
	 * Looks for a fitting back message
	 * @param yml the concerning file
	 * @param path the basic path without .BM<number>
	 * @param difference the difference between logout and the login
	 * @return the back message or null if not available
	 */
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

	/**
	 * Replaces %time with the period the player was offline
	 * 
	 * @param message the message containing the time constant
	 * @return the message without %time
	 */
	public static String getReplacedTime(String message, AppConfiguration cfg, PLMFile plmFile, Player player) {
		TimeNames tn = cfg.getTimeNames();
		if (tn == null) {
			return message;
		}
		String second = tn.getSecond();
		String seconds = tn.getSeconds();
		String minute = tn.getMinute();
		String minutes = tn.getMinutes();
		String hour = tn.getHour();
		String hours = tn.getHours();
		String day = tn.getDay();
		String days = tn.getDays();
		String month = tn.getMonth();
		String months = tn.getMonths();
		String noLastLogin = tn.getNoLastLogin();
		long difference;
		if (plmFile.getLastLogin(player) == 0L) {
			difference = 0L;
		} else {
			difference = System.currentTimeMillis() - plmFile.getLastLogin(player);
		}
		// No Data
		if (difference == 0L) {
			message = message.replaceAll("%time", noLastLogin);
		}
		// Less than 1 minute and is not 0
		if (difference < 60000L && difference != 0) {
			long a = difference / 1000L;
			if (a == 1L) {
				message = message.replaceAll("%time", a + " " + second);
			} else {
				message = message.replaceAll("%time", a + " " + seconds);
			}
		}
		// More than 1 minute and less than 1 hour
		if (difference >= 60000L && difference < 3600000L) {
			long a = difference / 60000L;
			if (a == 1L) {
				message = message.replaceAll("%time", a + " " + minute);
			} else {
				message = message.replaceAll("%time", a + " " + minutes);
			}
		}
		// More than 1 hour and less than 1 day
		if (difference >= 3600000L && difference < 86400000L) {
			long a = difference / 60000L;
			long rest = a % 60;
			a = a / 60;
			if (a == 1L && rest == 0L) {
				message = message.replaceAll("%time", a + " " + hour);
			} else if (rest == 0L) {
				message = message.replaceAll("%time", a + " " + hours);
			} else if (a == 1L && rest == 1L) {
				message = message.replaceAll("%time", a + " " + hour + " " + rest + " " + minute);
			} else if (a == 1L) {
				message = message.replaceAll("%time", a + " " + hour + " " + rest + " " + minutes);
			} else if (rest == 1L) {
				message = message.replaceAll("%time", a + " " + hours + " " + rest + " " + minute);
			} else {
				message = message.replaceAll("%time", a + " " + hours + " " + rest + " " + minutes);
			}
		}
		// More than 1 day and less than 10 days
		if (difference >= 86400000L && difference < 864000000L) {
			long a = difference / 3600000L;
			long rest = a % 24;
			a = a / 24;
			if (a == 1L && rest == 0L) {
				message = message.replaceAll("%time", a + " " + day);
			} else if (rest == 0L) {
				message = message.replaceAll("%time", a + " " + days);
			} else if (a == 1L && rest == 1L) {
				message = message.replaceAll("%time", a + " " + day + " " + rest + " " + hour);
			} else if (a == 1L) {
				message = message.replaceAll("%time", a + " " + day + " " + rest + " " + hours);
			} else if (rest == 1L) {
				message = message.replaceAll("%time", a + " " + days + " " + rest + " " + hour);
			} else {
				message = message.replaceAll("%time", a + " " + days + " " + rest + " " + hours);
			}
		}
		// More than 10 days and less than 30 days
		if (difference >= 864000000L && difference < 2592000000L) {
			long a = difference / 86400000L;
			if (a == 1L) {
				message = message.replaceAll("%time", a + " " + day);
			} else {
				message = message.replaceAll("%time", a + " " + days);
			}
		}
		// More than 1 month (30 days)
		if (difference >= 2592000000L) {
			long a = difference / 2592000000L;
			if (a == 1L) {
				message = message.replaceAll("%time", a + " " + month);
			} else {
				message = message.replaceAll("%time", a + " " + months);
			}
		}
		return message;
	}

	public static YamlConfiguration loadPLMFile(final File PLMFileData, final PLM plugin) {
		YamlConfiguration PConfig = YamlConfiguration.loadConfiguration(PLMFileData);
		if (!PConfig.contains("firstenabled")) {
			PConfig.set("firstenabled", "false");
		}
		if (!PConfig.contains("Countries")) {
			PConfig.set("Countries.United States", "United States");
			PConfig.set("Countries.France", "France");
			PConfig.set("Countries.Germany", "Germany");
			PConfig.set("Countries.Brazil", "Brazil");
			PConfig.set("Countries.Netherlands", "Netherlands");
			PConfig.set("Countries.United Kingdom", "United Kingdom");
			PConfig.set("Countries.Slovenia", "Slovenia");
			PConfig.set("Countries.Bulgaria", "Bulgaria");
			PConfig.set("Countries.Canada", "Canada");
			PConfig.set("Countries.Mexico", "Mexico");
			PConfig.set("Countries.Italy", "Italy");
			PConfig.set("Countries.Spain", "Spain");
			PConfig.set("Countries.Australia", "Australia");
			PConfig.set("Countries.India", "India");
			PConfig.set("Countries.Russian Federation", "Russian Federation");
			PConfig.set("Countries.Your Country", "Your Country");
		}
		if (!PConfig.contains("totallogins")) {
			PConfig.set("totallogins", 0L);
		}
		if (!PConfig.contains("uniqueplayers")) {
			PConfig.set("uniqueplayers", 0);
		}
		try {
			PConfig.save(PLMFileData);
			return PConfig;
		} catch (FileNotFoundException ex) {
			PLMLogger plmLogger = plugin.getPLMLogger();
			plmLogger.logError(ex.getMessage());
			plmLogger.logError("PLM.yml is not available!");
			plmLogger.logInfo("Please check whether PLM is permitted to write in PLM.yml!");
			return null;
		} catch (IOException e) {
			PLMLogger plmLogger = plugin.getPLMLogger();
			plmLogger.logError(e.getMessage());
			plmLogger.logError("PLM.yml is not available!");
			plmLogger.logInfo("Please check whether PLM is permitted to write in PLM.yml!");
			return null;
		}
	}

	/**
	 * Use this function to compare server versions
	 * @param plugin the PLM object
	 * @return the version as an integer e.g.: 1.6.4-R2.0 -> 164
	 */
	public static int getMinecraftVersion(PLM plugin) {
		String version = plugin.getServer().getBukkitVersion().split("-")[0];
		version = version.replaceAll("\\.", "");
		int versionNumber = 0;
		try {
			versionNumber = Integer.parseInt(version);
		} catch (NumberFormatException ne) {
			PLMLogger plmLogger = plugin.getPLMLogger();
			plmLogger.logError("An error occurred while analysing the Minecraft server version!");
			plmLogger.logError("Please report this problem as fast as possible");
		}
		return versionNumber;
	}

}
