package com.gmail.fantasticskythrow.messages;

import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.fantasticskythrow.other.MessageData;
import com.gmail.fantasticskythrow.other.PLMToolbox;
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
						mData = new MessageData(message, PLMToolbox.getChannels(playerpath, advancedMessagesYML), SectionTypes.PLAYER,
								SectionSubTypes.BACKMESSAGE);
						a = true;
						status = true;
					} else if (time < 0 && (time * -1) >= difference) {
						String message = advancedMessagesYML.getString(playerpath + ".BM" + i);
						mData = new MessageData(message, PLMToolbox.getChannels(playerpath, advancedMessagesYML), SectionTypes.PLAYER,
								SectionSubTypes.BACKMESSAGE);
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

	private boolean standardJoinCase() {
		if (advancedMessagesYML.contains(playerpath + ".JM1")) {
			mData = new MessageData(PLMToolbox.getMessage(playerpath + ".JM", advancedMessagesYML), PLMToolbox.getChannels(playerpath,
					advancedMessagesYML), SectionTypes.PLAYER, SectionSubTypes.JOINMESSAGE);
			return true;
		} else {
			return false;
		}
	}

	private boolean standardQuitCase() {
		if (advancedMessagesYML.contains(playerpath + ".QM1")) {
			mData = new MessageData(PLMToolbox.getMessage(playerpath + ".QM", advancedMessagesYML), PLMToolbox.getChannels(playerpath,
					advancedMessagesYML), SectionTypes.PLAYER, SectionSubTypes.QUITMESSAGE);
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkFirstMessage(String playerpath, long lastLogin, YamlConfiguration yml, AdvancedMessages am) {
		if (lastLogin == 0L && yml.contains(playerpath + ".FM1")) {
			am.setMessage(new MessageData(PLMToolbox.getMessage(playerpath + ".FM", yml), PLMToolbox.getChannels(playerpath, yml),
					SectionTypes.PLAYER, SectionSubTypes.FIRSTMESSAGE));
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkWelcomeMessages(String playerpath, YamlConfiguration yml, AdvancedMessages am) {
		final String path = playerpath + ".WM";
		if (yml.contains(path + "1")) {
			am.setWelcomeMessages(PLMToolbox.getAdvancedMessages(path, yml));
			return true;
		} else {
			return false;
		}
	}

	protected static boolean checkPublicMessages(String playerpath, YamlConfiguration yml, AdvancedMessages am) {
		final String path = playerpath + ".PM";
		if (yml.contains(path + "1")) {
			am.setPublicMessages(PLMToolbox.getAdvancedMessages(path, yml));
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
				messages.add(new MessageData(text, channels, SectionTypes.PLAYER, SectionSubTypes.FIRSTMESSAGE));
			}
		}
		if (yml.contains(playerpath + ".JM1")) {
			ArrayList<String> al = PLMToolbox.getAllMessages(playerpath + ".JM", yml);
			for (String text : al) {
				messages.add(new MessageData(text, channels, SectionTypes.PLAYER, SectionSubTypes.JOINMESSAGE));
			}
		}
		if (yml.contains(playerpath + ".BM1")) {
			String text = PLMToolbox.getBackMessage(yml, playerpath, difference);
			if (text != null) {
				messages.add(new MessageData(text, channels, SectionTypes.PLAYER, SectionSubTypes.BACKMESSAGE));
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
				messages.add(new MessageData(text, channels, SectionTypes.PLAYER, SectionSubTypes.QUITMESSAGE));
			}
		}
		return messages;
	}
}
