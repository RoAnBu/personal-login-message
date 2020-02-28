package com.gmail.fantasticskythrow.messages;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.other.PLMToolbox;

public class PLMFile implements IPLMFile, Runnable {
	private static final Logger logger = LogManager.getLogger(PLMFile.class);

	private final File PLMFileData;
	private YamlConfiguration PConfig;
	private boolean errorStatus = false;

	public PLMFile(PLM plm) {
		PLMFileData = new File(plm.getDataFolder(), "PLM.yml");
		PConfig = PLMToolbox.loadPLMFile(PLMFileData, plm);
		if (PConfig == null) {
			errorStatus = true;
		} else {
			plm.getServer().getScheduler().scheduleSyncRepeatingTask(plm, this, 12000, 12000);
		}
	}

	@Override
	public void setPlayerQuitTime(Player player) {
		if (!errorStatus) {
			final String path = String.format("Players.%s", player.getUniqueId().toString());
			if (PConfig.contains("Players." + player.getName().toLowerCase())) {
				PConfig.set("Players." + player.getName().toLowerCase(), null);
			}
			PConfig.set(path, System.currentTimeMillis());
		}
	}

	@Override
	public long getLastLogin(Player player) {
		if (!errorStatus) {
			final String path = String.format("Players.%s", player.getUniqueId().toString());
			long oldLogin;
			if (!PConfig.contains(path) && PConfig.contains("Players." + player.getName().toLowerCase())) {
				oldLogin = PConfig.getLong("Players." + player.getName().toLowerCase());
				PConfig.set("Players." + player.getName().toLowerCase(), null);
				PConfig.set(path, oldLogin);
			}
			return PConfig.getLong(path);
		} else {
			return 0L;
		}
	}

	@Override
	public long getDifference(Player player) {
		final long lastLogin = getLastLogin(player);
		if (lastLogin != 0L) {
			return (long) (System.currentTimeMillis() - lastLogin);
		} else {
			return 0L;
		}
	}

	@Override
	public int getPlayerLogins(Player player) {
		if (!errorStatus) {
			final String path = String.format("logins.%s", player.getUniqueId().toString());
			final long oldLogins;
			if (PConfig.contains("logins." + player.getName().toLowerCase())) {
				oldLogins = PConfig.getLong("logins." + player.getName().toLowerCase());
				PConfig.set("logins." + player.getName().toLowerCase(), null);
				PConfig.set(path, oldLogins);
			}
			return PConfig.getInt(path);
		} else {
			return 0;
		}
	}

	@Override
	public long getTotalLogins() {
		if (!errorStatus) {
			return PConfig.getLong("totallogins");
		} else {
			return 0L;
		}
	}

	@Override
	public int getUniquePlayerLogins() {
		if (!errorStatus) {
			return PConfig.getInt("uniqueplayers");
		} else {
			return 0;
		}
	}

	@Override
	public void setPlayerLogin(Player player) {
		if (!errorStatus) {
			final String path = String.format("logins.%s", player.getUniqueId().toString());
			if (!PConfig.contains("logins." + player.getUniqueId().toString()) && !PConfig.contains("logins." + player.getName().toLowerCase())) {
				final int newUniqueValue;
				if (PConfig.contains("uniqueplayers")) {
					newUniqueValue = PConfig.getInt("uniqueplayers") + 1;
				} else {
					newUniqueValue = 1;
				}
				PConfig.set("uniqueplayers", newUniqueValue);
			} else if (PConfig.contains("logins." + player.getName().toLowerCase())) {
				int oldValue = PConfig.getInt("logins." + player.getName().toLowerCase());
				PConfig.set("logins." + player.getName().toLowerCase(), null);
				PConfig.set(path, oldValue);
			}
			final int newValue = PConfig.getInt(path) + 1;
			PConfig.set(path, newValue);
			final long newTotalValue = PConfig.getLong("totallogins") + 1L;
			PConfig.set("totallogins", newTotalValue);
		}
	}

	@Override
	public boolean getFirstEnabled() {
		if (PConfig.getString("firstenabled").equalsIgnoreCase("true") && !errorStatus) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setFirstEnabled(boolean b) {
		if (!errorStatus) {
			if (b)
				PConfig.set("firstenabled", "true");
			else
				PConfig.set("firstenabled", "false");
		}
	}

	@Override
	public String getCountryName(String englishName) {
		if (!errorStatus) {
			if (PConfig.contains("Countries" + englishName)) {
				return PConfig.getString("Countries" + englishName);
			} else {
				return englishName;
			}
		} else {
			return englishName;
		}
	}

	@Override
	public boolean getErrorStatus() {
		return errorStatus;
	}

	@Override
	public void run() {
		try {
			PConfig.save(PLMFileData);
			PConfig = YamlConfiguration.loadConfiguration(PLMFileData);
			logger.debug("[PLM] PLM.yml has been saved successfully.");
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.warn("PLM.yml could not be saved!");
			logger.warn("Please check whether PLM is permitted to write in PLM.yml!");
		} catch (NullPointerException ne) {
			logger.warn("PLM.yml could not be loaded or saved. Login and logout information will be lost");
		}
	}

}
