package com.gmail.fantasticskythrow.messages;

import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMToolbox;
import com.gmail.fantasticskythrow.other.SectionSubTypes;
import com.gmail.fantasticskythrow.other.SectionTypes;

public final class GroupSection {

	private String grouppath;
	private MessageData mData;
	private YamlConfiguration advancedMessagesYML;
	private long difference;

	protected boolean checkMessagesJoin(String grouppath, YamlConfiguration yml, long difference, AdvancedMessages am) {
		this.grouppath = grouppath;
		this.advancedMessagesYML = yml;
		this.difference = difference;
		mData = null;
		if (isSuitableJoin()) {
			am.setMessage(mData);
			return true;
		} else {
			return false;
		}
	}

	protected boolean checkMessagesQuit(String grouppath, YamlConfiguration yml, AdvancedMessages am) {
		this.grouppath = grouppath;
		this.advancedMessagesYML = yml;
		mData = null;
		if (isSuitableQuit()) {
			am.setMessage(mData);
			return true;
		} else {
			return false;
		}
	}

	private boolean isSuitableJoin() {
		if (backCase())
			return true;
		else if (standardJoinCase())
			return true;
		else
			return false;
	}

	private boolean isSuitableQuit() {
		if (standardQuitCase())
			return true;
		else
			return false;
	}

	/**
	 * Message when a player is back after a certain period
	 * @return
	 */
	private boolean backCase() {
		boolean status = false;
		if (advancedMessagesYML.contains(grouppath + ".BM1")) {
			int backMessageCount = 2;
			while (advancedMessagesYML.contains(grouppath + ".BM" + backMessageCount)) {
				backMessageCount++;
			}
			boolean a = false;
			int i = 1;
			while (i < backMessageCount && a == false && difference > 0) {
				String currentPath = grouppath + ".BM" + i + "T";
				long time = 0;
				if (advancedMessagesYML.contains(currentPath)) {
					try {
						time = Long.parseLong(advancedMessagesYML.getString(currentPath)) * 60000;
					} catch (NumberFormatException e) {
						System.out.println("[PLM] Number format at " + currentPath + " is invalid!!");
						time = 0L;
					}
					if (time > 0 && time <= difference) {
						String message = advancedMessagesYML.getString(grouppath + ".BM" + i);
						mData = new MessageData(message, PLMToolbox.getChannels(grouppath, advancedMessagesYML), SectionTypes.GROUP,
								SectionSubTypes.BACKMESSAGE);
						a = true;
						status = true;
					} else if (time < 0 && (time * -1) >= difference) {
						String message = advancedMessagesYML.getString(grouppath + ".BM" + i);
						mData = new MessageData(message, PLMToolbox.getChannels(grouppath, advancedMessagesYML), SectionTypes.GROUP,
								SectionSubTypes.BACKMESSAGE);
						a = true;
						status = true;
					}
				} else {
					//Quit while
					a = true;
					System.out.println("[PLM] Couldn't find the time path for back message " + i + " at " + currentPath + " ! (Group section)");
				}
				i++;
			}
			return status;
		} else {
			return false;
		}
	}

	private boolean standardJoinCase() {
		if (advancedMessagesYML.contains(grouppath + ".JM1")) {
			mData = new MessageData(PLMToolbox.getMessage(grouppath + ".JM", advancedMessagesYML), PLMToolbox.getChannels(grouppath,
					advancedMessagesYML), SectionTypes.GROUP, SectionSubTypes.JOINMESSAGE);
			return true;
		} else {
			return false;
		}
	}

	private boolean standardQuitCase() {
		if (advancedMessagesYML.contains(grouppath + ".QM1")) {
			mData = new MessageData(PLMToolbox.getMessage(grouppath + ".QM", advancedMessagesYML), PLMToolbox.getChannels(grouppath,
					advancedMessagesYML), SectionTypes.GROUP, SectionSubTypes.QUITMESSAGE);
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkFirstMessage(String grouppath, long lastLogin, YamlConfiguration yml, AdvancedMessages am) {
		if (lastLogin == 0L && yml.contains(grouppath + ".FM1")) {
			am.setMessage(new MessageData(PLMToolbox.getMessage(grouppath + ".FM", yml), PLMToolbox.getChannels(grouppath, yml), SectionTypes.GROUP,
					SectionSubTypes.FIRSTMESSAGE));
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
				messages.add(new MessageData(text, channels, SectionTypes.GROUP, SectionSubTypes.FIRSTMESSAGE));
			}
		}
		if (yml.contains(grouppath + ".JM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(grouppath + ".JM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.GROUP, SectionSubTypes.JOINMESSAGE));
			}
		}
		if (yml.contains(grouppath + ".BM1")) {
			String text = PLMToolbox.getBackMessage(yml, grouppath, difference);
			if (text != null) {
				messages.add(new MessageData(text, channels, SectionTypes.GROUP, SectionSubTypes.BACKMESSAGE));
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
				messages.add(new MessageData(text, channels, SectionTypes.GROUP, SectionSubTypes.QUITMESSAGE));
			}
		}
		return messages;
	}
}
