package com.gmail.fantasticskythrow.other;

import org.bukkit.entity.Player;

public interface IVanishManager
{
	boolean isVanished(String name);

	boolean isVanished(Player player);

	boolean isPluginInstalled();
}
