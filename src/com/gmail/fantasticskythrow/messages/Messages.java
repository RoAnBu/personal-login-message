package com.gmail.fantasticskythrow.messages;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.commands.PLMRestoreCommand;
import com.gmail.fantasticskythrow.commands.ReloadCommand;
import com.gmail.fantasticskythrow.configuration.MainConfiguration;
import com.gmail.fantasticskythrow.other.PLMToolbox;
import com.gmail.fantasticskythrow.other.PLMLogger;
//import com.gmail.fantasticskythrow.other.HerochatManager;
import com.gmail.fantasticskythrow.other.PublicMessagePrinter;
import com.gmail.fantasticskythrow.other.VanishNoPacketManager;
import com.gmail.fantasticskythrow.other.WelcomeMessagePrinter;

import org.kitteh.vanish.event.VanishStatusChangeEvent;

/**
 * Provides event listeners and creates the instanced which are needed to get
 * the wanted string
 * 
 * @author Roman
 * 
 */
public class Messages implements Listener {

	private PLM plugin;
	private String joinMessage = "", quitMessage = "", playername;
	private Chat chat = null;
	private Permission permission = null;
	private MainConfiguration cfg = null;
	private boolean advancedStatus = false;
	private static boolean alreadyQuit = false;
	private StandardMessages sm = null;
	private AdvancedMessages am = null;
	private PLMFile plmFile;
	private String second, seconds, minute, minutes, hour, hours, day, days, month, months, noLastLogin;
	private Player player;
	private VanishNoPacketManager vnpHandler;
	private final PLMLogger plmLogger;

	// private HerochatManager chHandler;

	/**
	 * Provides the EventHandlers for PlayerJoinEvent and PlayerQuitEvent.
	 * Nothing is done by default, you have to call every method separately.
	 * 
	 * @param p
	 *            The main plugin object, in this case
	 *            com.gmail.fantasticskythrow.main.PLM
	 */
	public Messages(PLM p) { // Standard. Checks the AdvancedStatus itself, but not the issues
		plugin = p;
		plmLogger = plugin.getPLMLogger();
		cfg = plugin.getCfg();
		advancedStatus = cfg.getAdvancedStatus();
		permission = plugin.getPermission();
		chat = plugin.getChat();
		plmFile = new PLMFile(plugin);
		iniTimeMessages();
		vnpHandler = new VanishNoPacketManager(plugin, plugin.getServer().getOnlinePlayers());
		// chHandler = new HerochatManager(plugin);
		if (advancedStatus != true) { // StandardMessages
			sm = new StandardMessages(plugin);
			PLMRestoreCommand rc = new PLMRestoreCommand(plugin);
			plugin.getCommand("plm").setExecutor(rc);
		} else {
			am = new AdvancedMessages(plugin, plmFile);
			ReloadCommand reloadCommand = new ReloadCommand(plugin);
			plugin.getCommand("plm").setExecutor(reloadCommand);
		}
	}

	/**
	 * Uses the given status to control whether AdvancedStatus should be enabled
	 * or not.
	 * 
	 * @param p
	 *            The main plugin alias JavaPlugin
	 * @param as
	 *            - Decide whether AdvancedStatus should be true or false
	 */
	public Messages(PLM p, boolean as) {
		plugin = p;
		plmLogger = plugin.getPLMLogger();
		cfg = plugin.getCfg();
		advancedStatus = as;
		permission = plugin.getPermission();
		chat = plugin.getChat();
		plmFile = new PLMFile(plugin);
		iniTimeMessages();
		vnpHandler = new VanishNoPacketManager(plugin, plugin.getServer().getOnlinePlayers());
		// chHandler = new HerochatManager(plugin);
		if (advancedStatus == false) { // StandardMessages
			sm = new StandardMessages(plugin);
			PLMRestoreCommand rc = new PLMRestoreCommand(plugin);
			plugin.getCommand("plm").setExecutor(rc);
		} else {
			am = new AdvancedMessages(plugin, plmFile);
			ReloadCommand reloadCommand = new ReloadCommand(plugin);
			plugin.getCommand("plm").setExecutor(reloadCommand);
		}
	}

