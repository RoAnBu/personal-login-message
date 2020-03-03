package com.gmail.fantasticskythrow;

import com.gmail.fantasticskythrow.configuration.AppConfiguration;
import com.gmail.fantasticskythrow.messages.Messages;
import com.gmail.fantasticskythrow.messages.listener.CommonListener;
import com.gmail.fantasticskythrow.messages.listener.VanishStatusChangeEventFakeMessageListener;
import com.gmail.fantasticskythrow.messages.listener.VanishStatusChangeEventListener;
import com.gmail.fantasticskythrow.other.PLMPluginConnector;
import com.gmail.fantasticskythrow.other.logging.BukkitLoggerWrapper;
import com.gmail.fantasticskythrow.other.logging.ILoggerWrapper;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class PLM extends JavaPlugin {

	private Messages messages = null;
	private boolean isVaultUnavailable = false;
	private Chat chat = null;
	private Permission permission = null;
	private AppConfiguration cfg;
	private PLMPluginConnector plmPluginConn;

	private static BukkitLoggerWrapper logger = new BukkitLoggerWrapper(null);

	public static ILoggerWrapper logger() { return logger; }

	/**
	 * Decides whether to use StandardMode or AdvancedMessagesMode after it enabled the main configuration.
	 */
	@Override
	public void onEnable() {
		setupLogging();
		try {
			cfg = new AppConfiguration(new File(this.getDataFolder(), "config.yml"));
			plmPluginConn = new PLMPluginConnector(this);
			if (cfg.getPluginEnabled()) {
				if (!cfg.getAdvancedStatus()) { //Standard mode
					initStandardSetup();
				} else { //Advanced messages mode
					initAdvancedSetup();
				}
			} else {
				logger.info("Personal Login Message is not enabled in config");
			}
		} catch (Exception e) { // Not handled exceptions
			logger.error("An unknown error has occurred while setting up PLM!");
			logger.error(e);
		}
	}

	@Override
	public void onDisable() {
		if (cfg.getPluginEnabled()) {
			messages.getPlmFile().save();
		}
		logger.info("Personal Login Message disabled");
	}

	private void setupLogging() {
		logger.setLogger(this.getLogger());
	}

	private void initStandardSetup() {
		setupChat();
		setupPermissions();
		messages = new Messages(this, false);
		if (messages.getVnpHandler().isPluginInstalled() && !cfg.getReplaceVnpFakeMsg()) {
			this.getServer().getPluginManager().registerEvents(new VanishStatusChangeEventListener(messages), this);
		} else if (messages.getVnpHandler().isPluginInstalled() && cfg.getReplaceVnpFakeMsg()) {
			this.getServer().getPluginManager().registerEvents(new VanishStatusChangeEventFakeMessageListener(messages), this);
		} else {
			this.getServer().getPluginManager().registerEvents(new CommonListener(messages), this);
		}
		logger.info("Personal Login Message is enabled");
	}

	private void initAdvancedSetup() {
		setupChat();
		setupPermissions();
		if (isVaultUnavailable || permission == null) { //If vault or permission/chat plugin is not available -> Standard setup
			logger.warn("Sorry, you need Vault and a compatible permissions plugin to use the advanced messages mode!");
			initStandardSetup();
		} else { //Activate AdvancedMessages, because vault is active and it's enabled
			messages = new Messages(this, true);
			if (messages.getVnpHandler().isPluginInstalled() && !cfg.getReplaceVnpFakeMsg()) {
				this.getServer().getPluginManager().registerEvents(new VanishStatusChangeEventListener(messages), this);
			} else if (messages.getVnpHandler().isPluginInstalled() && cfg.getReplaceVnpFakeMsg()) {
				this.getServer().getPluginManager().registerEvents(new VanishStatusChangeEventFakeMessageListener(
						messages), this);
			} else {
				this.getServer().getPluginManager().registerEvents(new CommonListener(messages), this);
			}
			logger.info("Advanced messages mode is enabled");
		}
	}

	/**
	 * setupChat tries to find a chat plugin hooked by vault. It sends a message to console if no chat plugin was found or vault is not installed.
	 */
	private void setupChat() {
		try {
			RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
			if (chatProvider != null) {
				chat = chatProvider.getProvider();
			} else {
				logger.info("Found no chat plugin. Standard player format will be used.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error er) {
			logger.warn("PLM was not able to find 'Vault'. Is it installed?");
			logger.warn("Using chat format is now disabled");
			logger.trace(er);
			isVaultUnavailable = true;
		}
	}

	/**
	 * A try to find Vault and setup the hooked permission plugin. This is only called if setupChat() was successful.
	 * Any error here will be printed out in the console
	 */
	private void setupPermissions() {
		try {
			RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(
					net.milkbowl.vault.permission.Permission.class);
			if (permissionProvider != null) {
				permission = permissionProvider.getProvider();
			} else {
				logger.warn("Found no permission plugin!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error er) {
			if (!isVaultUnavailable) {
				logger.error("An unknown error has occurred concerning Vault");
				logger.error(er);
			}
		}
	}

	public Permission getPermission() {
		return permission;
	}

	public Chat getChat() {
		return chat;
	}

	public AppConfiguration getCfg() {
		return cfg;
	}

	public PLMPluginConnector getPLMPluginConnector() {
		return plmPluginConn;
	}

	public void reloadMessages() {
		cfg.reloadConfiguration();
		messages.reload();
	}

}
