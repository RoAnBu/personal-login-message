package com.gmail.fantasticskythrow;

import com.gmail.fantasticskythrow.configuration.AppConfiguration;
import com.gmail.fantasticskythrow.messages.MessagesModeManager;
import com.gmail.fantasticskythrow.messages.listener.CommonListener;
import com.gmail.fantasticskythrow.messages.listener.VanishStatusChangeEventFakeMessageListener;
import com.gmail.fantasticskythrow.messages.listener.VanishStatusChangeEventListener;
import com.gmail.fantasticskythrow.other.logging.BukkitLoggerWrapper;
import com.gmail.fantasticskythrow.other.logging.ILoggerWrapper;
import com.gmail.fantasticskythrow.other.plugins.IPLMPluginConnector;
import com.gmail.fantasticskythrow.other.plugins.PLMPluginConnector;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class PLM extends JavaPlugin {

	private MessagesModeManager messagesModeManager = null;
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
		if (!isMinecraftVersionSupported()) {
			logger.error("Minecraft version below 1.7.8 are no longer supported, your version is: " + getMinecraftVersion());
			logger.error("Disabling PLM");
			return;
		}
		try {
			cfg = new AppConfiguration(new File(this.getDataFolder(), "config.yml"));
			plmPluginConn = new PLMPluginConnector(this.getServer().getPluginManager());
			if (cfg.getPluginEnabled()) {
				if (!cfg.getAdvancedStatus()) { //Standard mode
					initStandardMode();
				} else { //Advanced messages mode
					initAdvancedMode();
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
			messagesModeManager.getPlmFile().save();
		}
		logger.info("Personal Login Message disabled");
	}

	private void setupLogging() {
		logger.setLogger(this.getLogger());
	}

	private void initStandardMode() {
		setupProviders();
		messagesModeManager = new MessagesModeManager(this, false);
		registerEventListeners();
		logger.info("Personal Login Message is enabled");
	}

	private void initAdvancedMode() {
		setupProviders();
		if (isVaultUnavailable || permission == null) { //If vault or permission/chat plugin is not available -> Standard setup
			logger.warn("Sorry, you need Vault and a compatible permissions plugin to use the advanced messages mode!");
			initStandardMode();
		} else { //Activate AdvancedMessages, because vault is active and it's enabled
			messagesModeManager = new MessagesModeManager(this, true);
			registerEventListeners();
			logger.info("Advanced messages mode is enabled");
		}
	}

	private void setupProviders() {
		setupChatProvider();
		setupPermissionProvider();
	}

	private void registerEventListeners() {
		if (messagesModeManager.getVanishNoPacketManager()
		                       .isPluginInstalled() && !cfg.getReplaceVnpFakeMsg()) {
			this.getServer()
			    .getPluginManager()
			    .registerEvents(new VanishStatusChangeEventListener(messagesModeManager), this);
		} else if (messagesModeManager.getVanishNoPacketManager()
		                              .isPluginInstalled() && cfg.getReplaceVnpFakeMsg()) {
			this.getServer()
			    .getPluginManager()
			    .registerEvents(new VanishStatusChangeEventFakeMessageListener(messagesModeManager), this);
		} else {
			this.getServer()
			    .getPluginManager()
			    .registerEvents(new CommonListener(messagesModeManager), this);
		}
	}

	/**
	 * setupChat tries to find a chat plugin hooked by vault. It sends a message to console if no chat plugin was found or vault is not installed.
	 */
	private void setupChatProvider() {
		try {
			RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
			if (chatProvider != null) {
				chat = chatProvider.getProvider();
			} else {
				logger.info("Found no chat plugin. Standard player format will be used.");
			}
		} catch (Error er) {
			logger.warn("PLM was not able to find 'Vault'. Is it installed?");
			logger.warn("Using chat format is now disabled");
			logger.trace(er);
			isVaultUnavailable = true;
		}
	}

	/**
	 * Tries to find Vault and setup the hooked permission plugin. This is only called if setupChatProvider() was successful.
	 */
	private void setupPermissionProvider() {
		try {
			RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(
					net.milkbowl.vault.permission.Permission.class);
			if (permissionProvider != null) {
				permission = permissionProvider.getProvider();
			} else {
				logger.warn("Found no permission plugin!");
			}
		} catch (Error er) {
			if (!isVaultUnavailable) {
				logger.error("An unknown error has occurred concerning Vault");
				throw er;
			}
		}
	}

	private boolean isMinecraftVersionSupported() {
		return getMinecraftVersion() >= 178;

	}

	/**
	 * Use this function to compare server versions
	 * @return the version as an integer e.g.: 1.6.4-R2.0 -> 164
	 */
	private int getMinecraftVersion() {
		String version = getServer().getBukkitVersion().split("-")[0];
		version = version.replaceAll("\\.", "");
		int versionNumber = 0;
		try {
			versionNumber = Integer.parseInt(version);
		} catch (NumberFormatException ne) {
			logger.error("An error occurred while analysing the Minecraft server version!");
			logger.error("Please report this problem as fast as possible");
		}
		return versionNumber;
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

	public IPLMPluginConnector getPLMPluginConnector() {
		return plmPluginConn;
	}

	public void reloadMessages() {
		cfg.reloadConfiguration();
		messagesModeManager.reloadMessageConfigFiles();
	}

}
