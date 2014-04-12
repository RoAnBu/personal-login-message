package com.gmail.fantasticskythrow.messages;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.configuration.ExtendedYamlConfiguration;
import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMLogger;
import com.gmail.fantasticskythrow.other.SectionSubTypes;
import com.gmail.fantasticskythrow.other.SectionTypes;

public class AdvancedMessages {

	private PLM plugin;
	private File advancedMessagesData;
	private YamlConfiguration advancedMessagesYML;
	private PLMFile settings;
	private MessageData mData;
	private Permission permission;
	private boolean errorStatus = false;
	private String[] welcomeMessages, publicMessages;
	private final String[] emptyMessages;
	private final PLMLogger plmLogger;

	public AdvancedMessages(PLM p, PLMFile f) {
		settings = f;
		plugin = p;
		plmLogger = plugin.getPLMLogger();
		loadAdvancedMessagesFile();
		emptyMessages = null;
		permission = plugin.getPermission();
	}

	private void loadAdvancedMessagesFile() {
		try {
			advancedMessagesData = new File(plugin.getDataFolder(), "AdvancedMessages.yml");
			advancedMessagesYML = ExtendedYamlConfiguration.loadConfiguration(advancedMessagesData);
			if (settings.getFirstEnabled() == false || !advancedMessagesData.exists()) {
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
				advancedMessagesYML.set("Groups.examplegroup.CH", "Messages, Information");
				advancedMessagesYML.set("players.exampleplayer.JM1", "&6The king Peter &2joined the server!");
				advancedMessagesYML.set("players.exampleplayer.QM1", "&6The king Peter &2left the server!");
				advancedMessagesYML.set("players.exampleplayer.JM2", "&2Our premium player Peter logged in!");
				advancedMessagesYML.set("players.exampleplayer.QM2", "&2Our premium player Peter is now offline!");
				advancedMessagesYML.set("players.exampleplayer2.JM1", "&aThis is the public join message");
				advancedMessagesYML.set("players.exampleplayer2.QM1", "&aThis is the public quit/leave message");
				advancedMessagesYML.set("players.exampleplayer2.WM1", "&eThis message is for the player who is joining");
				advancedMessagesYML.set("players.exampleplayer2.WM2", "&2This would be another line just under WM1");
				advancedMessagesYML.set("players.exampleplayer2.PM1", "&eThis is a message for the other players on the server.");
				advancedMessagesYML.set("players.exampleplayer2.PM2", "&aYou can create more than one here, too");
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
			plmLogger.logError("[PLM] Couldn't read AdvancedMessages.yml");
			plmLogger.logInfo("[PLM] Please make sure that you used ' in front any special letter (%, &,...)");
			plmLogger.logInfo("[PLM] More information on PLM's BukkitDev page!");
		} catch (IOException io) {
			plmLogger.logError("[PLM] An error has occurred while saving AdvancedMessages.yml");
			plmLogger.logInfo("[PLM] Please check whether PLM has all rights to do this!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Join Section

	protected MessageData getJoinMessage(Player p) {
		/*
		 * Error -> Skips message checking
		 */
		if (errorStatus == true) {
			return new MessageData("&4An error has occurred at the messages file! &e%playername joined", null, SectionTypes.ERROR,
					SectionSubTypes.FILEERROR);
		} else
		/*
		 * No error -> Looking for fitting message
		 */
		{
			String playername = p.getName().toLowerCase();
			String playerpath = String.format("players.%s", playername);
			String groupname = permission.getPlayerGroups(p)[0];
			String grouppath = String.format("Groups.%s", groupname);

			long difference = settings.getDifference(playername);
			if (PlayerSection.checkFirstMessage(playerpath, difference, advancedMessagesYML, this)) { //Player section, first message
			} else if (GroupSection.checkFirstMessage(grouppath, difference, advancedMessagesYML, this)) { //Group section, first message
			} else if (DefaultSection.checkFirstMessage(difference, advancedMessagesYML, this)) { //Default section, first message
			} else if (PlayerSection.checkMessagesJoin(playername, playerpath, advancedMessagesYML, difference, this)) { //Player section, back/join message
			} else if (GroupSection.checkMessagesJoin(grouppath, advancedMessagesYML, difference, this)) { //Group section, back/join message
			} else if (DefaultSection.checkMessagesJoin(advancedMessagesYML, difference, this)) { //Default section, back/join message
			} else {
				plmLogger.logWarning("[PLM] No path found for " + p.getName() + ". Using default messages");
				mData = new MessageData("&e%playername joined the game", null, SectionTypes.ERROR, SectionSubTypes.NOPATH);
			}
			mData.message = getReplacedWorld(mData.message, p);
			return mData;
		}
	}

	//Quit Section

	protected MessageData getQuitMessage(Player p) {
		String playername = p.getName().toLowerCase();
		String playerpath = String.format("players.%s", playername);
		String groupname = permission.getPlayerGroups(p)[0];
		String grouppath = String.format("Groups.%s", groupname);
		if (errorStatus == true) {
			mData = new MessageData("&e%playername left the game", null, SectionTypes.ERROR, SectionSubTypes.FILEERROR);
		} else if (PlayerSection.checkMessagesQuit(playername, playerpath, advancedMessagesYML, this)) {
		} else if (GroupSection.checkMessagesQuit(grouppath, advancedMessagesYML, this)) {
		} else if (DefaultSection.checkMessagesQuit(advancedMessagesYML, this)) {
		} else {
			plmLogger.logWarning("[PLM] No path found for " + p.getName() + ". Using default messages");
			mData = new MessageData("&e%playername left the game", null, SectionTypes.ERROR, SectionSubTypes.NOPATH);
		}
		mData.message = getReplacedWorld(mData.message, p);
		return mData;
	}

	protected MessageData getRandomJoinMessage(Player p) {
		/*
		 * Error -> Skips message checking
		 */
		if (errorStatus == true) {
			return new MessageData("&4An error has occurred at the messages file! &e%playername joined", null, SectionTypes.ERROR,
					SectionSubTypes.FILEERROR);
		} else
		/*
		 * No error -> Looking for fitting messages
		 */
		{
			final String playername = p.getName().toLowerCase();
			final String playerpath = String.format("players.%s", playername);
			final String groupname = permission.getPlayerGroups(p)[0];
			final String grouppath = String.format("Groups.%s", groupname);
			final ArrayList<MessageData> messages = new ArrayList<MessageData>();
			final long lastLogin = settings.getLastLogin(playername);
			final long difference = settings.getDifference(playername);

			ArrayList<MessageData> pm = PlayerSection.getJoinMessages(advancedMessagesYML, difference, lastLogin, playerpath);
			ArrayList<MessageData> gm = GroupSection.getJoinMessages(advancedMessagesYML, difference, lastLogin, grouppath);
			ArrayList<MessageData> dm = DefaultSection.getJoinMessages(advancedMessagesYML, difference, lastLogin);
			if (!pm.isEmpty()) {
				messages.addAll(pm);
			}
			if (!gm.isEmpty()) {
				messages.addAll(gm);
			}
			if (!dm.isEmpty()) {
				messages.addAll(dm);
			}
			/*
			 * Selecting one of the messages randomly. If no message was found -> Error message
			 */
			if (!messages.isEmpty() && messages.size() != 1) { // 2 or more messages
				int current = 0;
				for (MessageData md : messages) { //Message Debugging. Prints all messages to the console
					current++;
					this.plmLogger.logDebug("Message " + current + ": " + md.message);
				}
				int length = messages.size();
				int resultIndex;
				Random r = new Random();
				resultIndex = r.nextInt(length - 1);
				mData = messages.get(resultIndex);
			} else if (messages.isEmpty()) { // No message
				plmLogger.logWarning("[PLM] No path found for " + p.getName() + ". Using default messages");
				mData = new MessageData("&e%playername joined the game", null, SectionTypes.ERROR, SectionSubTypes.NOPATH);
			} else { // 1 message
				mData = messages.get(0);
			}

			mData.message = getReplacedWorld(mData.message, p);
			return mData;
		}
	}

	protected MessageData getRandomQuitMessage(Player p) {
		/*
		 * Error -> Skips message checking
		 */
		if (errorStatus == true) {
			return new MessageData("&4An error has occurred at the messages file! &e%playername left the game", null, SectionTypes.ERROR,
					SectionSubTypes.FILEERROR);
		} else
		/*
		 * No error -> Looking for fitting messages
		 */
		{
			final String playername = p.getName().toLowerCase();
			final String playerpath = String.format("players.%s", playername);
			final String groupname = permission.getPlayerGroups(p)[0];
			final String grouppath = String.format("Groups.%s", groupname);
			final ArrayList<MessageData> messages = new ArrayList<MessageData>();

			ArrayList<MessageData> pm = PlayerSection.getQuitMessages(advancedMessagesYML, playerpath);
			ArrayList<MessageData> gm = GroupSection.getQuitMessages(advancedMessagesYML, grouppath);
			ArrayList<MessageData> dm = DefaultSection.getQuitMessages(advancedMessagesYML);
			if (!pm.isEmpty()) {
				messages.addAll(pm);
			}
			if (!gm.isEmpty()) {
				messages.addAll(gm);
			}
			if (!dm.isEmpty()) {
				messages.addAll(dm);
			}
			/*
			 * Selecting one of the messages randomly. If no message was found -> Error message
			 */
			if (!messages.isEmpty() && messages.size() != 1) { //2 or more messages
				int current = 0;
				for (MessageData md : messages) { //Message Debugging. Prints all messages to the console
					current++;
					this.plmLogger.logDebug("Message " + current + ": " + md.message);
				}
				int length = messages.size();
				int resultIndex;
				Random r = new Random();
				resultIndex = r.nextInt(length - 1);
				mData = messages.get(resultIndex);
			} else if (messages.isEmpty()) { // No message
				plmLogger.logWarning("[PLM] No path found for " + p.getName() + ". Using default messages");
				mData = new MessageData("&e%playername left the game", null, SectionTypes.ERROR, SectionSubTypes.NOPATH);
			} else { // 1 Message
				mData = messages.get(0);
			}

			mData.message = getReplacedWorld(mData.message, p);
			return mData;
		}
	}

	protected void setMessage(MessageData mData) {
		if (!mData.message.equalsIgnoreCase("")) {
			this.mData = mData;
		} else {
			this.mData.message = "&e%playername joined the game";
		}
	}

	protected String[] getWelcomeMessages(Player p) {
		String groupname = permission.getPlayerGroups(p)[0];
		String grouppath = String.format("Groups.%s", groupname);
		String playername = p.getName().toLowerCase();
		String playerpath = String.format("players.%s", playername);
		if (errorStatus == true) {
			return emptyMessages;
		} else if (PlayerSection.checkWelcomeMessages(playerpath, advancedMessagesYML, this)) {
			for (int i = 0; i < welcomeMessages.length; i++) {
				welcomeMessages[i] = getReplacedWorld(welcomeMessages[i], p);
			}
			return welcomeMessages;
		} else if (GroupSection.checkWelcomeMessages(grouppath, advancedMessagesYML, this)) {
			for (int i = 0; i < welcomeMessages.length; i++) {
				welcomeMessages[i] = getReplacedWorld(welcomeMessages[i], p);
			}
			return welcomeMessages;
		} else if (DefaultSection.checkWelcomeMessages(advancedMessagesYML, this)) {
			for (int i = 0; i < welcomeMessages.length; i++) {
				welcomeMessages[i] = getReplacedWorld(welcomeMessages[i], p);
			}
			return welcomeMessages;
		} else {
			return emptyMessages;
		}
	}

	protected void setWelcomeMessages(ArrayList<String> messages) {
		this.welcomeMessages = new String[messages.size()];
		for (int i = 0; i < messages.size(); i++) {
			welcomeMessages[i] = messages.get(i);
		}
	}

	protected String[] getPublicMessages(Player p) {
		String groupname = permission.getPlayerGroups(p)[0];
		String grouppath = String.format("Groups.%s", groupname);
		String playername = p.getName().toLowerCase();
		String playerpath = String.format("players.%s", playername);
		if (errorStatus == true) {
			return emptyMessages;
		} else if (PlayerSection.checkPublicMessages(playerpath, advancedMessagesYML, this)) {
			for (int i = 0; i < publicMessages.length; i++) {
				publicMessages[i] = getReplacedWorld(publicMessages[i], p);
			}
			return publicMessages;
		} else if (GroupSection.checkPublicMessages(grouppath, advancedMessagesYML, this)) {
			for (int i = 0; i < publicMessages.length; i++) {
				publicMessages[i] = getReplacedWorld(publicMessages[i], p);
			}
			return publicMessages;
		} else if (DefaultSection.checkPublicMessages(advancedMessagesYML, this)) {
			for (int i = 0; i < publicMessages.length; i++) {
				publicMessages[i] = getReplacedWorld(publicMessages[i], p);
			}
			return publicMessages;
		} else {
			return emptyMessages;
		}
	}

	protected void setPublicMessages(ArrayList<String> messages) {
		this.publicMessages = new String[messages.size()];
		for (int i = 0; i < messages.size(); i++) {
			publicMessages[i] = messages.get(i);
		}
	}

	private String getReplacedWorld(String message, Player p) {
		if (advancedMessagesYML.contains("World names." + p.getWorld().getName())) {
			message = message.replaceAll("%world", advancedMessagesYML.getString("World names." + p.getWorld().getName()));
			message = message.replaceAll("%World", advancedMessagesYML.getString("World names." + p.getWorld().getName()));
		}
		return message;
	}
}
