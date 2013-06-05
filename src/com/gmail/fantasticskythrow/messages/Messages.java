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

import uk.org.whoami.geoip.GeoIPLookup;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.commands.PLMRestoreCommand;
import com.gmail.fantasticskythrow.commands.ReloadCommand;
import com.gmail.fantasticskythrow.configuration.MainConfiguration;
import com.gmail.fantasticskythrow.other.HerochatManager;
import com.gmail.fantasticskythrow.other.PublicMessagePrinter;
import com.gmail.fantasticskythrow.other.VanishNoPacketManager;
import com.gmail.fantasticskythrow.other.WelcomeMessagePrinter;

import org.kitteh.vanish.event.VanishStatusChangeEvent;


/**
 * Provides event listeners and creates the instanced which are needed to get the wanted string
 * @author Roman
 *
 */
public class Messages implements Listener{
    
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
    private HerochatManager chHandler;
    
    /**
     * Provides the EventHandlers for PlayerJoinEvent and PlayerQuitEvent. Nothing is done by default, you have to call every method separately.
     * @param p The main plugin object, in this case com.gmail.fantasticskythrow.main.PLM
     */
    public Messages(PLM p) {	//Standard. Checks the AdvancedStatus itself, but not the issues
	plugin = p;
	cfg = plugin.getCfg();
	advancedStatus = cfg.getAdvancedStatus();
	permission = plugin.getPermission();
	chat = plugin.getChat();
	plmFile = new PLMFile(plugin);
	iniTimeMessages();
	vnpHandler = new VanishNoPacketManager(plugin);
	chHandler = new HerochatManager(plugin);
	if (advancedStatus != true) {		//StandardMessages
	    sm = new StandardMessages(plugin);
	    PLMRestoreCommand rc = new PLMRestoreCommand(plugin);
	    plugin.getCommand("plm").setExecutor(rc);
	}
	else {
	    am = new AdvancedMessages(plugin, plmFile);
	    ReloadCommand reloadCommand = new ReloadCommand(plugin);
	    plugin.getCommand("plm").setExecutor(reloadCommand);
	}
    }
    
