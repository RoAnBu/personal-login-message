package com.gmail.fantasticskythrow.configuration;

import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.fantasticskythrow.PLM;

public class MainConfiguration {

	private PLM plugin;
	private FileConfiguration cfg;

	private boolean pluginStatus = true, usepermissionsGeneral, usepermissionsPM, fakejoinmessage, fakequitmessage, advancedStatus, debugStatus;
	public String second, seconds, minute, minutes, hour, hours, day, days, month, months, noLastLogin;
	private int delay;

	public MainConfiguration(PLM plugin) {
		this.plugin = plugin;
		loadConfiguration();
	}

	private void loadConfiguration() {
		try {
			/*
			 * Set default values if necessary
			 */
			cfg = plugin.getConfig();
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
			cfg.addDefault("Welcome messages.delayms", "200");
			cfg.addDefault("Public messages.usepermissions", "false");
			cfg.addDefault("VanishNoPacket.usefakejoinmessage", "false");
			cfg.addDefault("VanishNoPacket.usefakequitmessage", "false");
			cfg.options().copyDefaults(true);
			plugin.saveConfig();
			/*
			 * Load values
			 */
			setStatus();
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
			setInternalDelay();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[PLM] was not able to load the config.yml");
			System.out.println("[PLM] Plugin is now disabled!");
			pluginStatus = false;
		}
	}

	public void reloadConfiguration() {
		this.loadConfiguration();
	}

	private void setStatus() {
		if (this.cfg.getString("general.enabled").equalsIgnoreCase("true")) {
			pluginStatus = true;
		} else {
			pluginStatus = false;
		}
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
}
