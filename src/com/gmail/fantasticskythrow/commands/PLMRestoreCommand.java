package com.gmail.fantasticskythrow.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.messages.StandardMessages;

public class PLMRestoreCommand {

	protected static void onCommand(PLM plugin, CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 1 && args[0].equalsIgnoreCase("restore")) {
				//Correct syntax PLAYER
				StandardMessages.overwriteMessagesFile(plugin);
				sender.sendMessage(ChatColor.GREEN + "The config was replaced by default!");
				System.out.println("[PLM] The config was replaced by " + sender.getName());
			} else {
				//Wrong syntax PLAYER
				sender.sendMessage(ChatColor.RED + "Too many arguments. Usage: /plm restore");
			}
		} else {
			if (args.length == 1 && args[0].equalsIgnoreCase("restore")) {
				//Correct syntax CONSOLE
				StandardMessages.overwriteMessagesFile(plugin);
				System.out.println("[PLM] Messages.txt was replaced by the default file");
			} else {
				//Wrong syntax CONSOLE
				System.out.println("[PLM] Too many arguments. Usage: /plm restore");
			}
		}
	}
}
