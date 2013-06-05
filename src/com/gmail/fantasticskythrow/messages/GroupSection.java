package com.gmail.fantasticskythrow.messages;

import java.util.Random;

import org.bukkit.configuration.file.YamlConfiguration;

public final class GroupSection {

    private String grouppath, message;
    private YamlConfiguration advancedMessagesYML;
    private long difference;
    
    protected boolean checkMessagesJoin(String grouppath, YamlConfiguration yml, long difference, AdvancedMessages am) {
	this.grouppath = grouppath;
	this.advancedMessagesYML = yml;
	this.difference = difference;
	this.message = "";
	if (isSuitableJoin()) {
	    am.setMessage(message);
	    return true;
	}
	else {
	    return false;
	}
    }
    
    protected boolean checkMessagesQuit(String grouppath, YamlConfiguration yml, AdvancedMessages am) {
	this.grouppath = grouppath;
	this.advancedMessagesYML = yml;
	this.message = "";
	if (isSuitableQuit()) {
	    am.setMessage(message);
	    return true;
	}
	else {
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
			message = advancedMessagesYML.getString(grouppath + ".BM" + i);
			a = true;
			status = true;
		    }
		    else if (time < 0 && (time * -1) >= difference) {
			message = advancedMessagesYML.getString(grouppath + ".BM" + i);
			a = true;
			status = true;
		    }
		}
		else {
		    //Quit while
		    a = true;
		    System.out.println("[PLM] Couldn't find the time path for back message " + i + " at " + currentPath + " ! (Group section)");
		}
		i++;
	    }
	    return status;
	}
	else {
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
	    if (advancedMessagesYML.contains(grouppath + ".JM1")) {
		int count = 2;
		while (advancedMessagesYML.contains(grouppath + ".JM"+ count)) {
		    count++;
		}
		Random r = new Random();
		int n = r.nextInt(count - 1) + 1;
		message = advancedMessagesYML.getString(grouppath + ".JM" + n);
		return true;
	    }
	    else {
		return false;
	    }
	}
	/*
	 * Quit Case
	 */
	else {
	    if (advancedMessagesYML.contains(grouppath + ".QM1")) {
		int count = 2;
		while (advancedMessagesYML.contains(grouppath + ".QM"+ count)) {
		    count++;
		}
		Random r = new Random();
		int n = r.nextInt(count - 1) + 1;
		message = advancedMessagesYML.getString(grouppath + ".QM" + n);
		return true;
	    }
	    else {
		return false;
	    }
	}
    }
    
    protected boolean checkWelcomeMessages(String grouppath, YamlConfiguration yml, AdvancedMessages am) {
	if (yml.contains(grouppath + ".WM1")) {
	    int count = 2;
	    while (yml.contains(grouppath + ".WM"+ count)) {
		count++;
	    }
	    String[] messages = new String[count - 1];
	    while (count > 1 ) {
		count--;
		messages [count-1] = yml.getString(grouppath + ".WM" + count);
	    }
	    am.setWelcomeMessages(messages);
	    return true;
	} else {
	    return false;
	}
    }
    
    protected boolean checkPublicMessages(String grouppath, YamlConfiguration yml, AdvancedMessages am) {
	if (yml.contains(grouppath + ".PM1")) {
	    int count = 2;
	    while (yml.contains(grouppath + ".PM"+ count)) {
		count++;
	    }
	    String[] publicMessages = new String[count - 1];
	    while (count > 1 ) {
		count--;
		publicMessages [count-1] = yml.getString(grouppath + ".PM" + count);
	    }
	    am.setPublicMessages(publicMessages);
	    return true;
	} else {
	    return false;
	}
    }
    
    protected boolean checkFirstMessage(String grouppath, long lastLogin, YamlConfiguration yml, AdvancedMessages am) {
	    if (lastLogin == 0L && yml.contains(grouppath + ".FM1")) {
		int count = 2;
		while (yml.contains(grouppath + ".FM"+ count)) {
		    count++;
		}
		Random r = new Random();
		int n = r.nextInt(count - 1) + 1;
		am.setMessage( yml.getString( grouppath + ".FM" + n));
		return true;
	    }
	    else {
		return false;
	    }
    }
}
