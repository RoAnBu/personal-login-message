package com.gmail.fantasticskythrow.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.other.PLMToolbox;

public class PLMRestoreCommand {

	protected static void onCommand(PLM plugin, CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 1 && args[0].equalsIgnoreCase("restore")) {
				//Correct syntax PLAYER
				if (PLMToolbox.overwriteMessagesFile(plugin)) { //Success
					sender.sendMessage(ChatColor.GREEN + "Messages.txt was replaced by default!");
					System.out.println("[PLM] Messages.txt was replaced by " + sender.getName());
				} else {//Error
					sender.sendMessage(ChatColor.RED + "An error has occurred!");
					sender.sendMessage("Editing 'messages.txt' was not possible! Check the plugin's folder");
				}
			} else {
				//Wrong syntax PLAYER
				sender.sendMessage(ChatColor.RED + "Too many arguments. Usage: /plm restore");
			}
		} else {
			if (args.length == 1 && args[0].equalsIgnoreCase("restore")) {
				//Correct syntax CONSOLE
				if (PLMToolbox.overwriteMessagesFile(plugin)) {//Success
					System.out.println("Successfully replaced 'messages.txt' by default");
				} else {//Error
					System.out.println("[PLM] An error has occurred");
				}
			} else {
				//Wrong syntax CONSOLE
				System.out.println("[PLM] Too many arguments. Usage: /plm restore");
			}
		}
	}
}
