package com.gmail.fantasticskythrow.messages;

import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMToolbox;
import com.gmail.fantasticskythrow.other.SectionSubTypes;
import com.gmail.fantasticskythrow.other.SectionTypes;

public final class DefaultSection {

	private static final String defaultpath = "Default";

	protected static boolean checkMessagesJoin(YamlConfiguration yml, long difference, AdvancedMessages am) {
		String message = PLMToolbox.getBackMessage(yml, defaultpath, difference);
		if (message != null) {
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(defaultpath, yml), SectionTypes.DEFAULT, SectionSubTypes.BACKMESSAGE));
			return true;
		}
		if (yml.contains(defaultpath + ".JM1")) {
			message = PLMToolbox.getMessage(defaultpath + ".JM", yml);
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(defaultpath, yml), SectionTypes.DEFAULT, SectionSubTypes.JOINMESSAGE));
			return true;
		}
		return false;
	}

	protected static boolean checkMessagesQuit(YamlConfiguration yml, AdvancedMessages am) {
		String message;
		if (yml.contains(defaultpath + ".QM1")) {
			message = PLMToolbox.getMessage(defaultpath + ".QM", yml);
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(defaultpath, yml), SectionTypes.GROUP, SectionSubTypes.QUITMESSAGE));
			return true;
		}
		return false;
	}

	protected static boolean checkFirstMessage(long lastLogin, YamlConfiguration yml, AdvancedMessages am) {
		if (lastLogin == 0L && yml.contains("Default.FM1")) {
			am.setMessage(new MessageData(PLMToolbox.getMessage("Default.FM", yml), PLMToolbox.getChannels(defaultpath, yml), SectionTypes.DEFAULT,
					SectionSubTypes.FIRSTMESSAGE));
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkWelcomeMessages(YamlConfiguration yml, AdvancedMessages am) {
		final String path = defaultpath + ".WM";
		if (yml.contains(path + "1")) {
			am.setWelcomeMessages(PLMToolbox.getAllMessages(path, yml));
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkPublicMessages(YamlConfiguration yml, AdvancedMessages am) {
		final String path = defaultpath + ".PM";
		if (yml.contains(path + "1")) {
			am.setPublicMessages(PLMToolbox.getAllMessages(path, yml));
			return true;
		} else {
			return false;
		}
	}

	protected static ArrayList<MessageData> getJoinMessages(YamlConfiguration yml, long difference, long lastLogin) {
		ArrayList<MessageData> messages = new ArrayList<MessageData>();
		String[] channels = PLMToolbox.getChannels(defaultpath, yml);
		if (lastLogin == 0L && yml.contains(defaultpath + ".FM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(defaultpath + ".FM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.DEFAULT, SectionSubTypes.FIRSTMESSAGE));
			}
		}
		if (yml.contains(defaultpath + ".JM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(defaultpath + ".JM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.DEFAULT, SectionSubTypes.JOINMESSAGE));
			}
		}
		if (yml.contains(defaultpath + ".BM1")) {
			String text = PLMToolbox.getBackMessage(yml, defaultpath, difference);
			if (text != null) {
				messages.add(new MessageData(text, channels, SectionTypes.DEFAULT, SectionSubTypes.BACKMESSAGE));
			}
		}
		return messages;
	}

	protected static ArrayList<MessageData> getQuitMessages(YamlConfiguration yml) {
		ArrayList<MessageData> messages = new ArrayList<MessageData>();
		String[] channels = PLMToolbox.getChannels(defaultpath, yml);
		if (yml.contains(defaultpath + ".QM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(defaultpath + ".QM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.DEFAULT, SectionSubTypes.QUITMESSAGE));
			}
		}
		return messages;
	}
}
