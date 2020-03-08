package com.gmail.fantasticskythrow.other;

import com.gmail.fantasticskythrow.PLM;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

import java.util.Collection;
import java.util.HashMap;

public class VanishNoPacketManager implements IVanishManager
{

	private PLM plugin;
	private final HashMap<String, Boolean> players;

	public VanishNoPacketManager(PLM plugin) {
		Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
		this.plugin = plugin;
		Plugin pl = plugin.getServer().getPluginManager().getPlugin("VanishNoPacket");
		if (pl != null) {
			players = new HashMap<String, Boolean>();
			for (Player p : onlinePlayers) {
				if (p != null) {
					players.put(p.getName(), false);
				}
			}
		} else {
			players = null;
		}
	}

	/**
	 * Checks whether the player is vanished or not (VanishNoPacket). It only connects to the VanishNoPacketPlugin and asks for the status.
	 * If VNP is not available it'll return false
	 * @param name the player's name, not only the lowercase name!
	 * @return true if vanished, false anything else
	 */
	@Override public boolean isVanished(String name) {
		Plugin pl = plugin.getServer().getPluginManager().getPlugin("VanishNoPacket");
		if (pl != null) {
			try {
				return ((VanishPlugin) pl).getManager().isVanished(name);
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override public void addJoinedPlayer(final String name) {
		Plugin pl = plugin.getServer().getPluginManager().getPlugin("VanishNoPacket");
		if (pl != null) {
			players.put(name, true);
		}
	}

	/**
	 * Checks whether the player just joined or is online after the first event
	 * @param name - the player's name
	 * @return true if he already passed the first VanishStatusChangeEvent
	 */
	@Override public boolean isJustJoinedPlayer(final String name) {
		if (players != null) {
			if (players.containsKey(name)) {
				if (players.get(name)) {
					players.put(name, false);
					return false;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	@Override public void removeJoinedPlayer(final String name) {
		if (players != null) {
			if (players.containsKey(name)) {
				players.remove(name);
			}
		}
	}

	@Override public boolean isPluginInstalled() {
		Plugin pl = plugin.getServer().getPluginManager().getPlugin("VanishNoPacket");
		return pl != null;
	}
}
