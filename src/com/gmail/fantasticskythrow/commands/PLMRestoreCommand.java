package com.gmail.fantasticskythrow.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.PLM;

public class PLMRestoreCommand implements CommandExecutor {

	private PLM plugin;

	public PLMRestoreCommand(PLM p) {
		this.plugin = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("plm.admin") || sender.hasPermission("plm.*")) {
				if (args.length == 1 && args[0].equalsIgnoreCase("restore")) {
					//Rechte ja, Syntax ja SPIELER
					try {
						this.overwriteMessagesFile();
						sender.sendMessage(ChatColor.GREEN + "The config was replaced by default!");
						System.out.println("[PLM] The config was replaced by " + sender.getName());
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "An error ocurred while replacing the messages.txt file!");
						System.out.println("[PLM] An error ocurred while replacing the messages.txt file!");
					}
					return true;
				} else {
					//Rechte ja, Syntax nein SPIELER
					sender.sendMessage(ChatColor.RED + "Usage: /plm restore");
					return true;
				}
			} else {
				//Keine Rechte SPIELER
				sender.sendMessage(ChatColor.RED + "You are not permitted to modify PLM!");
				return true;
			}
		} else {
			if (args.length == 1 && args[0].equalsIgnoreCase("restore")) {
				//Syntax ja KONSOLE
				try {
					this.overwriteMessagesFile();
					System.out.println("[PLM] Messages.txt was replaced by the default file");
				} catch (Exception e) {
					System.out.println("[PLM] An error occurred while replacing the messages.txt file!");
				}
				return true;
			} else {
				//Syntax nein KONSOLE
				System.out.println("[PLM] Usage: /plm restore");
				return true;
			}
		}
	}

	private void overwriteMessagesFile() {
		try {
			File messagesFile = new File(plugin.getDataFolder(), "messages.txt");
			FileWriter fw = new FileWriter(messagesFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Join message:");
			bw.newLine();
			bw.write("&e%playername joined the game");
			bw.newLine();
			bw.newLine();
			bw.write("Quit message:");
			bw.newLine();
			bw.write("&e%playername left the game");
			bw.newLine();
			bw.newLine();
			bw.write("How to write own messages:");
			bw.newLine();
			bw.write("Colors are set with the standard minecraft color codes (with '&')");
			bw.newLine();
			bw.write("%playername will be replaced by the normal name.");
			bw.newLine();
			bw.write("%chatplayername will be replaced by the name with color and prefixes (suffixes too)");
			bw.newLine();
			bw.write("%group will be replaced by the name of the player's group");
			bw.newLine();
			bw.write("%world: The world where the player spawned. Only the complete name right now.");
			bw.newLine();
			bw.write("%World outputs the world with a capital letter and no _");
			bw.newLine();
			bw.write("NOTE: Please don't move the lines otherwise the plugin will return wrong values!!");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
			//TODO
		}
	}
}
