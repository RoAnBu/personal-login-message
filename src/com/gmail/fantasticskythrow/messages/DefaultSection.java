package com.gmail.fantasticskythrow.messages;

import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMToolbox;
import com.gmail.fantasticskythrow.other.SectionSubTypes;
import com.gmail.fantasticskythrow.other.SectionTypes;

public final class DefaultSection {

	private MessageData mData;
	private YamlConfiguration advancedMessagesYML;
	private long difference;
	private static final String defaultpath = "Default";

	/*
	 * Open Section -> Provider Section
	 */
	protected boolean checkMessagesJoin(YamlConfiguration yml, long difference, AdvancedMessages am) {
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

	protected boolean checkMessagesQuit(YamlConfiguration yml, AdvancedMessages am) {
		this.advancedMessagesYML = yml;
		mData = null;
		if (isSuitableQuit()) {
			am.setMessage(mData);
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Provider Section -> Execution Section
	 */
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

	/*
	 * Execution Section
	 */
	/**
	 * Message when a player is back after a certain period
	 * @return
	 */
	private boolean backCase() {
		boolean status = false;
		if (advancedMessagesYML.contains(defaultpath + ".BM1")) {
			int backMessageCount = 2;
			while (advancedMessagesYML.contains(defaultpath + ".BM" + backMessageCount)) {
				backMessageCount++;
			}
			boolean a = false;
			int i = 1;
			while (i < backMessageCount && a == false && difference > 0) {
				String currentPath = defaultpath + ".BM" + i + "T";
				long time = 0;
				if (advancedMessagesYML.contains(currentPath)) {
					try {
						time = Long.parseLong(advancedMessagesYML.getString(currentPath)) * 60000;
					} catch (NumberFormatException e) {
						System.out.println("[PLM] Number format at " + currentPath + " is invalid!!");
						time = 0L;
					}
					if (time > 0 && time <= difference) {
						String message = advancedMessagesYML.getString(defaultpath + ".BM" + i);
						mData = new MessageData(message, PLMToolbox.getChannels(defaultpath, advancedMessagesYML), SectionTypes.DEFAULT,
								SectionSubTypes.BACKMESSAGE);
						a = true;
						status = true;
					} else if (time < 0 && (time * -1) >= difference) {
						String message = advancedMessagesYML.getString(defaultpath + ".BM" + i);
						mData = new MessageData(message, PLMToolbox.getChannels(defaultpath, advancedMessagesYML), SectionTypes.DEFAULT,
								SectionSubTypes.BACKMESSAGE);
						a = true;
						status = true;
					}
				} else {
					//Quit while
					a = true;
					System.out.println("[PLM] Couldn't find the time path for back message " + i + " at " + currentPath + "'s personal section!");
				}
				i++;
			}
			return status;
		} else {
			return false;
		}
	}

	private boolean standardJoinCase() {
		if (advancedMessagesYML.contains(defaultpath + ".JM1")) {
			mData = new MessageData(PLMToolbox.getMessage(defaultpath + ".JM", advancedMessagesYML), PLMToolbox.getChannels(defaultpath,
					advancedMessagesYML), SectionTypes.DEFAULT, SectionSubTypes.JOINMESSAGE);
			return true;
		} else {
			return false;
		}
	}

	private boolean standardQuitCase() {

		if (advancedMessagesYML.contains(defaultpath + ".QM1")) {
			mData = new MessageData(PLMToolbox.getMessage(defaultpath + ".QM", advancedMessagesYML), PLMToolbox.getChannels(defaultpath,
					advancedMessagesYML), SectionTypes.DEFAULT, SectionSubTypes.QUITMESSAGE);
			return true;
		} else {
			return false;
		}
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
			am.setWelcomeMessages(PLMToolbox.getAdvancedMessages(path, yml));
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkPublicMessages(YamlConfiguration yml, AdvancedMessages am) {
		final String path = defaultpath + ".PM";
		if (yml.contains(path + "1")) {
			am.setPublicMessages(PLMToolbox.getAdvancedMessages(path, yml));
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
