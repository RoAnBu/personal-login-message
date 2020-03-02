package com.gmail.fantasticskythrow.messages;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.commands.PLMCommandHandler;
import com.gmail.fantasticskythrow.configuration.AppConfiguration;
import com.gmail.fantasticskythrow.other.*;
import com.gmail.fantasticskythrow.other.logging.ILoggerWrapper;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kitteh.vanish.event.VanishStatusChangeEvent;

import java.util.ArrayList;
import java.util.List;

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
	private final AppConfiguration appConfiguration;
	private boolean advancedStatus = false;
	private static boolean alreadyQuit = false;
	private StandardMessages standardMessages = null;
	private AdvancedMessages advancedMessages = null;
	private IPLMFile plmFile;
	private Player player;
	private final VanishNoPacketManager vnpHandler;
	private List<String> vnpFakeMsg = new ArrayList<String>();

	private static final ILoggerWrapper logger = PLM.logger();

	/**
	 * Uses the given status to control whether AdvancedStatus should be enabled  or not.
	 * 
	 * @param plugin The main plugin alias JavaPlugin
	 * @param advancedStatus Decide whether AdvancedStatus should be true or false
	 */
	public Messages(PLM plugin, boolean advancedStatus) {
		this.plugin = plugin;

		appConfiguration = plugin.getCfg();
		this.advancedStatus = advancedStatus;
		permission = plugin.getPermission();
		chat = plugin.getChat();
		if (PLMToolbox.getMinecraftVersion(plugin) >= 178) {
			plmFile = new PLMFile(plugin);
		} else {
			throw new NotImplementedException();
			// TODO handle old versions
		}
		vnpHandler = new VanishNoPacketManager(plugin);
		PLMCommandHandler commandHandler = new PLMCommandHandler(plugin, advancedStatus);
		plugin.getCommand("plm").setExecutor(commandHandler);
		if (!advancedStatus) { // StandardMessages
			standardMessages = new StandardMessages(plugin);
		} else { // Advanced messages mode
			advancedMessages = new AdvancedMessages(plugin, plmFile);
		}
	}

	/**
	 * Reloads the messages of AMM or SM
	 */
	public void reload() {
		if (advancedStatus) {
			advancedMessages = new AdvancedMessages(plugin, plmFile);
		} else {
			standardMessages.reload();
		}
	}

	public VanishNoPacketManager getVnpHandler() {
		return vnpHandler;
	}

	public IPLMFile getPlmFile() {
		return plmFile;
	}

	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		e.setJoinMessage(getFinalJoinMessage(e.getPlayer(), false));
	}

	public void onEarlyQuitEvent(PlayerQuitEvent e) {
		try {
			alreadyQuit = false;
			boolean isVanished = vnpHandler.isVanished(e.getPlayer().getName());
			if (isVanished) {
				alreadyQuit = true;
				logger.debug("No quit message because the player was vanished!");
			}
			vnpHandler.removeJoinedPlayer(e.getPlayer().getName());
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	public void onLatePlayerQuitEvent(PlayerQuitEvent e) {
		if (!alreadyQuit) {
			e.setQuitMessage(getFinalQuitMessage(e.getPlayer()));
		}
	}

	public void onPlayerKickEvent(PlayerKickEvent e) {
		e.setLeaveMessage(getFinalQuitMessage(e.getPlayer()));
	}

	public void onVanishStatusChangeEvent(VanishStatusChangeEvent event) {
		try {
			if (!vnpHandler.isJustJoinedPlayer(event.getPlayer().getName())) {
				boolean vnpFakeCmdUser = false;
				if (vnpFakeMsg.contains(event.getPlayer().getName())) {
					vnpFakeCmdUser = true;
					vnpFakeMsg.remove(event.getPlayer().getName());
				}
				if (event.isVanishing() && (appConfiguration.getUseFakeQuitMsg() || vnpFakeCmdUser)) { // -> Quit message (Fake)
					String fakeQuitMessage = getFinalQuitMessage(event.getPlayer());
					if (fakeQuitMessage != null) {
						plugin.getServer().broadcastMessage(fakeQuitMessage);
					}
					plmFile.setPlayerQuitTime(event.getPlayer());
				} else if (!event.isVanishing() && (appConfiguration.getUseFakeJoinMsg() || vnpFakeCmdUser)) { // Join  message (Fake)
					String fakeJoinMessage = getFinalJoinMessage(event.getPlayer(), true);
					if (fakeJoinMessage != null) {
						plugin.getServer().broadcastMessage(fakeJoinMessage);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
			logger.error("An unknown error has occurred at VanishStatusChangeEvent!");
			logger.error("Please make sure that all configuration files are available");
		}
	}

	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		try {
			String cmd = event.getMessage().replaceAll("/", "");
			if (cmd.equals("v fq") || cmd.equals("vanish fq")) {
				if (!vnpHandler.isVanished(event.getPlayer().getName())) {
					vnpFakeMsg.add(event.getPlayer().getName());
					event.setMessage("/vanish");
				} else {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "Already invisible :)");
				}
			} else if (cmd.equals("v fj") || cmd.equals("vanish fj")) {
				if (vnpHandler.isVanished(event.getPlayer().getName())) {
					vnpFakeMsg.add(event.getPlayer().getName());
					event.setMessage("/vanish");
				} else {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "Already visible :)");
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
			logger.error("An error has occurred at onPlayerCommandPreprocessEvent!");
		}
	}

	private String getFinalJoinMessage(Player player, final boolean ignoreVanish) {
		try {
			String joinMessage = null;
			vnpHandler.addJoinedPlayer(player.getName());
			this.player = player;
			plmFile.setPlayerLogin(player);
			MessageData mData;
			if (!appConfiguration.getUseRandom()) {
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

			if (PLMToolbox.getPermissionJoin(appConfiguration.getUsePermGeneral(), player) && !message.equalsIgnoreCase("off") && !isVanished) {
				joinMessage = message;
			}
			if (mData.type == null) {
				logger.debug("PLM's join message is: " + message);
			} else {
				logger.debug("PLM's join message is: " + message + " Path: " + mData.type + " | " + mData.subType);
			}

			return joinMessage;

		} catch (Exception ex) {
			logger.error(ex);
			logger.error("An unknown error has occurred at PlayerJoinEvent!");
			logger.error("Please make sure that all configuration files are available");
			// TODO remove null return
			return null;
		}
	}

	// TODO code duplication
	private String getFinalQuitMessage(Player player) {
		try {
			String quitMessage = null;
			this.player = player;
			MessageData mData;
			if (!appConfiguration.getUseRandom()) {
				mData = getMessagesQuit();
			} else {
				mData = getRandomQuitMessages();
			}
			String message = ChatColor.translateAlternateColorCodes('&', mData.message);

			if (PLMToolbox.getPermissionQuit(appConfiguration.getUsePermGeneral(), player) && !(message.equalsIgnoreCase("off"))) {
				quitMessage = message;
			}

			plmFile.setPlayerQuitTime(player);
			if (mData.type == null) {
				logger.debug("PLM's quit message is: " + message);
			} else {
				logger.debug("PLM's quit message is: " + message + " Path: " + mData.type + " | " + mData.subType);
			}
			return quitMessage;
		} catch (Exception ex) {
			logger.error(ex);
			logger.error("An unknown error has occurred at PlayerQuitEvent!");
			logger.error("Please make sure that all configuration files are available");
			// TODO remove null return
			return null;
		}
	}

	private MessageData getMessagesJoin() {
		String joinMessage;
		MessageData mData;
		/*
		 * Selects the class depending on the settings
		 */
		if (!advancedStatus) {
			mData = standardMessages.getJoinMessage();
			joinMessage = mData.message;
		} else {
			mData = advancedMessages.getJoinMessage(player);
			joinMessage = mData.message;
			printWelcomeMessage(advancedMessages);
			printPublicMessages(advancedMessages);
		}
		/*
		 * Replace placeholders
		 */
		joinMessage = PLMToolbox.getReplacedStandardPlaceholders(joinMessage, player, chat, permission, plugin, plmFile, vnpHandler);
		/*
		 * Replace %time when it was found in the string
		 */
		if (joinMessage.contains("%time")) {
			joinMessage = PLMToolbox.getReplacedTime(joinMessage, appConfiguration, plmFile, player);
		}
		mData.message = joinMessage;
		return mData;
	}

	private MessageData getMessagesQuit() {
		String quitMessage;
		MessageData mData;
		if (!advancedStatus) {
			mData = standardMessages.getQuitMessage();
			quitMessage = mData.message;
		} else {
			mData = advancedMessages.getQuitMessage(player);
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
			mData = advancedMessages.getRandomJoinMessage(player);
			joinMessage = mData.message;
			printWelcomeMessage(advancedMessages);
			printPublicMessages(advancedMessages);
			/*
			 * Replace placeholders
			 */
			joinMessage = PLMToolbox.getReplacedStandardPlaceholders(joinMessage, player, chat, permission, plugin, plmFile, vnpHandler);
			/*
			 * Replace %time when it was found in the string
			 */
			if (joinMessage.contains("%time")) {
				joinMessage = PLMToolbox.getReplacedTime(joinMessage, appConfiguration, plmFile, player);
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
			mData = advancedMessages.getRandomQuitMessage(player);
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
				String message = welcomeMessages[i];
				message = PLMToolbox.getReplacedComplexPlaceholders(message, player, chat, plugin, plmFile, vnpHandler, permission);
				message = PLMToolbox.getReplacedTime(message, appConfiguration, plmFile, player);
				welcomeMessages[i] = message;
			}
			int time = appConfiguration.getDelay();
			WelcomeMessagePrinter c = new WelcomeMessagePrinter();
			c.start(time, welcomeMessages, player);
		}
	}

	// TODO code duplication
	private void printPublicMessages(AdvancedMessages am) {
		String[] publicMessages = am.getPublicMessages(player);
		if (publicMessages != null && !vnpHandler.isVanished(player.getName())) {
			for (int i = 0; i < publicMessages.length; i++) {
				String message = publicMessages[i];
				message = PLMToolbox.getReplacedComplexPlaceholders(message, player, chat, plugin, plmFile, vnpHandler, permission);
				message = PLMToolbox.getReplacedTime(message, appConfiguration, plmFile, player);
				publicMessages[i] = message;
			}
			Player[] onlinePlayer = plugin.getServer()
			                              .getOnlinePlayers()
			                              .toArray(new Player[0]);
			/*
			 * Permissions: YES
			 */
			if (appConfiguration.getUsePermPM()) {
				Player[] receivers = new Player[onlinePlayer.length - 1];
				int receiverCount = 0;
				for (Player player : onlinePlayer) {
					if (player.hasPermission("plm." + permission.getPlayerGroups(this.player)[0]) || player.hasPermission(
							"plm." + this.player.getName())
							|| player.hasPermission("plm.pm") || player.hasPermission("plm." + this.player.getName()
							                                                                              .toLowerCase())) {
						logger.debug(player.getName() + " has the permission");
						logger.debug(
								"plm.<group>: " + player.hasPermission("plm." + permission.getPlayerGroups(this.player)[0]));
						logger.debug("plm.<player>: " + player.hasPermission("plm." + this.player.getName()));
						logger.debug("plm.pm: " + player.hasPermission("plm.pm"));
						logger.debug("plm.<lowercasename>: " + player.hasPermission("plm." + this.player.getName()
						                                                                                .toLowerCase()));
						if (!player.getName()
						       .equalsIgnoreCase(this.player.getName())) {
							receivers[receiverCount] = player;
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
				// TODO make readable
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

	// TODO use lists
	private void sendPublicMessages(Player[] receivers, String[] messages) {
		PublicMessagePrinter pmPrinter = new PublicMessagePrinter();
		pmPrinter.start(messages, receivers);
	}

}
