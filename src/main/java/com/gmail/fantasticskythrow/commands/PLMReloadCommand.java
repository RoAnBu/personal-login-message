package com.gmail.fantasticskythrow.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.PLM;

public class PLMReloadCommand {

	private static final Logger logger = LogManager.getLogger(PLMCommandHandler.class);

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
