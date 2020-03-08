package com.gmail.fantasticskythrow.other;

public interface IVanishManager
{
	boolean isVanished(String name);

	void addJoinedPlayer(String name);

	boolean isJustJoinedPlayer(String name);

	void removeJoinedPlayer(String name);

	boolean isPluginInstalled();
}