    /**
     * Uses the given status to control whether AdvancedStatus should be enabled or not.
     * @param p The main plugin alias JavaPlugin
     * @param as - Decide whether AdvancedStatus should be true or false
     */
    public Messages (PLM p, boolean as) {
	plugin = p;
	cfg = plugin.getCfg();
	advancedStatus = as;
	permission = plugin.getPermission();
	chat = plugin.getChat();
	plmFile = new PLMFile(plugin);
	iniTimeMessages();
	vnpHandler = new VanishNoPacketManager(plugin);
	chHandler = new HerochatManager(plugin);
	if (advancedStatus == false) {	//StandardMessages
	    sm = new StandardMessages(plugin);
	    PLMRestoreCommand rc = new PLMRestoreCommand(plugin);
	    plugin.getCommand("plm").setExecutor(rc);
	}
	else {
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
    
    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
	    try {
		vnpHandler.addJoinedPlayer(e.getPlayer().getName());
		this.player = e.getPlayer();
		this.playername = e.getPlayer().getName().toLowerCase();
//		String[] test = new String[2];
//		test[0] = "This is a test message";
//		test[1] = "Second test message";
//		chHandler.sendMessages("Minecity", test);
		String message = getMessagesJoin(e.getPlayer());
		boolean isVanished = vnpHandler.isVanished(e.getPlayer().getName());
		if ( getPermissionJoin(e.getPlayer()) && ! message.equalsIgnoreCase("off") && ! isVanished) {
		    e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', message));
	    	}
	    	else if (!isVanished){
	    	    if (! getPermissionJoin(e.getPlayer()) ||  message.equalsIgnoreCase("off")){
	    	    e.setJoinMessage(null);
	    	    }
	    	}
	    } catch (NullPointerException ne) {
		System.out.println("[SEVERE] [PLM] A problem occurred at PlayerJoinEvent!");
		ne.printStackTrace();
		e.setJoinMessage(null);
	    } catch (Exception ex) {
		ex.printStackTrace();
		System.out.println("[PLM] An unknown error occurred at PlayerJoinEvent!");
		System.out.println("[PLM] Please make sure that all configuration files are available");
	    }
    }
    

    @EventHandler (priority = EventPriority.LOWEST)
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
    
    @EventHandler (priority = EventPriority.HIGH)
    public void onLatePlayerQuitEvent(PlayerQuitEvent e) {
	if (alreadyQuit == false) {
	    try {
		this.player = e.getPlayer();
		this.playername = e.getPlayer().getName().toLowerCase();
		String message = getMessagesQuit(e.getPlayer());
		if (getPermissionQuit(e.getPlayer()) && ! (message.equalsIgnoreCase("off"))) {
		    e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', message));
		}
		else if (! getPermissionQuit(e.getPlayer()) ||  message.equalsIgnoreCase("off")){
		    e.setQuitMessage(null);
		}
		plmFile.setPlayerQuitTime(e.getPlayer().getName().toLowerCase());
	    }
	    catch (NullPointerException ne) {
		System.out.println("[SEVERE] [PLM] A problem occurred at PlayerQuitEvent!");
		e.setQuitMessage(null);
	    }
	    
	    catch (Exception ex) {
		ex.printStackTrace();
		System.out.println("[PLM] An unknown error occurred at PlayerQuitEvent!");
		System.out.println("[PLM] Please make sure that all configuration files are available");
	    }
	}
    }
    
    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerKickEvent(PlayerKickEvent e) {
	    try {
		vnpHandler.removeJoinedPlayer(e.getPlayer().getName());
		this.player = e.getPlayer();
		this.playername = e.getPlayer().getName().toLowerCase();
		String message = getMessagesQuit(e.getPlayer());
		if (getPermissionQuit(e.getPlayer()) && ! (message.equalsIgnoreCase("off"))) {
		    e.setLeaveMessage(ChatColor.translateAlternateColorCodes('&', message));
		}
		else if (! getPermissionQuit(e.getPlayer()) ||  message.equalsIgnoreCase("off")){
		    e.setLeaveMessage(null);
		}
	    }
	    catch (NullPointerException ne) {
		System.out.println("[SEVERE] [PLM] A problem occurred at PlayerKickEvent!");
		e.setLeaveMessage(null);
	    }
	    
	    catch (Exception ex) {
		ex.printStackTrace();
		System.out.println("[PLM] An unknown error occurred at PlayerQuitEvent!");
		System.out.println("[PLM] Please make sure that all configuration files are available");
	    }
    }
    
    @EventHandler (priority = EventPriority.NORMAL)
    public void onVanishStatusChangeEvent(VanishStatusChangeEvent e) {
	try {
	    if (!vnpHandler.isJustJoinedPlayer(e.getPlayer().getName())) {
	        if (e.isVanishing() && cfg.getUseFakeQuitMsg()) {	//-> Quit message (Fake)
	            this.player = e.getPlayer();
	            this.playername = e.getPlayer().getName().toLowerCase();
	            String message = getMessagesQuit(e.getPlayer());
	            if (getPermissionQuit(e.getPlayer()) && ! (message.equalsIgnoreCase("off"))) {
	                plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
	            }
	            plmFile.setPlayerQuitTime(e.getPlayer().getName().toLowerCase());
	        } else if (!e.isVanishing() && cfg.getUseFakeJoinMsg()) {  //Join message (Fake)
	            this.player = e.getPlayer();
	            this.playername = e.getPlayer().getName().toLowerCase();
	            String message = getMessagesJoin(e.getPlayer());
	            if ( getPermissionJoin(e.getPlayer()) && ! message.equalsIgnoreCase("off")) {
	    	    	plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
	            }
	        }
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.out.println("[PLM] An unknown error occurred at VanishStatusChangeEvent!");
	    System.out.println("[PLM] Please make sure that all configuration files are available");
	}
    }
    
    /**
     * Replaces all constants apart from color codes
     * @param pl The wanted player
     * @return The join message without the constants (%chatplayername, %playername, %group). The constants are replaced in the correct format with the information delivered by the player. %group returns only the first group
     */
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
	joinMessage = getReplacedPlayername(joinMessage);
	joinMessage = getReplacedChatplayername(joinMessage);
	joinMessage = getReplacedGroup(joinMessage);
	joinMessage = getReplacedWorld(joinMessage);
	joinMessage = getReplacedCountry(joinMessage);
	/*
	 * Replace %time when it was found in the string
	 */
	if (joinMessage.contains("%time")) {
	    joinMessage = getReplacedTime(joinMessage);
	}
	return joinMessage;
    }
    
    /**
     * Replaces all constants apart from color codes
     * @param Player
     * @return The quit message without the constants (%chatplayername, %playername, %group). The constants are replaced in the correct format with the information delivered by the player. %group returns only the first group
     */
    private String getMessagesQuit(Player pl) {
	if (advancedStatus == false) {
	    quitMessage = sm.getQuitMessage();
	}
	else {
	    quitMessage = am.getQuitMessage(pl);
	}
	quitMessage = getReplacedPlayername(quitMessage);
	quitMessage = getReplacedChatplayername(quitMessage);
	quitMessage = getReplacedGroup(quitMessage);
	quitMessage = getReplacedWorld(quitMessage);
	quitMessage = getReplacedCountry(quitMessage);
	return quitMessage;
    }
    
    
    /**
     * Checks whether the player has permission 'plm.join'
     * @param p Player - The player to check
     * @return true, if he has permission
     */
    private boolean getPermissionJoin(Player p) {
	if (this.cfg.getUsePermGeneral()) {
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
    private boolean getPermissionQuit(Player p) {
	if (this.cfg.getUsePermGeneral()) {
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
    
    
    
    private String getCapitalWord(String word) {
	String b = "";
	b = b + word.charAt(0);
	word = word.replaceFirst(b, b.toUpperCase());
	if (word.contains("_")) {
	    word = word.replaceAll("_", " ");
	}
	return word;
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
    
    private String getReplacedPlayername(String text) {
	return text.replaceAll("%playername", player.getName());
    }
    
    private String getReplacedChatplayername(String text) {
	if (this.chat != null) {
	    String name = (String) (this.chat.getPlayerPrefix(player) + player.getName() + this.chat.getPlayerSuffix(player));
	    return text.replaceAll("%chatplayername", name);
	} else if (this.chat == null && text.contains("%chatplayername")){
	    System.out.println("[PLM] PLM was not able to identify a chat format for this player!");
	    return getReplacedPlayername(text.replaceAll("%chatplayername", "%playername"));
	} else {
	    return text;
	}
    }
    
    private String getReplacedGroup(String text) {
	if (text.contains("%group") && this.permission != null) {
	    return text.replaceAll("%group", this.permission.getPlayerGroups(player)[0]);
	} else if (text.contains("%group") && this.permission == null) {
	    return text.replaceAll("%group", "unknown group");
	} else {
	    return text;
	}
    }
    
    private String getReplacedWorld(String text) {
	text = text.replaceAll("%world", player.getWorld().getName());
	text = text.replaceAll("%World", getCapitalWord(player.getWorld().getName()));
	return text;
    }
    
    private String getReplacedCountry(String text) {
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
     * Replaces %time with the period the player was offline
     * @param message - the message containing the time constant
     * @return the message without %time
     */
    private String getReplacedTime(String message) {
	long difference;
	if (plmFile.getLastLogin(playername) == 0L) {
	    difference = 0L;
	}
	else {
	    difference = System.currentTimeMillis() - plmFile.getLastLogin(playername);
	}
	//Keine Werte
	if (difference == 0L) {
	    message = message.replaceAll("%time", noLastLogin);
	}
	//Kleiner als eine Minute und nicht gleich 0
	if (difference < 60000L && difference != 0) {
	    long a = difference / 1000L;
	    if (a == 1L) {
		message = message.replaceAll("%time", a + " " + second);
	    }
	    else {
		message = message.replaceAll("%time", a + " " + seconds);
	    }
	}
	//Größer als eine Minute, kleiner als eine Stunde
	if (difference >= 60000L && difference < 3600000L) {
	    long a = difference / 60000L;
	    if (a == 1L) {
		message = message.replaceAll("%time", a + " " + minute);
	    }
	    else {
		message = message.replaceAll("%time", a + " " + minutes);
	    }
	}
	//Größer als eine Stunde, kleiner als ein Tag
	if (difference >= 3600000L && difference < 86400000L) {
	    long a = difference / 60000L;
	    long rest = a % 60;
	    a = a / 60;
	    if (a == 1L && rest == 0L) {
		message = message.replaceAll("%time", a + " " + hour);
	    }
	    else if (rest == 0L) {
		message = message.replaceAll("%time", a + " " + hours);
	    }
	    else if (a == 1L && rest == 1L) {
	    message = message.replaceAll("%time", a + " " + hour + " " + rest + " " + minute);
	    }
	    else if (a == 1L) {
		message = message.replaceAll("%time", a + " " + hour + " " + rest + " " + minutes);
	    }
	    else if (rest == 1L) {
		message = message.replaceAll("%time", a + " " + hours + " " + rest + " " + minute);
	    }
	    else {
		message = message.replaceAll("%time", a + " " + hours + " " + rest + " " + minutes);
	    }
	}
	//Größer als ein Tag, kleiner als 10 Tage
	if (difference >= 86400000L && difference < 864000000L) {
	    long a = difference / 3600000L;
	    long rest = a % 24;
	    a = a / 24;
	    if (a == 1L && rest == 0L) {
		message = message.replaceAll("%time", a + " " + day);
	    }
	    else if (rest == 0L) {
		message = message.replaceAll("%time", a + " " + days);
	    }
	    else if (a == 1L && rest == 1L) {
		message = message.replaceAll("%time", a + " " + day + " " + rest + " " + hour);
	    }
	    else if (a == 1L) {
		message = message.replaceAll("%time", a + " " + day + " " + rest + " " + hours);
	    }
	    else if (rest == 1L) {
		message = message.replaceAll("%time", a + " " + days + " " + rest + " " + hour);
	    }
	    else {
		message = message.replaceAll("%time", a + " " + days + " " + rest + " " + hours);
	    }
	}
	//Größer als 10 Tage, kleiner als 30 Tage
	if (difference >= 864000000L && difference < 2592000000L) {
	    long a = difference / 86400000L;
	    if (a == 1L) {
		message = message.replaceAll("%time", a + " " + day);
	    }
	    else {
		message = message.replaceAll("%time", a + " " + days);
	    }
	}
	//Größer als 30 Tage (1 Monat)
	if (difference >= 2592000000L) {
	    long a = difference / 2592000000L;
	    if (a == 1L) {
		message = message.replaceAll("%time", a + " " + month);
	    }
	    else {
		message = message.replaceAll("%time", a + " " + months);
	    }
	}
	return message;
    }
    
    private String getReplacedPlayerlist(String text) {
//	String m = "";
//	Player[] playerlist = plugin.getServer().getOnlinePlayers();
//	for (int i = 0; i < (playerlist.length - 1); i++) {
//	    m = m + playerlist[i].getName() + ", ";
//	}
//	m = m + playerlist[playerlist.length - 1].getName();
//	return text.replaceAll("%playerlist", m);
	String m = "";
	Player[] playerlist = plugin.getServer().getOnlinePlayers();
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
    
    private String getReplacedChatplayerlist(String text) {
//	    String m = "";
//	    Player[] playerlist = plugin.getServer().getOnlinePlayers();
//	    for (int i = 0; i < (playerlist.length - 1); i++) {
//		Player p = playerlist[i];
//		m = m + (this.chat.getPlayerPrefix(p) + p.getName() + this.chat.getPlayerSuffix(p)) + ", ";
//	    }
//	    Player p = playerlist[playerlist.length - 1];
//	    m = m + (this.chat.getPlayerPrefix(p) + p.getName() + this.chat.getPlayerSuffix(p));
//	    return text.replaceAll("%chatplayerlist", m);
	if (chat != null) {
	    String m = "";
	    Player[] playerlist = plugin.getServer().getOnlinePlayers();
	    for (int i = 0; i < (playerlist.length - 1); i++) {
		Player p = playerlist[i];
		if (!vnpHandler.isVanished(p.getName())) {
		    m = m + (this.chat.getPlayerPrefix(p) + p.getName() + this.chat.getPlayerSuffix(p)) + ", ";
		}
	    }
	    Player p = playerlist[playerlist.length - 1];
	    if (!vnpHandler.isVanished(p.getName())) {
		m = m + (this.chat.getPlayerPrefix(p) + p.getName() + this.chat.getPlayerSuffix(p));
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
	    return getReplacedPlayerlist(text.replaceAll("%chatplayerlist", "%playerlist"));
	}
    }
    
    private String getReplacedGroupplayerlist(String text) {
	if (permission != null) {
	    String m = "";
	    Player[] playerlist = plugin.getServer().getOnlinePlayers();
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
    
    private String getReplacedGroupchatplayerlist(String text) {
	if (permission != null && chat != null) {
	    String m = "";
	    Player[] playerlist = plugin.getServer().getOnlinePlayers();
	    for (int i = 0; i < (playerlist.length - 1); i++) {
		Player p = playerlist[i];
		if (permission.getPlayerGroups(p)[0] == permission.getPlayerGroups(player)[0] && !vnpHandler.isVanished(p.getName())) {
		    m = m + (this.chat.getPlayerPrefix(p) + p.getName() + this.chat.getPlayerSuffix(p)) + ", ";
		}
	    }
	    Player p = playerlist[playerlist.length - 1];
	    if (permission.getPlayerGroups(p)[0] == permission.getPlayerGroups(player)[0] && !vnpHandler.isVanished(p.getName())) {
		m = m + (this.chat.getPlayerPrefix(p) + p.getName() + this.chat.getPlayerSuffix(p));
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
    
    private void printWelcomeMessage(Player p, AdvancedMessages am) {
	String[] welcomeMessages = am.getWelcomeMessages(p);
	if (welcomeMessages != null) {
//	    System.out.println("messages != null");
	    for (int i = 0; i < welcomeMessages.length; i++) {
		welcomeMessages [i] = getReplacedPlayername(welcomeMessages[i]);
		welcomeMessages [i] = getReplacedChatplayername(welcomeMessages[i]);
		welcomeMessages [i] = getReplacedWorld(welcomeMessages[i]);
		welcomeMessages [i] = getReplacedCountry(welcomeMessages[i]);
		welcomeMessages [i] = getReplacedTime(welcomeMessages[i]);
		welcomeMessages [i] = getReplacedPlayerlist(welcomeMessages[i]);
		welcomeMessages [i] = getReplacedChatplayerlist(welcomeMessages[i]);
		welcomeMessages [i] = getReplacedGroupplayerlist(welcomeMessages[i]);
		welcomeMessages [i] = getReplacedGroupchatplayerlist(welcomeMessages[i]);
		welcomeMessages [i] = getReplacedGroup(welcomeMessages[i]);
	    }
	    int time = cfg.getDelay();
	    WelcomeMessagePrinter c = new WelcomeMessagePrinter();
	    c.start(time, welcomeMessages, p);
	}
    }
    
    private void printPublicMessages(Player p, AdvancedMessages am) {
	String[] publicMessages = am.getPublicMessages(p);
	if (publicMessages != null && ! vnpHandler.isVanished(player.getName())) {
//	    System.out.println("messages != null");
	    for (int i = 0; i < publicMessages.length; i++) {
		publicMessages [i] = getReplacedPlayername(publicMessages[i]);
		publicMessages [i] = getReplacedChatplayername(publicMessages[i]);
		publicMessages [i] = getReplacedWorld(publicMessages[i]);
		publicMessages [i] = getReplacedCountry(publicMessages[i]);
		publicMessages [i] = getReplacedTime(publicMessages[i]);
		publicMessages [i] = getReplacedPlayerlist(publicMessages[i]);
		publicMessages [i] = getReplacedChatplayerlist(publicMessages[i]);
		publicMessages [i] = getReplacedGroupplayerlist(publicMessages[i]);
		publicMessages [i] = getReplacedGroupchatplayerlist(publicMessages[i]);
		publicMessages [i] = getReplacedGroup(publicMessages[i]);
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
			if (! onlinePlayer[i].getName().equalsIgnoreCase(p.getName())) {
			    System.out.println(onlinePlayer.length);
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
		    if (! onlinePlayer[i].getName().equalsIgnoreCase(p.getName())) {
			receivers[j] = onlinePlayer[i];
		    }
		    else {
			b = 1;
		    }
		}
		sendPublicMessages(receivers, publicMessages);
	    }
	}
    }
    
    private void sendPublicMessages (Player[] receivers, String[] messages) {
	PublicMessagePrinter pmPrinter = new PublicMessagePrinter();
	pmPrinter.start(messages, receivers);
    }
}
