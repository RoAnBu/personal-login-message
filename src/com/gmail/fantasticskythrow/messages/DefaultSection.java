package com.gmail.fantasticskythrow.messages;

import java.util.Random;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.SectionSubTypes;
import com.gmail.fantasticskythrow.other.SectionTypes;

public final class DefaultSection {

	private MessageData mData;
	private YamlConfiguration advancedMessagesYML;
	private long difference;
	private final String defaultpath = "Default";

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
		else if (standardCase(true))
			return true;
		else
			return false;
	}

	private boolean isSuitableQuit() {
		if (standardCase(false))
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
						mData = new MessageData(message, getChannels(), SectionTypes.DEFAULT, SectionSubTypes.BACKMESSAGE);
						a = true;
						status = true;
					} else if (time < 0 && (time * -1) >= difference) {
						String message = advancedMessagesYML.getString(defaultpath + ".BM" + i);
						mData = new MessageData(message, getChannels(), SectionTypes.DEFAULT, SectionSubTypes.BACKMESSAGE);
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

	/**
	 * The normal case
	 * @param join
	 * @return
	 */
	private boolean standardCase(boolean join) {
		/*
		 * Join Case
		 */
		if (join) {
			if (advancedMessagesYML.contains(defaultpath + ".JM1")) {
				int count = 2;
				while (advancedMessagesYML.contains(defaultpath + ".JM" + count)) {
					count++;
				}
				Random r = new Random();
				int n = r.nextInt(count - 1) + 1;
				String message = advancedMessagesYML.getString(defaultpath + ".JM" + n);
				mData = new MessageData(message, getChannels(), SectionTypes.DEFAULT, SectionSubTypes.JOINMESSAGE);
				return true;
			} else {
				return false;
			}
		}
		/*
		 * Quit Case
		 */
		else {
			if (advancedMessagesYML.contains(defaultpath + ".QM1")) {
				int count = 2;
				while (advancedMessagesYML.contains(defaultpath + ".QM" + count)) {
					count++;
				}
				Random r = new Random();
				int n = r.nextInt(count - 1) + 1;
				String message = advancedMessagesYML.getString(defaultpath + ".QM" + n);
				mData = new MessageData(message, getChannels(), SectionTypes.DEFAULT, SectionSubTypes.QUITMESSAGE);
				return true;
			} else {
				return false;
			}
		}
	}

	/*
	 * First Message Section
	 */

	protected boolean checkFirstMessage(long lastLogin, YamlConfiguration yml, AdvancedMessages am) {
		if (lastLogin == 0L && yml.contains("Default.FM1")) {
			int count = 2;
			while (yml.contains("Default.FM" + count)) {
				count++;
			}
			Random r = new Random();
			int n = r.nextInt(count - 1) + 1;
			String message = yml.getString("Default.FM" + n);
			am.setMessage(new MessageData(message, getChannels(), SectionTypes.DEFAULT, SectionSubTypes.FIRSTMESSAGE));
			return true;
		} else {
			return false;
		}
	}

	protected boolean checkWelcomeMessages(YamlConfiguration yml, AdvancedMessages am) {
		if (yml.contains(defaultpath + ".WM1")) {
			int count = 2;
			while (yml.contains(defaultpath + ".WM" + count)) {
				count++;
			}
			String[] messages = new String[count - 1];
			while (count > 1) {
				count--;
				messages[count - 1] = yml.getString(defaultpath + ".WM" + count);
			}
			am.setWelcomeMessages(messages);
			return true;
		} else {
			return false;
		}
	}

	protected boolean checkPublicMessages(YamlConfiguration yml, AdvancedMessages am) {
		if (yml.contains(defaultpath + ".PM1")) {
			int count = 2;
			while (yml.contains(defaultpath + ".PM" + count)) {
				count++;
			}
			String[] publicMessages = new String[count - 1];
			while (count > 1) {
				count--;
				publicMessages[count - 1] = yml.getString(defaultpath + ".PM" + count);
			}
			am.setPublicMessages(publicMessages);
			return true;
		} else {
			return false;
		}
	}

	private String[] getChannels() {
		if (advancedMessagesYML.contains(defaultpath + ".CH")) {
			String[] channels = advancedMessagesYML.getString(defaultpath + ".CH").split(", ");
			return channels;
		} else {
			return null;
		}
	}
}
