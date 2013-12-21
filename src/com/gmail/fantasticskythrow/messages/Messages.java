package com.gmail.fantasticskythrow.messages;

import java.util.List;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kitteh.vanish.event.VanishStatusChangeEvent;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.commands.PLMCommandHandler;
import com.gmail.fantasticskythrow.configuration.MainConfiguration;
import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMToolbox;
import com.gmail.fantasticskythrow.other.PLMLogger;
import com.gmail.fantasticskythrow.other.HerochatManager;
import com.gmail.fantasticskythrow.other.PublicMessagePrinter;
import com.gmail.fantasticskythrow.other.VanishNoPacketManager;
import com.gmail.fantasticskythrow.other.WelcomeMessagePrinter;

/**
 * Provides event listeners and creates the instances which are needed to get
 * the wanted string
 * 
 * @author Roman
 * 
 */
public class Messages {

	private final PLM plugin;
	private String playername;
	private Chat chat = null;
	private Permission permission = null;
	private final MainConfiguration cfg;
	private boolean advancedStatus = false;
	private static boolean alreadyQuit = false;
	private StandardMessages sm = null;
	private AdvancedMessages am = null;
	private PLMFile plmFile;
	private String second, seconds, minute, minutes, hour, hours, day, days, month, months, noLastLogin;
	private Player player;
	private final VanishNoPacketManager vnpHandler;
	private final PLMLogger plmLogger;
	private HerochatManager chHandler;

	/**
	 * Uses the given status to control whether AdvancedStatus should be enabled  or not.
	 * 
	 * @param plugin The main plugin alias JavaPlugin
	 * @param advancedStatus Decide whether AdvancedStatus should be true or false
	 */
	public Messages(PLM plugin, boolean advancedStatus) {
		this.plugin = plugin;
		plmLogger = plugin.getPLMLogger();
		cfg = plugin.getCfg();
		this.advancedStatus = advancedStatus;
		permission = plugin.getPermission();
		chat = plugin.getChat();
		plmFile = new PLMFile(plugin);
		iniTimeMessages();
		vnpHandler = new VanishNoPacketManager(plugin, plugin.getServer().getOnlinePlayers());
		chHandler = new HerochatManager(plugin);
		PLMCommandHandler commandHandler = new PLMCommandHandler(plugin, plmLogger, advancedStatus);
		plugin.getCommand("plm").setExecutor(commandHandler);
		if (advancedStatus == false) { // StandardMessages
			sm = new StandardMessages(plugin);
		} else { // Advanced messages mode
			am = new AdvancedMessages(plugin, plmFile);
		}
	}

	/**
	 * Creates a new instance of AdvancedMessages
	 */
	public void reload() {
		am = new AdvancedMessages(plugin, plmFile);
	}

	public VanishNoPacketManager getVnpHandler() {
		return vnpHandler;
	}

	protected PLMFile getPlmFile() {
		return plmFile;
	}

	protected void setPlayer(Player p) {
		this.player = p;
		this.playername = p.getName().toLowerCase();
	}

	protected void onPlayerJoinEvent(PlayerJoinEvent e) {
		e.setJoinMessage(getFinalJoinMessage(e.getPlayer(), false));
	}

