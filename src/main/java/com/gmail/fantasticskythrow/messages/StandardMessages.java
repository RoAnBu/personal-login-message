package com.gmail.fantasticskythrow.messages;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMToolbox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class StandardMessages {

	private final File messagesFile;
	private String joinMessage = "", quitMessage = "";
	private PLM plugin;

	private static final Logger logger = LogManager.getLogger(StandardMessages.class);

	public StandardMessages(PLM p) {
		this.plugin = p;
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
			logger.error("An error has occurred while reading messages.txt");
			logger.info("Please check the messages.txt file. Standard join and quit messages will be used");
			logger.info(e.getMessage());
			joinMessage = "&e%playername joined the game";
			quitMessage = "&e%playername left the game";
		}
	}

	private void checkMessagesFile() {
		try {
			if (!messagesFile.exists()) {
				PLMToolbox.overwriteMessagesFile(plugin);
			} else {
				logger.info("The file messages.txt was loaded");
			}
		} catch (Exception e) {
			logger.error(e);
			logger.error("An error has occurred while checking the messages.txt");
			logger.info("Trying to replace it by default...");
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
