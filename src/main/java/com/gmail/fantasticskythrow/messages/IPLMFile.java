package com.gmail.fantasticskythrow.messages;

import org.bukkit.entity.Player;

public interface IPLMFile
{

	void setPlayerQuitTime(Player player);

	long getLastLogin(Player player);

	long getDifference(Player player);

	int getPlayerLogins(Player player);

	long getTotalLogins();

	int getUniquePlayerLogins();

	void setPlayerLogin(Player player);

	boolean getFirstEnabled();

	void setFirstEnabled(boolean b);

	String getCountryName(String englishName);

	boolean getErrorStatus();

	void run();
}
