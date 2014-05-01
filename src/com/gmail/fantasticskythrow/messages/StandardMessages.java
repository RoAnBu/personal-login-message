package com.gmail.fantasticskythrow.messages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMLogger;
import com.gmail.fantasticskythrow.other.PLMToolbox;

public class StandardMessages {

	private final File messagesFile;
	private String joinMessage = "", quitMessage = "";
	private PLM plugin;
	private final PLMLogger plmLogger;

	public StandardMessages(PLM p) {
		this.plugin = p;
		plmLogger = plugin.getPLMLogger();
		messagesFile = new File(plugin.getDataFolder(), "messages.txt");
		checkMessagesFile();
		getMessages();
	}

	public void reload() {
		getMessages();
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
			plmLogger.logError("An error has occurred while reading messages.txt");
			plmLogger.logInfo("Please check the messages.txt file. Standard join and quit messages will be used");
			plmLogger.logInfo(e.getMessage());
			joinMessage = "&e%playername joined the game";
			quitMessage = "&e%playername left the game";
		}
	}

	private void checkMessagesFile() {
		try {
			if (!messagesFile.exists()) {
				PLMToolbox.overwriteMessagesFile(plugin);
			} else {
				plmLogger.logInfo("The file messages.txt was loaded");
			}
		} catch (Exception e) {
			plmLogger.logError("An error has occurred while checking the messages.txt");
			plmLogger.logInfo("Trying to replace it by default...");
			PLMToolbox.overwriteMessagesFile(plugin);
		}
	}

	public MessageData getJoinMessage() {
		return new MessageData(joinMessage, null);
	}

	public MessageData getQuitMessage() {
		return new MessageData(quitMessage, null);
	}
}
