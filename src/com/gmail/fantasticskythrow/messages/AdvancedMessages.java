package com.gmail.fantasticskythrow.messages;

import java.io.File;
import java.io.IOException;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.configuration.ExtendedYamlConfiguration;

public class AdvancedMessages {

	private PLM plugin;
	private File advancedMessagesData;
	private YamlConfiguration advancedMessagesYML;
	private PLMFile settings;
	private String message;
	private Permission permission;
	private boolean errorStatus = false;
	private PlayerSection playerS;
	private GroupSection groupS;
	private DefaultSection defaultS;
	private String[] welcomeMessages, publicMessages;
	private final String[] emptyMessages;

	public AdvancedMessages(PLM p, PLMFile f) {
		settings = f;
		plugin = p;
		loadAdvancedMessagesFile();
		emptyMessages = null;
		if (!errorStatus) {
			playerS = new PlayerSection();
			groupS = new GroupSection();
			defaultS = new DefaultSection();
		}
		permission = plugin.getPermission();
	}

	private void loadAdvancedMessagesFile() {
		try {
			advancedMessagesData = new File(plugin.getDataFolder(), "AdvancedMessages.yml");
			advancedMessagesYML = ExtendedYamlConfiguration.loadConfiguration(advancedMessagesData);
			if (settings.getFirstEnabled() == false) {
				advancedMessagesYML.set("Default.JM1", "%chatplayername &ejoined the game");
				advancedMessagesYML.set("Default.QM1", "%chatplayername &eleft the game");
				advancedMessagesYML.set("Groups.examplegroup.JM1", "&4Admin %playername joined the game");
				advancedMessagesYML.set("Groups.examplegroup.QM1", "&4Admin %playername left the game");
				advancedMessagesYML.set("Groups.examplegroup.BM1", "&4Admin &a%playername is back after more than one day!");
				advancedMessagesYML.set("Groups.examplegroup.BM1T", "1440");
				advancedMessagesYML.set("Groups.examplegroup.BM2", "&4Admin &a%playername is back after some hours");
				advancedMessagesYML.set("Groups.examplegroup.BM2T", "60");
				advancedMessagesYML.set("Groups.examplegroup.BM3", "&4Admin &a%playername is back after less than one hour!");
				advancedMessagesYML.set("Groups.examplegroup.BM3T", "-59");
				advancedMessagesYML.set("players.exampleplayer.JM1", "&6The king Peter &2joined the server!");
				advancedMessagesYML.set("players.exampleplayer.QM1", "&6The king Peter &2left the server!");
				advancedMessagesYML.set("players.exampleplayer.JM2", "&2Our premium player Peter logged in!");
				advancedMessagesYML.set("players.exampleplayer.QM2", "&2Our premium player Peter is now offline!");
				advancedMessagesYML.set("World names.exampleworld", "main world");
				settings.setFirstEnabled(true);
				advancedMessagesYML.save(advancedMessagesData);
			}
			if (advancedMessagesYML.contains("Default.FM")) {
				String m = advancedMessagesYML.getString("Default.FM");
				advancedMessagesYML.set("Default.FM", null);
				advancedMessagesYML.set("Default.FM1", m);
				advancedMessagesYML.save(advancedMessagesData);
			}
		} catch (IllegalStateException ex) {
			errorStatus = true;
			System.out.println("[PLM] SEVERE! Couldn't read AdvancedMessages.yml");
			System.out.println("[PLM] Please make sure that you used ' in front any special letter (%, &,...)");
			System.out.println("[PLM] More information on PLM's BukkitDev page!");
		} catch (IOException io) {
			System.out.println("[PLM] An error occurred while saving AdvancedMessages.yml");
			System.out.println("[PLM] Please check whether PLM has all rights to do this!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Join Section

	/**
	 * 
	 * @param p
	 * @return
	 */
	protected String getJoinMessage(Player p) {
		/*
		 * Error -> Skips message checking
		 */
		if (errorStatus == true) {
			return "&4An error occurred at the messages file! &e%playername joined";
		} else
		/*
		 * No error -> Looking for fitting message
		 */
		{
			String playername = p.getName().toLowerCase();
			String playerpath = String.format("players.%s", playername);
			String groupname = permission.getPlayerGroups(p)[0];
			String grouppath = String.format("Groups.%s", groupname);

			long difference;
			if (settings.getLastLogin(playername) != 0L) {
				difference = System.currentTimeMillis() - settings.getLastLogin(playername);
			} else {
				difference = 0L;
			}
			if (playerS.checkFirstMessage(playerpath, difference, advancedMessagesYML, this)) {
				message = getReplacedWorld(message, p);
				return message;
			} else if (groupS.checkFirstMessage(grouppath, difference, advancedMessagesYML, this)) {
				message = getReplacedWorld(message, p);
				return message;
			} else if (defaultS.checkFirstMessage(difference, advancedMessagesYML, this)) {
				message = getReplacedWorld(message, p);
				return message;
			} else if (playerS.checkMessagesJoin(playername, playerpath, advancedMessagesYML, difference, this)) {
				message = getReplacedWorld(message, p);
				return message;
			} else if (groupS.checkMessagesJoin(grouppath, advancedMessagesYML, difference, this)) {
				message = getReplacedWorld(message, p);
				return message;
			} else if (defaultS.checkMessagesJoin(advancedMessagesYML, difference, this)) {
				message = getReplacedWorld(message, p);
				return message;
			} else {
				System.out.println("[PLM] No path found for " + p.getName() + ". Using default messages");
				return "&e%playername joined the game";
			}
		}
	}

	//Quit Section

	/**
	 * 
	 * @param p
	 * @return
	 */
	protected String getQuitMessage(Player p) {
		String playername = p.getName().toLowerCase();
		String playerpath = String.format("players.%s", playername);
		String groupname = permission.getPlayerGroups(p)[0];
		String grouppath = String.format("Groups.%s", groupname);
		if (errorStatus == true) {
			return "&e%playername left the game";
		} else if (playerS.checkMessagesQuit(playername, playerpath, advancedMessagesYML, this)) {
			message = getReplacedWorld(message, p);
			return message;
		} else if (groupS.checkMessagesQuit(grouppath, advancedMessagesYML, this)) {
			message = getReplacedWorld(message, p);
			return message;
		} else if (defaultS.checkMessagesQuit(advancedMessagesYML, this)) {
			message = getReplacedWorld(message, p);
			return message;
		} else {
			System.out.println("[PLM] No path found for " + p.getName() + ". Using default messages");
			return "&e%playername left the game";
		}
	}

	private String getReplacedWorld(String message, Player p) {
		if (advancedMessagesYML.contains("World names." + p.getWorld().getName())) {
			message = message.replaceAll("%world", advancedMessagesYML.getString("World names." + p.getWorld().getName()));
			message = message.replaceAll("%World", advancedMessagesYML.getString("World names." + p.getWorld().getName()));
		}
		return message;
	}

	protected void setMessage(String message) {
		if (message != "") {
			this.message = message;
		} else {
			message = "&e%playername joined the game";
		}
	}

	/*
	 * Welcome messages
	 */

	protected String[] getWelcomeMessages(Player p) {
		String groupname = permission.getPlayerGroups(p)[0];
		String grouppath = String.format("Groups.%s", groupname);
		String playername = p.getName().toLowerCase();
		String playerpath = String.format("players.%s", playername);
		if (errorStatus == true) {
			return emptyMessages;
		} else if (playerS.checkWelcomeMessages(playerpath, advancedMessagesYML, this)) {
			for (int i = 0; i < welcomeMessages.length; i++) {
				welcomeMessages[i] = getReplacedWorld(welcomeMessages[i], p);
			}
			return welcomeMessages;
		} else if (groupS.checkWelcomeMessages(grouppath, advancedMessagesYML, this)) {
			for (int i = 0; i < welcomeMessages.length; i++) {
				welcomeMessages[i] = getReplacedWorld(welcomeMessages[i], p);
			}
			return welcomeMessages;
		} else if (defaultS.checkWelcomeMessages(advancedMessagesYML, this)) {
			for (int i = 0; i < welcomeMessages.length; i++) {
				welcomeMessages[i] = getReplacedWorld(welcomeMessages[i], p);
			}
			return welcomeMessages;
		} else {
			return emptyMessages;
		}
	}

	protected void setWelcomeMessages(String[] messages) {
		this.welcomeMessages = messages;
	}

	protected String[] getPublicMessages(Player p) {
		String groupname = permission.getPlayerGroups(p)[0];
		String grouppath = String.format("Groups.%s", groupname);
		String playername = p.getName().toLowerCase();
		String playerpath = String.format("players.%s", playername);
		if (errorStatus == true) {
			return emptyMessages;
		} else if (playerS.checkPublicMessages(playerpath, advancedMessagesYML, this)) {
			for (int i = 0; i < publicMessages.length; i++) {
				publicMessages[i] = getReplacedWorld(publicMessages[i], p);
			}
			return publicMessages;
		} else if (groupS.checkPublicMessages(grouppath, advancedMessagesYML, this)) {
			for (int i = 0; i < publicMessages.length; i++) {
				publicMessages[i] = getReplacedWorld(publicMessages[i], p);
			}
			return publicMessages;
		} else if (defaultS.checkPublicMessages(advancedMessagesYML, this)) {
			for (int i = 0; i < publicMessages.length; i++) {
				publicMessages[i] = getReplacedWorld(publicMessages[i], p);
			}
			return publicMessages;
		} else {
			return emptyMessages;
		}
	}

	protected void setPublicMessages(String[] publicMessages) {
		this.publicMessages = publicMessages;
	}
}
