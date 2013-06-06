package com.gmail.fantasticskythrow.other;

import java.util.logging.Logger;

import com.gmail.fantasticskythrow.PLM;

public class PLMLogger {

	private PLM plugin;
	private final Logger LOGGER;

	public PLMLogger(PLM plugin) {
		this.plugin = plugin;
		this.LOGGER = this.plugin.getServer().getLogger();
	}

	public void logInfo(String message) {
		LOGGER.info(message);
	}

	public void logError(String message) {
		LOGGER.severe(message);
	}

	public void logWarning(String message) {
		LOGGER.warning(message);
	}

	public void logDebug(String message) {
		if (plugin.getCfg().getDebugStatus()) {
			LOGGER.info(message);
		}
	}
}
