package com.gmail.fantasticskythrow.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.PLM;
import com.gmail.fantasticskythrow.other.PLMLogger;

public class PLMCommandHandler implements CommandExecutor {

	private PLMLogger plmLogger;
	private boolean advancedStatus;
	private PLM plugin;

	public PLMCommandHandler(PLM plugin, PLMLogger plmLogger, boolean advancedStatus) {
		this.plmLogger = plmLogger;
		this.advancedStatus = advancedStatus;
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String commandType;
		if (args.length != 0) {
			commandType = args[0];
		} else {
			commandType = "";
		}
		if (sender instanceof Player) {
			/*
			 * PLAYER
			 */
			if (sender.hasPermission("plm.*") || sender.hasPermission("plm.admin")) {
				if (commandType.equalsIgnoreCase("reload")) { //plm reload
					PLMReloadCommand.onCommand(sender, args, plugin);
				} else if (commandType.equalsIgnoreCase("restore") && !advancedStatus) { //plm restore without AMM
					PLMRestoreCommand.onCommand(plugin, sender, args);
				} else if (commandType.equalsIgnoreCase("restore") && advancedStatus) { //plm restore with AMM
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&1/plm restore &cis not available with Advanced Messages Mode!"));
				} else { // Wrong subcommand
					sender.sendMessage(ChatColor.RED + "/plm " + commandType + " is not a valid command!");
				}
			} else { // No Permission, Player
				sender.sendMessage(ChatColor.YELLOW + "Sorry, you are not permitted to modify PLM");
				plmLogger.logInfo(sender.getName() + " tried to execute a command although he is not permitted to do that");
			}
			/*
			 * CONSOLE
			 */
		} else {
			if (commandType.equalsIgnoreCase("reload")) { //plm reload
				PLMReloadCommand.onCommand(sender, args, plugin);
			} else if (commandType.equalsIgnoreCase("restore") && !advancedStatus) { //plm restore without AMM
				PLMRestoreCommand.onCommand(plugin, sender, args);
			} else if (commandType.equalsIgnoreCase("restore") && advancedStatus) { //plm restore with AMM
				plmLogger.logInfo("plm restore is not available with Advanced Messages Mode!");
			} else { // Wrong subcommand
				plmLogger.logInfo("plm " + commandType + " is not a valid command!");
			}
		}
		return true;
	}
}
