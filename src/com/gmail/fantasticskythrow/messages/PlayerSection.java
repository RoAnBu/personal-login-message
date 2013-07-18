package com.gmail.fantasticskythrow.messages;

import java.util.Random;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.SectionSubTypes;
import com.gmail.fantasticskythrow.other.SectionTypes;

public final class PlayerSection {

	private String playername, playerpath;
	private MessageData mData;
	private YamlConfiguration advancedMessagesYML;
	private long difference;

	protected boolean checkMessagesJoin(String playername, String playerpath, YamlConfiguration yml, long difference, AdvancedMessages am) {
		this.playername = playername;
		this.playerpath = playerpath;
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

	protected boolean checkMessagesQuit(String playername, String playerpath, YamlConfiguration yml, AdvancedMessages am) {
		this.playername = playername;
		this.playerpath = playerpath;
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

	/**
	 * Message when a player is back after a certain period
	 * @return
	 */
	private boolean backCase() {
		boolean status = false;
		if (advancedMessagesYML.contains(playerpath + ".BM1")) {
			int backMessageCount = 2;
			while (advancedMessagesYML.contains(playerpath + ".BM" + backMessageCount)) {
				backMessageCount++;
			}
			boolean a = false;
			int i = 1;
			while (i < backMessageCount && a == false && difference > 0) {
				String currentPath = playerpath + ".BM" + i + "T";
				long time = 0;
				if (advancedMessagesYML.contains(currentPath)) {
					try {
						time = Long.parseLong(advancedMessagesYML.getString(currentPath)) * 60000;
					} catch (NumberFormatException e) {
						System.out.println("[PLM] Number format at " + currentPath + " is invalid!!");
						time = 0L;
					}
					if (time > 0 && time <= difference) {
						String message = advancedMessagesYML.getString(playerpath + ".BM" + i);
						mData = new MessageData(message, getChannels(), SectionTypes.PLAYER, SectionSubTypes.BACKMESSAGE);
						a = true;
						status = true;
					} else if (time < 0 && (time * -1) >= difference) {
						String message = advancedMessagesYML.getString(playerpath + ".BM" + i);
						mData = new MessageData(message, getChannels(), SectionTypes.PLAYER, SectionSubTypes.BACKMESSAGE);
						a = true;
						status = true;
					}
				} else {
					//Quit while
					a = true;
					System.out.println("[PLM] Couldn't find the time path for back message " + i + " at " + playername + "'s personal section!");
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
			if (advancedMessagesYML.contains(playerpath + ".JM1")) {
				int count = 2;
				while (advancedMessagesYML.contains(playerpath + ".JM" + count)) {
					count++;
				}
				Random r = new Random();
				int n = r.nextInt(count - 1) + 1;
				String message = advancedMessagesYML.getString(playerpath + ".JM" + n);
				mData = new MessageData(message, getChannels(), SectionTypes.PLAYER, SectionSubTypes.JOINMESSAGE);
				return true;
			} else {
				return false;
			}
		}
		/*
		 * Quit Case
		 */
		else {
			if (advancedMessagesYML.contains(playerpath + ".QM1")) {
				int count = 2;
				while (advancedMessagesYML.contains(playerpath + ".QM" + count)) {
					count++;
				}
				Random r = new Random();
				int n = r.nextInt(count - 1) + 1;
				String message = advancedMessagesYML.getString(playerpath + ".QM" + n);
				mData = new MessageData(message, getChannels(), SectionTypes.PLAYER, SectionSubTypes.QUITMESSAGE);
				return true;
			} else {
				return false;
			}
		}
	}

	protected boolean checkFirstMessage(String playerpath, long lastLogin, YamlConfiguration yml, AdvancedMessages am) {
		if (lastLogin == 0L && yml.contains(playerpath + ".FM1")) {
			int count = 2;
			while (yml.contains(playerpath + ".FM" + count)) {
				count++;
			}
			Random r = new Random();
			int n = r.nextInt(count - 1) + 1;
			String message = yml.getString(playerpath + ".FM" + n);
			am.setMessage(new MessageData(message, getChannels(playerpath), SectionTypes.PLAYER, SectionSubTypes.FIRSTMESSAGE));
			return true;
		} else {
			return false;
		}
	}

	protected boolean checkWelcomeMessages(String playerpath, YamlConfiguration yml, AdvancedMessages am) {
		if (yml.contains(playerpath + ".WM1")) {
			int count = 2;
			while (yml.contains(playerpath + ".WM" + count)) {
				count++;
			}
			String[] messages = new String[count - 1];
			while (count > 1) {
				count--;
				messages[count - 1] = yml.getString(playerpath + ".WM" + count);
			}
			am.setWelcomeMessages(messages);
			return true;
		} else {
			return false;
		}
	}

	protected boolean checkPublicMessages(String playerpath, YamlConfiguration yml, AdvancedMessages am) {
		if (yml.contains(playerpath + ".PM1")) {
			int count = 2;
			while (yml.contains(playerpath + ".PM" + count)) {
				count++;
			}
			String[] publicMessages = new String[count - 1];
			while (count > 1) {
				count--;
				publicMessages[count - 1] = yml.getString(playerpath + ".PM" + count);
			}
			am.setPublicMessages(publicMessages);
			return true;
		} else {
			return false;
		}
	}

	private String[] getChannels() {
		if (advancedMessagesYML.contains(playerpath + ".CH")) {
			String[] channels = advancedMessagesYML.getString(playerpath + ".CH").split(", ");
			return channels;
		} else {
			return null;
		}
	}

	private String[] getChannels(String playerpath) {
		if (advancedMessagesYML.contains(playerpath + ".CH")) {
			String[] channels = advancedMessagesYML.getString(playerpath + ".CH").split(", ");
			return channels;
		} else {
			return null;
		}
	}
}