	/**
	 * Creates a new instance of AdvancedMessages
	 */
	public void reload() {
		am = new AdvancedMessages(plugin, plmFile);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		try {
			vnpHandler.addJoinedPlayer(e.getPlayer().getName());
			this.player = e.getPlayer();
			this.playername = e.getPlayer().getName().toLowerCase();
			// String[] test = new String[2];
			// test[0] = "This is a test message";
			// test[1] = "Second test message";
			// chHandler.sendMessages("Minecity", test);
			String message = getMessagesJoin(e.getPlayer());
			boolean isVanished = vnpHandler.isVanished(e.getPlayer().getName());
			if (PLMToolbox.getPermissionJoin(cfg.getUsePermGeneral(), player) && !message.equalsIgnoreCase("off") && !isVanished) {
				e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', message));
			} else if (!isVanished) {
				if (!PLMToolbox.getPermissionJoin(cfg.getUsePermGeneral(), player) || message.equalsIgnoreCase("off")) {
					e.setJoinMessage(null);
				}
			}
		} catch (NullPointerException ne) {
			plmLogger.logError("[PLM] A problem occurred at PlayerJoinEvent!");
			ne.printStackTrace();
			e.setJoinMessage(null);
		} catch (Exception ex) {
			ex.printStackTrace();
			plmLogger.logError("[PLM] An unknown error occurred at PlayerJoinEvent!");
			plmLogger.logError("[PLM] Please make sure that all configuration files are available");
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEarlyQuitEvent(PlayerQuitEvent e) {
		try {
			alreadyQuit = false;
			boolean isVanished = vnpHandler.isVanished(e.getPlayer().getName());
			if (isVanished) {
				alreadyQuit = true;
			}
			vnpHandler.removeJoinedPlayer(e.getPlayer().getName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onLatePlayerQuitEvent(PlayerQuitEvent e) {
		if (alreadyQuit == false) {
			try {
				this.player = e.getPlayer();
				this.playername = e.getPlayer().getName().toLowerCase();
				String message = getMessagesQuit(e.getPlayer());
				if (PLMToolbox.getPermissionQuit(cfg.getUsePermGeneral(), player) && !(message.equalsIgnoreCase("off"))) {
					e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', message));
				} else if (!PLMToolbox.getPermissionQuit(cfg.getUsePermGeneral(), player) || message.equalsIgnoreCase("off")) {
					e.setQuitMessage(null);
				}
				plmFile.setPlayerQuitTime(e.getPlayer().getName().toLowerCase());
			} catch (NullPointerException ne) {
				plmLogger.logError("[PLM] A problem occurred at PlayerQuitEvent!");
				e.setQuitMessage(null);
			}

			catch (Exception ex) {
				ex.printStackTrace();
				plmLogger.logError("[PLM] An unknown error occurred at PlayerQuitEvent!");
				plmLogger.logError("[PLM] Please make sure that all configuration files are available");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerKickEvent(PlayerKickEvent e) {
		try {
			vnpHandler.removeJoinedPlayer(e.getPlayer().getName());
			this.player = e.getPlayer();
			this.playername = e.getPlayer().getName().toLowerCase();
			String message = getMessagesQuit(e.getPlayer());
			if (PLMToolbox.getPermissionQuit(cfg.getUsePermGeneral(), player) && !(message.equalsIgnoreCase("off"))) {
				e.setLeaveMessage(ChatColor.translateAlternateColorCodes('&', message));
			} else if (!PLMToolbox.getPermissionQuit(cfg.getUsePermGeneral(), player) || message.equalsIgnoreCase("off")) {
				e.setLeaveMessage(null);
			}
		} catch (NullPointerException ne) {
			plmLogger.logError("[PLM] A problem occurred at PlayerKickEvent!");
			e.setLeaveMessage(null);
		}

		catch (Exception ex) {
			ex.printStackTrace();
			plmLogger.logError("[PLM] An unknown error occurred at PlayerQuitEvent!");
			plmLogger.logError("[PLM] Please make sure that all configuration files are available");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVanishStatusChangeEvent(VanishStatusChangeEvent e) {
		try {
			if (!vnpHandler.isJustJoinedPlayer(e.getPlayer().getName())) {
				if (e.isVanishing() && cfg.getUseFakeQuitMsg()) { // -> Quit message (Fake)
					this.player = e.getPlayer();
					this.playername = e.getPlayer().getName().toLowerCase();
					String message = getMessagesQuit(e.getPlayer());
					if (PLMToolbox.getPermissionQuit(cfg.getUsePermGeneral(), player) && !(message.equalsIgnoreCase("off"))) {
						plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
					}
					plmFile.setPlayerQuitTime(e.getPlayer().getName().toLowerCase());
				} else if (!e.isVanishing() && cfg.getUseFakeJoinMsg()) { // Join
																			// message
																			// (Fake)
					this.player = e.getPlayer();
					this.playername = e.getPlayer().getName().toLowerCase();
					String message = getMessagesJoin(e.getPlayer());
					if (PLMToolbox.getPermissionJoin(cfg.getUsePermGeneral(), player) && !message.equalsIgnoreCase("off")) {
						plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			plmLogger.logError("[PLM] An unknown error occurred at VanishStatusChangeEvent!");
			plmLogger.logError("[PLM] Please make sure that all configuration files are available");
		}
	}

	private String getMessagesJoin(Player pl) {
		/*
		 * Selects the class depending on the settings
		 */
		if (advancedStatus == false) {
			joinMessage = sm.getJoinMessage(pl.getName());
		} else {
			joinMessage = am.getJoinMessage(pl);
			printWelcomeMessage(pl, am);
			printPublicMessages(pl, am);
		}
		/*
		 * Replace placeholders
		 */
		joinMessage = PLMToolbox.getReplacedPlayername(joinMessage, player);
		joinMessage = PLMToolbox.getReplacedChatplayername(joinMessage, chat, player);
		joinMessage = PLMToolbox.getReplacedGroup(joinMessage, permission, player);
		joinMessage = PLMToolbox.getReplacedWorld(joinMessage, player);
		joinMessage = PLMToolbox.getReplacedCountry(joinMessage, plugin, player, plmFile);
		/*
		 * Replace %time when it was found in the string
		 */
		if (joinMessage.contains("%time")) {
			joinMessage = getReplacedTime(joinMessage);
		}
		return joinMessage;
	}

	private String getMessagesQuit(Player pl) {
		if (advancedStatus == false) {
			quitMessage = sm.getQuitMessage();
		} else {
			quitMessage = am.getQuitMessage(pl);
		}
		quitMessage = PLMToolbox.getReplacedPlayername(quitMessage, player);
		quitMessage = PLMToolbox.getReplacedChatplayername(quitMessage, chat, player);
		quitMessage = PLMToolbox.getReplacedGroup(quitMessage, permission, player);
		quitMessage = PLMToolbox.getReplacedWorld(quitMessage, player);
		quitMessage = PLMToolbox.getReplacedCountry(quitMessage, plugin, player, plmFile);
		return quitMessage;
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
	 * @param message
	 *            - the message containing the time constant
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

	private void printWelcomeMessage(Player p, AdvancedMessages am) {
		String[] welcomeMessages = am.getWelcomeMessages(p);
		if (welcomeMessages != null) {
			for (int i = 0; i < welcomeMessages.length; i++) {
				String m = welcomeMessages[i];
				m = PLMToolbox.getReplacedPlayername(m, player);
				m = PLMToolbox.getReplacedChatplayername(m, chat, player);
				m = PLMToolbox.getReplacedWorld(m, player);
				m = PLMToolbox.getReplacedCountry(m, plugin, player, plmFile);
				m = getReplacedTime(m);
				m = PLMToolbox.getReplacedPlayerlist(m, vnpHandler, plugin.getServer());
				m = PLMToolbox.getReplacedChatplayerlist(m, chat, vnpHandler, plugin.getServer());
				m = PLMToolbox.getReplacedGroupplayerlist(m, vnpHandler, permission, plugin.getServer(), player);
				m = PLMToolbox.getReplacedGroupchatplayerlist(m, vnpHandler, permission, chat, plugin.getServer(), player);
				welcomeMessages[i] = m;
			}
			int time = cfg.getDelay();
			WelcomeMessagePrinter c = new WelcomeMessagePrinter();
			c.start(time, welcomeMessages, p);
		}
	}

	private void printPublicMessages(Player p, AdvancedMessages am) {
		String[] publicMessages = am.getPublicMessages(p);
		if (publicMessages != null && !vnpHandler.isVanished(player.getName())) {
			for (int i = 0; i < publicMessages.length; i++) {
				String m = publicMessages[i];
				m = PLMToolbox.getReplacedPlayername(m, player);
				m = PLMToolbox.getReplacedChatplayername(m, chat, player);
				m = PLMToolbox.getReplacedWorld(m, player);
				m = PLMToolbox.getReplacedCountry(m, plugin, player, plmFile);
				m = getReplacedTime(m);
				m = PLMToolbox.getReplacedPlayerlist(m, vnpHandler, plugin.getServer());
				m = PLMToolbox.getReplacedChatplayerlist(m, chat, vnpHandler, plugin.getServer());
				m = PLMToolbox.getReplacedGroupplayerlist(m, vnpHandler, permission, plugin.getServer(), player);
				m = PLMToolbox.getReplacedGroupchatplayerlist(m, vnpHandler, permission, chat, plugin.getServer(), player);
				m = PLMToolbox.getReplacedGroup(m, permission, player);
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
					if (onlinePlayer[i].hasPermission("plm." + permission.getPlayerGroups(p)[0]) || onlinePlayer[i].hasPermission("plm.pm")) {
						if (!onlinePlayer[i].getName().equalsIgnoreCase(p.getName())) {
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
					if (!onlinePlayer[i].getName().equalsIgnoreCase(p.getName())) {
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
