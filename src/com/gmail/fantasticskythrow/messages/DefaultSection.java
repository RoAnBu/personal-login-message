package com.gmail.fantasticskythrow.messages;

import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMToolbox;
import com.gmail.fantasticskythrow.other.SectionSubTypes;
import com.gmail.fantasticskythrow.other.SectionTypes;

public final class DefaultSection {

	private static final String DEFAULT_PATH = "Default";

	protected static boolean checkMessagesJoin(YamlConfiguration yml, long difference, AdvancedMessages am) {
		String message = PLMToolbox.getBackMessage(yml, DEFAULT_PATH, difference);
		if (message != null) {
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(DEFAULT_PATH, yml), SectionTypes.DEFAULT, SectionSubTypes.BACK_MESSAGE));
			return true;
		}
		if (yml.contains(DEFAULT_PATH + ".JM1")) {
			message = PLMToolbox.getMessage(DEFAULT_PATH + ".JM", yml);
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(DEFAULT_PATH, yml), SectionTypes.DEFAULT, SectionSubTypes.JOIN_MESSAGE));
			return true;
		}
		return false;
	}

	protected static boolean checkMessagesQuit(YamlConfiguration yml, AdvancedMessages am) {
		String message;
		if (yml.contains(DEFAULT_PATH + ".QM1")) {
			message = PLMToolbox.getMessage(DEFAULT_PATH + ".QM", yml);
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(DEFAULT_PATH, yml), SectionTypes.DEFAULT, SectionSubTypes.QUIT_MESSAGE));
			return true;
		}
		return false;
	}

	protected static boolean checkFirstMessage(long lastLogin, YamlConfiguration yml, AdvancedMessages am) {
		if (lastLogin == 0L && yml.contains("Default.FM1")) {
			am.setMessage(new MessageData(PLMToolbox.getMessage("Default.FM", yml), PLMToolbox.getChannels(DEFAULT_PATH, yml), SectionTypes.DEFAULT,
			                              SectionSubTypes.FIRST_MESSAGE));
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkWelcomeMessages(YamlConfiguration yml, AdvancedMessages am) {
		final String path = DEFAULT_PATH + ".WM";
		if (yml.contains(path + "1")) {
			am.setWelcomeMessages(PLMToolbox.getAllMessages(path, yml));
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkPublicMessages(YamlConfiguration yml, AdvancedMessages am) {
		final String path = DEFAULT_PATH + ".PM";
		if (yml.contains(path + "1")) {
			am.setPublicMessages(PLMToolbox.getAllMessages(path, yml));
			return true;
		} else {
			return false;
		}
	}

	protected static ArrayList<MessageData> getJoinMessages(YamlConfiguration yml, long difference, long lastLogin) {
		ArrayList<MessageData> messages = new ArrayList<MessageData>();
		String[] channels = PLMToolbox.getChannels(DEFAULT_PATH, yml);
		if (lastLogin == 0L && yml.contains(DEFAULT_PATH + ".FM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(DEFAULT_PATH + ".FM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.DEFAULT, SectionSubTypes.FIRST_MESSAGE));
			}
		}
		if (yml.contains(DEFAULT_PATH + ".JM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(DEFAULT_PATH + ".JM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.DEFAULT, SectionSubTypes.JOIN_MESSAGE));
			}
		}
		if (yml.contains(DEFAULT_PATH + ".BM1")) {
			String text = PLMToolbox.getBackMessage(yml, DEFAULT_PATH, difference);
			if (text != null) {
				messages.add(new MessageData(text, channels, SectionTypes.DEFAULT, SectionSubTypes.BACK_MESSAGE));
			}
		}
		return messages;
	}

	protected static ArrayList<MessageData> getQuitMessages(YamlConfiguration yml) {
		ArrayList<MessageData> messages = new ArrayList<MessageData>();
		String[] channels = PLMToolbox.getChannels(DEFAULT_PATH, yml);
		if (yml.contains(DEFAULT_PATH + ".QM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(DEFAULT_PATH + ".QM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.DEFAULT, SectionSubTypes.QUIT_MESSAGE));
			}
		}
		return messages;
	}
}
