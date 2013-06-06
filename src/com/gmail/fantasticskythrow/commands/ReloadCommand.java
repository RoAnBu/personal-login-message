package com.gmail.fantasticskythrow.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.PLM;

public class ReloadCommand implements CommandExecutor {

	private PLM plugin;

	public ReloadCommand(PLM p) {
		this.plugin = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("plm.admin") || sender.hasPermission("plm.*")) {
				if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
					//Rechte ja, Syntax ja SPIELER
					try {
						plugin.reloadMessages();
						sender.sendMessage(ChatColor.GREEN + "Advanced Messages Mode was reloaded!");
						System.out.println("[PLM] Advanced Messages Mode reloaded by " + sender.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				} else {
					//Rechte ja, Syntax nein SPIELER
					sender.sendMessage(ChatColor.RED + "/plm reload");
					return false;
				}
			} else {
				//Keine Rechte SPIELER
				sender.sendMessage(ChatColor.RED + "You are not permitted to modify PLM!");
				return true;
			}
		} else {
			if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
				//Syntax ja KONSOLE
				try {
					plugin.reloadMessages();
					System.out.println("[PLM] Advanced Messages Mode reloaded");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			} else {
				//Syntax nein KONSOLE
				System.out.println("/plm reload");
				return true;
			}
		}
	}
}
