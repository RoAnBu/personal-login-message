package com.gmail.fantasticskythrow.messages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMLogger;

public class StandardMessages {

	private File messagesFile;
	private String joinMessage = "", quitMessage = "";
	private PLM plugin;
	private final PLMLogger plmLogger;

	public StandardMessages(PLM p) {
		this.plugin = p;
		plmLogger = plugin.getPLMLogger();
		checkMessagesFile();
	}

	private void getMessages() {
		try {
			FileReader fr = new FileReader(messagesFile);
			BufferedReader br = new BufferedReader(fr);
			br.readLine();
			joinMessage = br.readLine();
			br.readLine();
			br.readLine();
			quitMessage = br.readLine();
			br.close();
		}

		catch (Exception e) {
			plmLogger.logError("[PLM] An error has occurred while reading messages.txt");
			plmLogger.logInfo("[PLM] Please check the messages.txt file. Standard join and quit messages will be used");
			plmLogger.logInfo(e.getMessage());
			joinMessage = "&e%playername joined the game";
			quitMessage = "&e%playername left the game";
		}
	}

	private void checkMessagesFile() {
		try {
			messagesFile = new File(plugin.getDataFolder(), "messages.txt");
			if (!messagesFile.exists()) {
				overwriteMessagesFile(plugin);
			} else {
				checkMFContent();
				plmLogger.logInfo("[PLM] The file messages.txt was loaded");
			}
		} catch (Exception e) {
			plmLogger.logError("[PLM] An error has occurred while checking the messages.txt");
			plmLogger.logInfo("[PLM] Trying to replace it by default...");
			overwriteMessagesFile(plugin);
		}
	}

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
			bw.write("Colors are set with the standard minecraft color codes (with '&')");
			bw.newLine();
			bw.write("%playername will be replaced by the normal name.");
			bw.newLine();
			bw.write("%chatplayername will be replaced by the name with color and prefixes (suffixes too)");
			bw.newLine();
			bw.write("%group will be replaced by the name of the player's group");
			bw.newLine();
			bw.write("%world: The world where the player spawned. Only the complete name right now.");
			bw.newLine();
			bw.write("%World outputs the world with a capital letter and no _");
			bw.newLine();
			bw.write("NOTE: Please don't move the lines otherwise the plugin will return wrong values!!");
			bw.close();
			return true;
		} catch (Exception e) {
			System.out.println("[PLM] Editing 'messages.txt' was not possible! Check the plugin's folder");
			return false;
		}
	}

	private void checkMFContent() {
		try {
			FileReader fr = new FileReader(messagesFile);
			BufferedReader br = new BufferedReader(fr);
			for (int a = 12; a > 1; a--) {
				br.readLine();
			}
			if (br.readLine() == null) {
				FileWriter fw = new FileWriter(messagesFile, true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.newLine();
				bw.write("%world: The world where the player spawned. Only the complete name right now.");
				bw.newLine();
				bw.write("%World outputs the world with a capital letter and no _");
				bw.close();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Getters
	 */
	public MessageData getJoinMessage() {
		getMessages();
		return new MessageData(joinMessage, null);
	}

	public MessageData getQuitMessage() {
		getMessages();
		return new MessageData(quitMessage, null);
	}
}
