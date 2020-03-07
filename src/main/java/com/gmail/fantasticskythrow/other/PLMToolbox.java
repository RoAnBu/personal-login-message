package com.gmail.fantasticskythrow.other;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.other.logging.ILoggerWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A big arrangement of tools for replacing and checking
 * @author FantasticSkyThrow
 *
 */
public class PLMToolbox {

	private static final ILoggerWrapper logger = PLM.logger();

	/**
	 * Checks whether the player has permission 'plm.join'
	 * @param p Player The player to check
	 * @return true, if he has permission
	 */
	public static boolean getPermissionJoin(boolean usePermGeneral, Player p) {
		if (usePermGeneral) {
			if (p.hasPermission("plm.join")) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks whether the player has permission 'plm.quit'
	 * @param p Player The player to check
	 * @return true, if he has permission
	 */
	public static boolean getPermissionQuit(boolean usePermGeneral, Player p) {
		if (usePermGeneral) {
			if (p.hasPermission("plm.quit")) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Overwrites messages.txt from Standard mode
	 * @param plugin The main PLM object to find the location of the file
	 * @return true if successful, false in case of an error
	 */
	public static boolean overwriteMessagesFile(PLM plugin) {
		try {
			File messagesFile = new File(plugin.getDataFolder(), "messages.txt");
			FileWriter fw = new FileWriter(messagesFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Join message:");
			bw.newLine();
			bw.write("&e%playername joined the game");
			bw.newLine();
			bw.newLine();
			bw.write("Quit message:");
			bw.newLine();
			bw.write("&e%playername left the game");
			bw.newLine();
			bw.newLine();
			bw.write("How to write own messages:");
			bw.newLine();
			bw.write("Visit http://dev.bukkit.org/bukkit-plugins/personal-login-message/pages/standard-mode/");
			bw.newLine();
			bw.write("NOTE: Please don't move the lines. Otherwise the plugin will return wrong values!!");
			bw.close();
			return true;
		} catch (Exception e) {
			logger.error("Editing 'messages.txt' was not possible! Check the plugin's folder");
			return false;
		}
	}

	/**
	 * Looks for channels in the given section
	 * @param path the path e.g. "Groups.Admin" without dot and additional endings
	 * @param yml The concerning file
	 * @return The channels or null if no channels have been found
	 */
	public static String[] getChannels(String path, YamlConfiguration yml) {
		if (yml.contains(path + ".CH")) {
			String[] channels = yml.getString(path + ".CH").split(", ");
			return channels;
		} else {
			return null;
		}
	}

	public static YamlConfiguration loadPLMFile(final File PLMFileData, final PLM plugin) {
		YamlConfiguration PConfig = YamlConfiguration.loadConfiguration(PLMFileData);
		if (!PConfig.contains("firstenabled")) {
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
			return PConfig;
		} catch (IOException ex) {
			logger.error(ex.getMessage());
			logger.error("PLM.yml is not available!");
			logger.error("Please check whether PLM is permitted to write in PLM.yml!");
			// TODO duplicate code, null return
			return null;
		}
	}

}
