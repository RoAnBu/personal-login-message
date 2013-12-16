package com.gmail.fantasticskythrow.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.fantasticskythrow.PLM;

public class MainConfiguration {

	private PLM plugin;
	private YamlConfiguration cfg;
	private final File cfgData;

	private boolean pluginStatus = true, usepermissionsGeneral, usepermissionsPM, fakejoinmessage, fakequitmessage, advancedStatus, debugStatus,
			useChannels, useRandom;
	public String second, seconds, minute, minutes, hour, hours, day, days, month, months, noLastLogin;
	private int delay;
	private List<String> channels;

	public MainConfiguration(PLM plugin) {
		this.plugin = plugin;
		cfgData = new File(this.plugin.getDataFolder(), "config.yml");
		loadConfiguration();
	}

	private void loadConfiguration() {
		try {
			/*
			 * Set default values if necessary
			 */
			cfg = YamlConfiguration.loadConfiguration(cfgData);
			cfg.addDefault("general.enabled", "true");
			cfg.addDefault("general.usepermissions", "false");
			cfg.addDefault("general.debug", "false");
			cfg.addDefault("advancedmessages.enabled", "false");
			cfg.addDefault("advancedmessages.second", "second");
			cfg.addDefault("advancedmessages.seconds", "seconds");
			cfg.addDefault("advancedmessages.minute", "minute");
			cfg.addDefault("advancedmessages.minutes", "minutes");
			cfg.addDefault("advancedmessages.hour", "hour");
			cfg.addDefault("advancedmessages.hours", "hours");
			cfg.addDefault("advancedmessages.day", "day");
			cfg.addDefault("advancedmessages.days", "days");
			cfg.addDefault("advancedmessages.month", "month");
			cfg.addDefault("advancedmessages.months", "months");
			cfg.addDefault("advancedmessages.no last login", "no last login");
			cfg.addDefault("advancedmessages.userandom", "false");
			cfg.addDefault("Welcome messages.delayms", "200");
			cfg.addDefault("Public messages.usepermissions", "false");
			cfg.addDefault("VanishNoPacket.usefakejoinmessage", "false");
			cfg.addDefault("VanishNoPacket.usefakequitmessage", "false");
			cfg.addDefault("Use Channels", "false");
			List<String> channelList = new ArrayList<String>();
			channelList.add("Default");
			cfg.addDefault("Channels", channelList);
			cfg.options().copyDefaults(true);
			cfg.save(cfgData);
			/*
			 * Load values
			 */
			if (this.cfg.getString("general.enabled").equalsIgnoreCase("true")) {
				pluginStatus = true;
			} else {
				pluginStatus = false;
			}
			second = cfg.getString("advancedmessages.second");
			seconds = cfg.getString("advancedmessages.seconds");
			minute = cfg.getString("advancedmessages.minute");
			minutes = cfg.getString("advancedmessages.minutes");
			hour = cfg.getString("advancedmessages.hour");
			hours = cfg.getString("advancedmessages.hours");
			day = cfg.getString("advancedmessages.day");
			days = cfg.getString("advancedmessages.days");
			month = cfg.getString("advancedmessages.month");
			months = cfg.getString("advancedmessages.months");
			noLastLogin = cfg.getString("advancedmessages.no last login");
			if (cfg.getString("general.usepermissions").equalsIgnoreCase("true"))
				usepermissionsGeneral = true;
			else
				usepermissionsGeneral = false;
			if (cfg.getString("Public messages.usepermissions").equalsIgnoreCase("true"))
				usepermissionsPM = true;
			else
				usepermissionsPM = false;
			if (cfg.getString("VanishNoPacket.usefakejoinmessage").equalsIgnoreCase("true"))
				fakejoinmessage = true;
			else
				fakejoinmessage = false;
			if (cfg.getString("VanishNoPacket.usefakequitmessage").equalsIgnoreCase("true"))
				fakequitmessage = true;
			else
				fakequitmessage = false;
			if (cfg.getString("advancedmessages.enabled").equalsIgnoreCase("true"))
				advancedStatus = true;
			else
				advancedStatus = false;
			if (cfg.getString("general.debug").equalsIgnoreCase("true"))
				debugStatus = true;
			else
				debugStatus = false;
			if (cfg.getString("Use Channels").equalsIgnoreCase("true"))
				useChannels = true;
			else
				useChannels = false;
			if (cfg.getString("advancedmessages.userandom").equalsIgnoreCase("true"))
				useRandom = true;
			else
				useRandom = false;
			setInternalDelay();
			channels = cfg.getStringList("Channels");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("[PLM] was not able to load the config.yml");
			System.out.println("[PLM] Plugin is now disabled!");
			pluginStatus = false;
		}
	}

	public void reloadConfiguration() {
		this.loadConfiguration();
	}

	private void setInternalDelay() {
		String a = cfg.getString("Welcome messages.delayms");
		try {
			delay = Integer.parseInt(a);
		} catch (NumberFormatException e) {
			System.out.println("[PLM] Could not find a number at delayms in the config.yml!");
			delay = 200;
		}
	}

	public boolean getPluginStatus() {
		return pluginStatus;
	}

	public boolean getUsePermGeneral() {
		return this.usepermissionsGeneral;
	}

	public boolean getUsePermPM() {
		return this.usepermissionsPM;
	}

	public boolean getUseFakeJoinMsg() {
		return this.fakejoinmessage;
	}

	public boolean getUseFakeQuitMsg() {
		return this.fakequitmessage;
	}

	public int getDelay() {
		return delay;
	}

	public boolean getAdvancedStatus() {
		return this.advancedStatus;
	}

	public boolean getDebugStatus() {
		return debugStatus;
	}

	public boolean getUseChannels() {
		return useChannels;
	}

	public List<String> getChannels() {
		return channels;
	}

	public boolean getUseRandom() {
		return useRandom;
	}

}
