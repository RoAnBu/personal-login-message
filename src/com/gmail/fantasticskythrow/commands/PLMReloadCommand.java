package com.gmail.fantasticskythrow.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.PLM;

public class PLMReloadCommand {

	public static void onCommand(CommandSender sender, String[] args, PLM plugin) {
		if (sender instanceof Player) {
			if (args.length == 1) {
				//Correct syntax PLAYER
				try {
					plugin.reloadMessages();
					sender.sendMessage(ChatColor.BLUE + "[PLM] " + ChatColor.GREEN + "Messages have been reloaded!");
					System.out.println("[PLM] Messages reloaded by " + sender.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//Wrong syntax PLAYER
				sender.sendMessage(ChatColor.RED + "Too many arguments. Usage: /plm reload");
			}
		} else {
			if (args.length == 1) {
				//Correct syntax CONSOLE
				try {
					plugin.reloadMessages();
					System.out.println("[PLM] Messages reloaded");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//Wrong syntax CONSOLE
				System.out.println("Too many arguments. Usage: plm reload");
			}
		}
	}
}
