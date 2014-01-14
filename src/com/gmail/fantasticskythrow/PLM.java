package com.gmail.fantasticskythrow;

import java.io.IOException;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.fantasticskythrow.configuration.MainConfiguration;
import com.gmail.fantasticskythrow.messages.CommonListener;
import com.gmail.fantasticskythrow.messages.Messages;
import com.gmail.fantasticskythrow.messages.VanishStatusChangeEventListener;
import com.gmail.fantasticskythrow.other.Metrics;
import com.gmail.fantasticskythrow.other.Metrics.Graph;
import com.gmail.fantasticskythrow.other.PLMLogger;
import com.gmail.fantasticskythrow.other.PLMPluginConnector;

/**
 * Main class. Just calls basic methods
 * @author Roman
 *
 */
public final class PLM extends JavaPlugin {

	private Messages m = null;
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
				activateMetrics();
			} else { //Not activated
				plmLogger.logInfo("[PLM] Personal Login Message is not enabled in config");
			}
		} catch (Exception e) { // Not handled exceptions
			plmLogger.logError("[PLM] An unknown error has occurred while setting up PLM!");
			e.printStackTrace();
		}
	}

	/**
	 * Just a simple message that it was disabled
	 */
	@Override
	public void onDisable() {
		plmLogger.logInfo("[PLM] Personal Login Message disabled");
	}

	private void initStandardSetup() {
		setupChat();
		setupPermissions();
		m = new Messages(this, false);
		if (m.getVnpHandler().isPluginInstalled()) {
			this.getServer().getPluginManager().registerEvents(new VanishStatusChangeEventListener(m), this);
		} else {
			this.getServer().getPluginManager().registerEvents(new CommonListener(m), this);
		}
		plmLogger.logInfo("[PLM] Personal Login Message is enabled");
	}

	private void initAdvancedSetup() {
		setupChat();
		setupPermissions();
		if (vaultErrorStatus == true || permission == null) { //If vault or permission/chat plugin is not available -> Standard setup
			plmLogger.logWarning("[PLM] Sorry, you need Vault and a compatible permissions plugin to use the advanced messages mode!");
			initStandardSetup();
		} else { //Activate AdvancedMessages, because vault is active and it's enabled
			m = new Messages(this, true);
			if (m.getVnpHandler().isPluginInstalled()) {
				this.getServer().getPluginManager().registerEvents(new VanishStatusChangeEventListener(m), this);
			} else {
				this.getServer().getPluginManager().registerEvents(new CommonListener(m), this);
			}
			plmLogger.logInfo("[PLM] Advanced messages mode is enabled");
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
				plmLogger.logInfo("[PLM] Found no chat plugin. Standard player format will be used.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error er) {
			plmLogger.logWarning("PLM was not able to find 'Vault'. Is it installed?");
			plmLogger.logWarning("[PLM] Using chat format is now disabled");
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
				plmLogger.logWarning("[PLM] Found no permission plugin!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error er) {
			if (vaultErrorStatus == false)
				plmLogger.logError("[PLM] An unknown error has occurred concerning Vault");
		}
	}

	/**
	 * Activates metrics which are in an other package
	 */
	private void activateMetrics() {
		try {
			Metrics metrics = new Metrics(this);
			Graph graph = metrics.createGraph("Advanced Messages Mode enabled?");
			graph.addPlotter(new Metrics.Plotter(cfg.getAdvancedStatus() ? "Yes" : "No") {
				@Override
				public int getValue() {
					return 1;
				}
			});
			Graph graph2 = metrics.createGraph("Vault installed?");
			graph2.addPlotter(new Metrics.Plotter(vaultErrorStatus ? "No" : "Yes") {
				@Override
				public int getValue() {
					return 1;
				}
			});
			metrics.start();
		} catch (IOException e) {
			plmLogger.logError(e.getMessage());
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
		m.reload();
	}

}
