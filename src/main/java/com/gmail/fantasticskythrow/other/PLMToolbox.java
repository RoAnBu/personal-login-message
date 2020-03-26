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

	private static final ILoggerWrapper logger = PLM.Companion.logger();

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

}
