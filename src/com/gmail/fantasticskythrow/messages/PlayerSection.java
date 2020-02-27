package com.gmail.fantasticskythrow.messages;

import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMToolbox;
import com.gmail.fantasticskythrow.other.SectionSubTypes;
import com.gmail.fantasticskythrow.other.SectionTypes;

public final class PlayerSection {

	protected static boolean checkMessagesJoin(String playername, String playerpath, YamlConfiguration yml, long difference, AdvancedMessages am) {
		String message = PLMToolbox.getBackMessage(yml, playerpath, difference);
		if (message != null) {
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(playerpath, yml), SectionTypes.PLAYER, SectionSubTypes.BACK_MESSAGE));
			return true;
		}
		if (yml.contains(playerpath + ".JM1")) {
			message = PLMToolbox.getMessage(playerpath + ".JM", yml);
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(playerpath, yml), SectionTypes.PLAYER, SectionSubTypes.JOIN_MESSAGE));
			return true;
		}
		return false;
	}

	protected static boolean checkMessagesQuit(String playername, String playerpath, YamlConfiguration yml, AdvancedMessages am) {
		String message;
		if (yml.contains(playerpath + ".QM1")) {
			message = PLMToolbox.getMessage(playerpath + ".QM", yml);
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(playerpath, yml), SectionTypes.PLAYER, SectionSubTypes.QUIT_MESSAGE));
			return true;
		}
		return false;
	}

	protected static boolean checkFirstMessage(String playerpath, long lastLogin, YamlConfiguration yml, AdvancedMessages am) {
		if (lastLogin == 0L && yml.contains(playerpath + ".FM1")) {
			am.setMessage(new MessageData(PLMToolbox.getMessage(playerpath + ".FM", yml), PLMToolbox.getChannels(playerpath, yml),
					SectionTypes.PLAYER, SectionSubTypes.FIRST_MESSAGE));
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkWelcomeMessages(String playerpath, YamlConfiguration yml, AdvancedMessages am) {
		final String path = playerpath + ".WM";
		if (yml.contains(path + "1")) {
			am.setWelcomeMessages(PLMToolbox.getAllMessages(path, yml));
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkPublicMessages(String playerpath, YamlConfiguration yml, AdvancedMessages am) {
		final String path = playerpath + ".PM";
		if (yml.contains(path + "1")) {
			am.setPublicMessages(PLMToolbox.getAllMessages(path, yml));
			return true;
		} else {
			return false;
		}
	}

	protected static ArrayList<MessageData> getJoinMessages(YamlConfiguration yml, long difference, long lastLogin, final String playerpath) {
		ArrayList<MessageData> messages = new ArrayList<MessageData>();
		String[] channels = PLMToolbox.getChannels(playerpath, yml);
		if (lastLogin == 0L && yml.contains(playerpath + ".FM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(playerpath + ".FM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.PLAYER, SectionSubTypes.FIRST_MESSAGE));
			}
		}
		if (yml.contains(playerpath + ".JM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(playerpath + ".JM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.PLAYER, SectionSubTypes.JOIN_MESSAGE));
			}
		}
		if (yml.contains(playerpath + ".BM1")) {
			String text = PLMToolbox.getBackMessage(yml, playerpath, difference);
			if (text != null) {
				messages.add(new MessageData(text, channels, SectionTypes.PLAYER, SectionSubTypes.BACK_MESSAGE));
			}
		}
		return messages;
	}

	protected static ArrayList<MessageData> getQuitMessages(YamlConfiguration yml, String playerpath) {
		ArrayList<MessageData> messages = new ArrayList<MessageData>();
		String[] channels = PLMToolbox.getChannels(playerpath, yml);
		if (yml.contains(playerpath + ".QM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(playerpath + ".QM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.PLAYER, SectionSubTypes.QUIT_MESSAGE));
			}
		}
		return messages;
	}
}