	protected void onEarlyQuitEvent(PlayerQuitEvent e) {
		try {
			alreadyQuit = false;
			boolean isVanished = vnpHandler.isVanished(e.getPlayer().getName());
			if (isVanished) {
				alreadyQuit = true;
				plmLogger.logDebug("[PLM] No quit message because the player was vanished!");
			}
			vnpHandler.removeJoinedPlayer(e.getPlayer().getName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void onLatePlayerQuitEvent(PlayerQuitEvent e) {
		if (!alreadyQuit) {
			e.setQuitMessage(getFinalQuitMessage(e.getPlayer()));
		}
	}

	protected void onPlayerKickEvent(PlayerKickEvent e) {
		e.setLeaveMessage(getFinalQuitMessage(e.getPlayer()));
	}

	protected void onVanishStatusChangeEvent(VanishStatusChangeEvent e) {
		try {
			if (!vnpHandler.isJustJoinedPlayer(e.getPlayer().getName())) {
				if (e.isVanishing() && cfg.getUseFakeQuitMsg()) { // -> Quit message (Fake)
					String fakeQuitMessage = getFinalQuitMessage(e.getPlayer());
					if (fakeQuitMessage != null) {
						plugin.getServer().broadcastMessage(fakeQuitMessage);
					}
					plmFile.setPlayerQuitTime(e.getPlayer().getName().toLowerCase());
				} else if (!e.isVanishing() && cfg.getUseFakeJoinMsg()) { // Join  message (Fake)
					String fakeJoinMessage = getFinalJoinMessage(e.getPlayer(), true);
					if (fakeJoinMessage != null) {
						plugin.getServer().broadcastMessage(fakeJoinMessage);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			plmLogger.logError("[PLM] An unknown error has occurred at VanishStatusChangeEvent!");
			plmLogger.logError("[PLM] Please make sure that all configuration files are available");
		}
	}

	private String getFinalJoinMessage(Player player, final boolean ignoreVanish) {
		try {
			String joinMessage = null;
			vnpHandler.addJoinedPlayer(player.getName());
			this.player = player;
			this.playername = player.getName().toLowerCase();
			plmFile.setPlayerLogin(playername);
			MessageData mData;
			if (!cfg.getUseRandom()) {
				mData = getMessagesJoin();
			} else {
				mData = getRandomJoinMessages();
			}
			String message = ChatColor.translateAlternateColorCodes('&', mData.message);
			boolean isVanished;
			if (ignoreVanish) {
				isVanished = false;
			} else {
				isVanished = vnpHandler.isVanished(player.getName());
			}
			if (PLMToolbox.getPermissionJoin(cfg.getUsePermGeneral(), player) && !message.equalsIgnoreCase("off") && !isVanished) {

				//Sending/activating message
				if (!cfg.getUseChannels() && mData.channels == null) { //No channel use anyway
					joinMessage = message;
				} else if (mData.channels == null) { //No channels given with the section
					if (!sendMessageToConfigChannels(message)) { // Returns false if the Default channel is activated
						joinMessage = message; // Brings the message to the public channel, too
					} else { // Disable join message because it's not in the channels
						joinMessage = null;
					}
				} else { //Specified channels from section are given
					sendMessageToChannels(message, mData.channels);
					joinMessage = null;
				}

			} else if (!isVanished) {// Cases to disable join message
				if (!PLMToolbox.getPermissionJoin(cfg.getUsePermGeneral(), player) || message.equalsIgnoreCase("off")) {
					joinMessage = null;
				}
			}
			if (mData.type == null) {
				plmLogger.logDebug("PLM's join message is: " + message);
			} else {
				plmLogger.logDebug("PLM's join message is: " + message + " Path: " + mData.type + " | " + mData.subType);
			}

			return joinMessage;

		} catch (Exception ex) {
			ex.printStackTrace();
			plmLogger.logError("[PLM] An unknown error has occurred at PlayerJoinEvent!");
			plmLogger.logError("[PLM] Please make sure that all configuration files are available");
			return null;
		}
	}

	private String getFinalQuitMessage(Player player) {
		try {
			String quitMessage = null;
			this.player = player;
			this.playername = player.getName().toLowerCase();
			MessageData mData;
			if (!cfg.getUseRandom()) {
				mData = getMessagesQuit();
			} else {
				mData = getRandomQuitMessages();
			}
			String message = ChatColor.translateAlternateColorCodes('&', mData.message);
			if (PLMToolbox.getPermissionQuit(cfg.getUsePermGeneral(), player) && !(message.equalsIgnoreCase("off"))) {

				//Sending/activating message
				if (!cfg.getUseChannels() && mData.channels == null) { //No channel use anyway
					quitMessage = message;
				} else if (mData.channels == null) { //No channels given with the section
					if (!sendMessageToConfigChannels(message)) { // Returns false if the Default channel is activated
						quitMessage = message; // Brings the message to the public channel, too
					} else { // Disable quit message because it's not in the channels
						quitMessage = null;
					}
				} else { //Specified channels from section are given
					sendMessageToChannels(message, mData.channels);
					quitMessage = null;
				}

			} else if (!PLMToolbox.getPermissionQuit(cfg.getUsePermGeneral(), player) || message.equalsIgnoreCase("off")) {
				quitMessage = null;
			}

			plmFile.setPlayerQuitTime(player.getName().toLowerCase());
			if (mData.type == null) {
				plmLogger.logDebug("PLM's quit message is: " + message);
			} else {
				plmLogger.logDebug("PLM's quit message is: " + message + " Path: " + mData.type + " | " + mData.subType);
			}
			return quitMessage;
		} catch (Exception ex) {
			ex.printStackTrace();
			plmLogger.logError("[PLM] An unknown error has occurred at PlayerQuitEvent!");
			plmLogger.logError("[PLM] Please make sure that all configuration files are available");
			return null;
		}
	}

	private MessageData getMessagesJoin() {
		String joinMessage;
		MessageData mData;
		/*
		 * Selects the class depending on the settings
		 */
		if (advancedStatus == false) {
			mData = sm.getJoinMessage();
			joinMessage = mData.message;
		} else {
			mData = am.getJoinMessage(player);
			joinMessage = mData.message;
			printWelcomeMessage(am);
			printPublicMessages(am);
		}
		/*
		 * Replace placeholders
		 */
		joinMessage = PLMToolbox.getReplacedStandardPlaceholders(joinMessage, player, chat, permission, plugin, plmFile, vnpHandler);
		/*
		 * Replace %time when it was found in the string
		 */
		if (joinMessage.contains("%time")) {
			joinMessage = getReplacedTime(joinMessage);
		}
		mData.message = joinMessage;
		return mData;
	}

	private MessageData getMessagesQuit() {
		String quitMessage;
		MessageData mData;
		if (advancedStatus == false) {
			mData = sm.getQuitMessage();
			quitMessage = mData.message;
		} else {
			mData = am.getQuitMessage(player);
			quitMessage = mData.message;
		}
		quitMessage = PLMToolbox.getReplacedStandardPlaceholders(quitMessage, player, chat, permission, plugin, plmFile, vnpHandler);
		mData.message = quitMessage;
		return mData;
	}

	private MessageData getRandomJoinMessages() {
		String joinMessage;
		MessageData mData;
		/*
		 * Selects the class depending on the settings
		 */
		if (!advancedStatus) { // No effect if standard mode
			return getMessagesJoin();
		} else { // With AMM
			mData = am.getRandomJoinMessage(player);
			joinMessage = mData.message;
			printWelcomeMessage(am);
			printPublicMessages(am);
			/*
			 * Replace placeholders
			 */
			joinMessage = PLMToolbox.getReplacedStandardPlaceholders(joinMessage, player, chat, permission, plugin, plmFile, vnpHandler);
			/*
			 * Replace %time when it was found in the string
			 */
			if (joinMessage.contains("%time")) {
				joinMessage = getReplacedTime(joinMessage);
			}
			mData.message = joinMessage;
			return mData;
		}
	}

	private MessageData getRandomQuitMessages() {
		String quitMessage;
		MessageData mData;
		if (!advancedStatus) {
			return getMessagesQuit();
		} else {
			mData = am.getRandomQuitMessage(player);
			quitMessage = mData.message;
			quitMessage = PLMToolbox.getReplacedStandardPlaceholders(quitMessage, player, chat, permission, plugin, plmFile, vnpHandler);
			mData.message = quitMessage;
			return mData;
		}

	}

	/**
	 * Loads the time strings from config.yml
	 */
	private void iniTimeMessages() {
		second = cfg.second;
		seconds = cfg.seconds;
		minute = cfg.minute;
		minutes = cfg.minutes;
		hour = cfg.hour;
		hours = cfg.hours;
		day = cfg.day;
		days = cfg.days;
		month = cfg.month;
		months = cfg.months;
		noLastLogin = cfg.noLastLogin;
	}

	/**
	 * Replaces %time with the period the player was offline
	 * 
	 * @param message the message containing the time constant
	 * @return the message without %time
	 */
	private String getReplacedTime(String message) {
		long difference;
		if (plmFile.getLastLogin(playername) == 0L) {
			difference = 0L;
		} else {
			difference = System.currentTimeMillis() - plmFile.getLastLogin(playername);
		}
		// Keine Werte
		if (difference == 0L) {
			message = message.replaceAll("%time", noLastLogin);
		}
		// Kleiner als eine Minute und nicht gleich 0
		if (difference < 60000L && difference != 0) {
			long a = difference / 1000L;
			if (a == 1L) {
				message = message.replaceAll("%time", a + " " + second);
			} else {
				message = message.replaceAll("%time", a + " " + seconds);
			}
		}
		// Groesser als eine Minute, kleiner als eine Stunde
		if (difference >= 60000L && difference < 3600000L) {
			long a = difference / 60000L;
			if (a == 1L) {
				message = message.replaceAll("%time", a + " " + minute);
			} else {
				message = message.replaceAll("%time", a + " " + minutes);
			}
		}
		// Groesser als eine Stunde, kleiner als ein Tag
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
		// Groesser als ein Tag, kleiner als 10 Tage
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
		// Groesser als 10 Tage, kleiner als 30 Tage
		if (difference >= 864000000L && difference < 2592000000L) {
			long a = difference / 86400000L;
			if (a == 1L) {
				message = message.replaceAll("%time", a + " " + day);
			} else {
				message = message.replaceAll("%time", a + " " + days);
			}
		}
		// Groesser als 30 Tage (1 Monat)
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

	private void printWelcomeMessage(AdvancedMessages am) {
		String[] welcomeMessages = am.getWelcomeMessages(player);
		if (welcomeMessages != null) {
			for (int i = 0; i < welcomeMessages.length; i++) {
				String m = welcomeMessages[i];
				m = PLMToolbox.getReplacedComplexPlaceholders(m, player, chat, plugin, plmFile, vnpHandler, permission);
				m = getReplacedTime(m);
				welcomeMessages[i] = m;
			}
			int time = cfg.getDelay();
			WelcomeMessagePrinter c = new WelcomeMessagePrinter();
			c.start(time, welcomeMessages, player);
		}
	}

	private void printPublicMessages(AdvancedMessages am) {
		String[] publicMessages = am.getPublicMessages(player);
		if (publicMessages != null && !vnpHandler.isVanished(player.getName())) {
			for (int i = 0; i < publicMessages.length; i++) {
				String m = publicMessages[i];
				m = PLMToolbox.getReplacedComplexPlaceholders(m, player, chat, plugin, plmFile, vnpHandler, permission);
				m = getReplacedTime(m);
				publicMessages[i] = m;
			}
			Player[] onlinePlayer = plugin.getServer().getOnlinePlayers();
			/*
			 * Permissions: YES
			 */
			if (cfg.getUsePermPM()) {
				Player[] receivers = new Player[onlinePlayer.length - 1];
				int receiverCount = 0;
				for (int i = 0; i < onlinePlayer.length; i++) {
					Player pl = onlinePlayer[i];
					if (pl.hasPermission("plm." + permission.getPlayerGroups(player)[0]) || pl.hasPermission("plm." + player.getName())
							|| pl.hasPermission("plm.pm") || pl.hasPermission("plm." + player.getName().toLowerCase())) {
						plmLogger.logDebug(pl.getName() + " has the permission");
						plmLogger.logDebug("plm.<group>: " + pl.hasPermission("plm." + permission.getPlayerGroups(player)[0]));
						plmLogger.logDebug("plm.<player>: " + pl.hasPermission("plm." + player.getName()));
						plmLogger.logDebug("plm.pm: " + pl.hasPermission("plm.pm"));
						plmLogger.logDebug("plm.<lowercasename>: " + pl.hasPermission("plm." + player.getName().toLowerCase()));
						if (!onlinePlayer[i].getName().equalsIgnoreCase(player.getName())) {
							receivers[receiverCount] = onlinePlayer[i];
							receiverCount++;
						}
					}
				}
				sendPublicMessages(receivers, publicMessages);
			}
			/*
			 * Permissions: NO
			 */
			else {
				Player[] receivers = new Player[onlinePlayer.length - 1];
				int b = 0;
				for (int i = 0; i < onlinePlayer.length; i++) {
					int j = i - b;
					if (!onlinePlayer[i].getName().equalsIgnoreCase(player.getName())) {
						receivers[j] = onlinePlayer[i];
					} else {
						b = 1;
					}
				}
				sendPublicMessages(receivers, publicMessages);
			}
		}
	}

	private void sendPublicMessages(Player[] receivers, String[] messages) {
		PublicMessagePrinter pmPrinter = new PublicMessagePrinter();
		pmPrinter.start(messages, receivers);
	}

	/**
	 * Sends a message to the global defined channels (in config)
	 * @param message The join/quit message to send
	 * @return true if no need to use the public join/quit message system. False -> Activate join/quit message
	 */
	private boolean sendMessageToConfigChannels(String message) {
		List<String> channels = cfg.getChannels();
		boolean answer = true;
		if (chHandler.isHerochatInstalled()) {
			if (channels.contains("Default")) {
				answer = false;
				channels.remove("Default");
			}
			if (channels.contains("default")) {
				answer = false;
				channels.remove("default");
			}
			for (String s : channels) {
				chHandler.sendMessage(s, message);
			}
			return answer;
		} else { //Herochat not found
			return false;
		}
	}

	/**
	 * Sends a message to the given channels. The channel "Default" won't be ignored
	 * @param message The message with translated color codes
	 * @param channels The channels which are the aim for the message.
	 */
	private void sendMessageToChannels(String message, String[] channels) {
		if (chHandler.isHerochatInstalled()) {
			for (String s : channels) {
				chHandler.sendMessage(s, message);
			}
		} else { //Herochat not found
			plmLogger.logInfo("[PLM] You defined channels but you don't have Herochat installed");
		}
	}

}
