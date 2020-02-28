package com.gmail.fantasticskythrow.messages;

import org.bukkit.entity.Player;

public interface PLMFile {

	public void setPlayerQuitTime(Player player);

	public long getLastLogin(Player player);

	public long getDifference(Player player);

	public int getPlayerLogins(Player player);

	public long getTotalLogins();

	public int getUniquePlayerLogins();

	public void setPlayerLogin(Player player);

	public boolean getFirstEnabled();

	public void setFirstEnabled(boolean b);

	public String getCountryName(String englishName);

	public boolean getErrorStatus();

	public void run();
}
