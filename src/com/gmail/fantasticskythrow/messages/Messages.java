package com.gmail.fantasticskythrow.messages;

import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
	private Chat chat = null;
	private Permission permission = null;
	private final MainConfiguration cfg;
	private boolean advancedStatus = false;
	private static boolean alreadyQuit = false;
	private StandardMessages sm = null;
	private AdvancedMessages am = null;
	private PLMFile plmFile;
	private Player player;
	private final VanishNoPacketManager vnpHandler;
	private final PLMLogger plmLogger;
	private HerochatManager chHandler;
	private List<String> vnpFakeMsg = new ArrayList<String>();

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
		if (PLMToolbox.getMinecraftVersion(plugin) >= 178) {
			plmFile = new NewPLMFile(plugin);
		} else {
			plmFile = new OldPLMFile(plugin);
		}
		vnpHandler = new VanishNoPacketManager(plugin);
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
	 * Reloads the messages of AMM or SM
	 */
	public void reload() {
		if (advancedStatus) {
			am = new AdvancedMessages(plugin, plmFile);
		} else {
			sm.reload();
		}
	}

	public VanishNoPacketManager getVnpHandler() {
		return vnpHandler;
	}

	public PLMFile getPlmFile() {
		return plmFile;
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
				boolean vnpFakeCmdUser = false;
				if (vnpFakeMsg.contains(e.getPlayer().getName())) {
					vnpFakeCmdUser = true;
					vnpFakeMsg.remove(e.getPlayer().getName());
				}
				if (e.isVanishing() && (cfg.getUseFakeQuitMsg() || vnpFakeCmdUser)) { // -> Quit message (Fake)
					String fakeQuitMessage = getFinalQuitMessage(e.getPlayer());
					if (fakeQuitMessage != null) {
						plugin.getServer().broadcastMessage(fakeQuitMessage);
					}
					plmFile.setPlayerQuitTime(e.getPlayer());
				} else if (!e.isVanishing() && (cfg.getUseFakeJoinMsg() || vnpFakeCmdUser)) { // Join  message (Fake)
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

	protected void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
		try {
			String cmd = e.getMessage().replaceAll("/", "");
			if (cmd.equals("v fq") || cmd.equals("vanish fq")) {
				if (!vnpHandler.isVanished(e.getPlayer().getName())) {
					vnpFakeMsg.add(e.getPlayer().getName());
					e.setMessage("/vanish");
				} else {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "Already invisible :)");
				}
			} else if (cmd.equals("v fj") || cmd.equals("vanish fj")) {
				if (vnpHandler.isVanished(e.getPlayer().getName())) {
					vnpFakeMsg.add(e.getPlayer().getName());
					e.setMessage("/vanish");
				} else {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "Already visible :)");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			plmLogger.logError("An error has occurred at onPlayerCommandPreprocessEvent!");
		}
	}

	private String getFinalJoinMessage(Player player, final boolean ignoreVanish) {
		try {
			String joinMessage = null;
			vnpHandler.addJoinedPlayer(player.getName());
			this.player = player;
			plmFile.setPlayerLogin(player);
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
					if (!PLMToolbox.sendMessageToConfigChannels(message, chHandler, cfg.getChannels())) { // Returns false if the Default channel is activated
						joinMessage = message; // Brings the message to the public channel, too
					} else { // Disable join message because it's not in the channels
						joinMessage = null;
					}
				} else { //Specified channels from section are given
					PLMToolbox.sendMessageToChannels(message, mData.channels, chHandler, plmLogger);
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
					if (!PLMToolbox.sendMessageToConfigChannels(message, chHandler, cfg.getChannels())) { // Returns false if the Default channel is activated
						quitMessage = message; // Brings the message to the public channel, too
					} else { // Disable quit message because it's not in the channels
						quitMessage = null;
					}
				} else { //Specified channels from section are given
					PLMToolbox.sendMessageToChannels(message, mData.channels, chHandler, plmLogger);
					quitMessage = null;
				}

			} else if (!PLMToolbox.getPermissionQuit(cfg.getUsePermGeneral(), player) || message.equalsIgnoreCase("off")) {
				quitMessage = null;
			}

			plmFile.setPlayerQuitTime(player);
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
			joinMessage = PLMToolbox.getReplacedTime(joinMessage, cfg, plmFile, player);
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
				joinMessage = PLMToolbox.getReplacedTime(joinMessage, cfg, plmFile, player);
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

	private void printWelcomeMessage(AdvancedMessages am) {
		String[] welcomeMessages = am.getWelcomeMessages(player);
		if (welcomeMessages != null) {
			for (int i = 0; i < welcomeMessages.length; i++) {
				String m = welcomeMessages[i];
				m = PLMToolbox.getReplacedComplexPlaceholders(m, player, chat, plugin, plmFile, vnpHandler, permission);
				m = PLMToolbox.getReplacedTime(m, cfg, plmFile, player);
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
				m = PLMToolbox.getReplacedTime(m, cfg, plmFile, player);
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

}
