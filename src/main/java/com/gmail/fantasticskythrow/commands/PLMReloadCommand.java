package com.gmail.fantasticskythrow.commands;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.other.logging.ILoggerWrapper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PLMReloadCommand {

	private static final ILoggerWrapper logger = PLM.logger();

	public static void onCommand(CommandSender sender, String[] args, PLM plugin) {
		if (sender instanceof Player) {
			if (args.length == 1) {
				//Correct syntax PLAYER
				try {
					plugin.reloadMessages();
					sender.sendMessage(ChatColor.BLUE + "[PLM] " + ChatColor.GREEN + "Messages have been reloaded!");
					logger.info("Messages reloaded by " + sender.getName());
				} catch (Exception e) {
					logger.error(e);
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
					System.out.println("Messages reloaded");
					logger.info("Messages reloaded");
				} catch (Exception e) {
					logger.error(e);
				}
			} else {
				//Wrong syntax CONSOLE
				System.out.println("Too many arguments. Usage: plm reload");
				logger.info("Too many arguments. Usage: plm reload");
			}
		}
	}
}
