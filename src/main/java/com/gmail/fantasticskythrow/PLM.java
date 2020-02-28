package com.gmail.fantasticskythrow;

import com.gmail.fantasticskythrow.configuration.MainConfiguration;
import com.gmail.fantasticskythrow.messages.CommonListener;
import com.gmail.fantasticskythrow.messages.Messages;
import com.gmail.fantasticskythrow.messages.VanishStatusChangeEventFakeMessageListener;
import com.gmail.fantasticskythrow.messages.VanishStatusChangeEventListener;
import com.gmail.fantasticskythrow.other.PLMLogger;
import com.gmail.fantasticskythrow.other.PLMPluginConnector;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class. Just calls basic methods
 * @author Roman
 *
 */
public final class PLM extends JavaPlugin {

	private Messages messages = null;
	private boolean vaultErrorStatus = false;
	private Chat chat = null;
	private Permission permission = null;
	private MainConfiguration cfg;
	private PLMLogger plmLogger;
	private PLMPluginConnector plmPluginConn;

	/**
	 * Decides whether to use SM or AMM after it enabled the main configuration. Activates metrics, too.
	 */
	@Override
	public void onEnable() {
		try {
			cfg = new MainConfiguration(this);
			plmLogger = new PLMLogger(this);
			plmPluginConn = new PLMPluginConnector(this);
			if (cfg.getPluginStatus()) { //Activated
				if (!cfg.getAdvancedStatus()) { //Standard mode
					initStandardSetup();
				} else { //Advanced messages mode
					initAdvancedSetup();
				}
			} else { //Not activated
				plmLogger.logInfo("Personal Login Message is not enabled in config");
			}
		} catch (Exception e) { // Not handled exceptions
			plmLogger.logError("An unknown error has occurred while setting up PLM!");
			e.printStackTrace();
		}
	}

	/**
	 * Just a simple message that it was disabled
	 */
	@Override
	public void onDisable() {
		if (cfg.getPluginStatus()) {
			messages.getPlmFile().run(); //Save PLM.yml
		}
		plmLogger.logInfo("Personal Login Message disabled");
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
		plmLogger.logInfo("Personal Login Message is enabled");
	}

	private void initAdvancedSetup() {
		setupChat();
		setupPermissions();
		if (vaultErrorStatus || permission == null) { //If vault or permission/chat plugin is not available -> Standard setup
			plmLogger.logWarning("Sorry, you need Vault and a compatible permissions plugin to use the advanced messages mode!");
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
			plmLogger.logInfo("Advanced messages mode is enabled");
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
				plmLogger.logInfo("Found no chat plugin. Standard player format will be used.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error er) {
			plmLogger.logWarning("PLM was not able to find 'Vault'. Is it installed?");
			plmLogger.logWarning("Using chat format is now disabled");
			vaultErrorStatus = true;
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
				plmLogger.logWarning("Found no permission plugin!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error er) {
			if (!vaultErrorStatus)
				plmLogger.logError("An unknown error has occurred concerning Vault");
		}
	}

	public Permission getPermission() {
		return permission;
	}

	public Chat getChat() {
		return chat;
	}

	public MainConfiguration getCfg() {
		return cfg;
	}

	public PLMLogger getPLMLogger() {
		return plmLogger;
	}

	public PLMPluginConnector getPLMPluginConnector() {
		return plmPluginConn;
	}

	public void reloadMessages() {
		cfg.reloadConfiguration();
		messages.reload();
	}

}
