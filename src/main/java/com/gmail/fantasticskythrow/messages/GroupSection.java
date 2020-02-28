package com.gmail.fantasticskythrow.messages;

import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMToolbox;
import com.gmail.fantasticskythrow.other.SectionSubTypes;
import com.gmail.fantasticskythrow.other.SectionTypes;

public final class GroupSection {

	protected static boolean checkMessagesJoin(String grouppath, YamlConfiguration yml, long difference, AdvancedMessages am) {
		String message = PLMToolbox.getBackMessage(yml, grouppath, difference);
		if (message != null) {
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(grouppath, yml), SectionTypes.GROUP, SectionSubTypes.BACK_MESSAGE));
			return true;
		}
		if (yml.contains(grouppath + ".JM1")) {
			message = PLMToolbox.getMessage(grouppath + ".JM", yml);
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(grouppath, yml), SectionTypes.GROUP, SectionSubTypes.JOIN_MESSAGE));
			return true;
		}
		return false;
	}

	protected static boolean checkMessagesQuit(String grouppath, YamlConfiguration yml, AdvancedMessages am) {
		String message;
		if (yml.contains(grouppath + ".QM1")) {
			message = PLMToolbox.getMessage(grouppath + ".QM", yml);
			am.setMessage(new MessageData(message, PLMToolbox.getChannels(grouppath, yml), SectionTypes.GROUP, SectionSubTypes.QUIT_MESSAGE));
			return true;
		}
		return false;
	}

	protected static boolean checkFirstMessage(String grouppath, long lastLogin, YamlConfiguration yml, AdvancedMessages am) {
		if (lastLogin == 0L && yml.contains(grouppath + ".FM1")) {
			am.setMessage(new MessageData(PLMToolbox.getMessage(grouppath + ".FM", yml), PLMToolbox.getChannels(grouppath, yml), SectionTypes.GROUP,
					SectionSubTypes.FIRST_MESSAGE));
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkWelcomeMessages(String grouppath, YamlConfiguration yml, AdvancedMessages am) {
		final String path = grouppath + ".WM";
		if (yml.contains(path + "1")) {
			am.setWelcomeMessages(PLMToolbox.getAllMessages(path, yml));
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkPublicMessages(String grouppath, YamlConfiguration yml, AdvancedMessages am) {
		final String path = grouppath + ".PM";
		if (yml.contains(path + "1")) {
			am.setPublicMessages(PLMToolbox.getAllMessages(path, yml));
			return true;
		} else {
			return false;
		}
	}

	protected static ArrayList<MessageData> getJoinMessages(YamlConfiguration yml, long difference, long lastLogin, final String grouppath) {
		ArrayList<MessageData> messages = new ArrayList<MessageData>();
		String[] channels = PLMToolbox.getChannels(grouppath, yml);
		if (lastLogin == 0L && yml.contains(grouppath + ".FM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(grouppath + ".FM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.GROUP, SectionSubTypes.FIRST_MESSAGE));
			}
		}
		if (yml.contains(grouppath + ".JM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(grouppath + ".JM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.GROUP, SectionSubTypes.JOIN_MESSAGE));
			}
		}
		if (yml.contains(grouppath + ".BM1")) {
			String text = PLMToolbox.getBackMessage(yml, grouppath, difference);
			if (text != null) {
				messages.add(new MessageData(text, channels, SectionTypes.GROUP, SectionSubTypes.BACK_MESSAGE));
			}
		}
		return messages;
	}

	protected static ArrayList<MessageData> getQuitMessages(YamlConfiguration yml, String grouppath) {
		ArrayList<MessageData> messages = new ArrayList<MessageData>();
		String[] channels = PLMToolbox.getChannels(grouppath, yml);
		if (yml.contains(grouppath + ".QM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(grouppath + ".QM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.GROUP, SectionSubTypes.QUIT_MESSAGE));
			}
		}
		return messages;
	}
}
