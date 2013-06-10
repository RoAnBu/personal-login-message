package com.gmail.fantasticskythrow.messages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.fantasticskythrow.PLM;

public class PLMFile {
	private PLM plugin;
	private File PLMFileData;
	private YamlConfiguration PConfig;
	private boolean errorStatus = false;

	public PLMFile(PLM p) {
		plugin = p;
		loadFile();
	}

	private void loadFile() {
		PLMFileData = new File(plugin.getDataFolder(), "PLM.yml");
		PConfig = YamlConfiguration.loadConfiguration(PLMFileData);
		if (PConfig.contains("firstenabled") == false) {
			PConfig.set("firstenabled", "false");
		}
		if (!PConfig.contains("Countries")) {
			PConfig.set("Countries.United States", "United States");
			PConfig.set("Countries.France", "France");
			PConfig.set("Countries.Germany", "Germany");
			PConfig.set("Countries.Brazil", "Brazil");
			PConfig.set("Countries.Netherlands", "Netherlands");
			PConfig.set("Countries.United Kingdom", "United Kingdom");
			PConfig.set("Countries.Slovenia", "Slovenia");
			PConfig.set("Countries.Bulgaria", "Bulgaria");
			PConfig.set("Countries.Canada", "Canada");
			PConfig.set("Countries.Mexico", "Mexico");
			PConfig.set("Countries.Italy", "Italy");
			PConfig.set("Countries.Spain", "Spain");
			PConfig.set("Countries.Australia", "Australia");
			PConfig.set("Countries.India", "India");
			PConfig.set("Countries.Russian Federation", "Russian Federation");
			PConfig.set("Countries.Your Country", "Your Country");
		}
		if (!PConfig.contains("totallogins")) {
			PConfig.set("totallogins", 0L);
		}
		if (!PConfig.contains("uniqueplayers")) {
			PConfig.set("uniqueplayers", 0);
		}
		try {
			PConfig.save(PLMFileData);
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
			System.out.println("[PLM] PLM.yml is not available!");
			System.out.println("[PLM] Please check whether PLM is permitted to write in PLM.yml!");
			errorStatus = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPlayerQuitTime(String playername) {
		PConfig = YamlConfiguration.loadConfiguration(PLMFileData);
		final String path = String.format("Players.%s", playername);
		PConfig.set(path, System.currentTimeMillis());
		try {
			PConfig.save(PLMFileData);
		} catch (IOException e) {
			System.out.println("[PLM] PLM.yml is not available! Could'nt save the quit time!");
			System.out.println("[PLM] Please check whether PLM is permitted to write in PLM.yml!");
		}
	}

	public long getLastLogin(String playername) {
		PConfig = YamlConfiguration.loadConfiguration(PLMFileData);
		if (errorStatus == false) {
			return PConfig.getLong(String.format("Players.%s", playername));
		} else {
			return 0L;
		}
	}

	public int getPlayerLogins(String playername) {
		final String path = String.format("logins.%s", playername);
		PConfig = YamlConfiguration.loadConfiguration(PLMFileData);
		if (errorStatus == false) {
			return PConfig.getInt(path);
		} else {
			return 0;
		}
	}

	public long getTotalLogins() {
		PConfig = YamlConfiguration.loadConfiguration(PLMFileData);
		if (errorStatus == false) {
			return PConfig.getLong("totallogins");
		} else {
			return 0L;
		}
	}

	public int getUniquePlayerLogins() {
		PConfig = YamlConfiguration.loadConfiguration(PLMFileData);
		if (errorStatus == false) {
			return PConfig.getInt("uniqueplayers");
		} else {
			return 0;
		}
	}

	public void setPlayerLogin(String playername) {
		PConfig = YamlConfiguration.loadConfiguration(PLMFileData);

		if (!PConfig.contains("logins." + playername)) {
			final int newUniqueValue;
			if (PConfig.contains("uniqueplayers")) {
				newUniqueValue = PConfig.getInt("uniqueplayers") + 1;
			} else {
				newUniqueValue = 1;
			}
			PConfig.set("uniqueplayers", newUniqueValue);
		}

		final String path = String.format("logins.%s", playername);
		final int newValue = PConfig.getInt(path) + 1;
		PConfig.set(path, newValue);

		final long newTotalValue = PConfig.getLong("totallogins") + 1L;
		PConfig.set("totallogins", newTotalValue);

		try {
			PConfig.save(PLMFileData);
		} catch (IOException e) {
			System.out.println("[PLM] PLM.yml is not available!");
			System.out.println("[PLM] Please check whether PLM is permitted to write in PLM.yml!");
		}
	}

	public boolean getFirstEnabled() {
		PConfig = YamlConfiguration.loadConfiguration(PLMFileData);
		if (PConfig.getString("firstenabled").equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}

	public void setFirstEnabled(boolean b) {
		PConfig = YamlConfiguration.loadConfiguration(PLMFileData);
		if (b == true)
			PConfig.set("firstenabled", "true");
		else
			PConfig.set("firstenabled", "false");
		try {
			PConfig.save(PLMFileData);
		} catch (IOException e) {
			System.out.println("[PLM] PLM.yml is not available!");
			System.out.println("[PLM] Please check whether PLM is permitted to write in PLM.yml!");
		}
	}

	public String getCountryName(String englishName) {
		if (PConfig.contains("Countries" + englishName)) {
			return PConfig.getString("Countries" + englishName);
		} else {
			return englishName;
		}
	}

	public boolean getErrorStatus() {
		return errorStatus;
	}
}
