package com.gmail.fantasticskythrow.messages;

import org.bukkit.entity.Player;

public interface IPLMFile
{

	void setPlayerQuitTimeToCurrentTime(Player player);

	long getLastLoginTimeMs(Player player);

	long getTimeSinceLastLoginMs(Player player);

	int getPlayerLogins(Player player);

	long getTotalLogins();

	int getUniquePlayerLogins();

	void addPlayerLogin(Player player);

	boolean isPluginFirstEnabled();

	void setFirstEnabled(boolean b);

	String getAlternateNameForCountry(String englishName);

	void save();
}
