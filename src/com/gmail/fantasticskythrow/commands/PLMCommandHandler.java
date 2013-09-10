package com.gmail.fantasticskythrow.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.fantasticskythrow.other.PLMLogger;

public class PLMCommandHandler implements CommandExecutor {

	private PLMLogger plmLogger;
	private boolean advancedStatus;

	public PLMCommandHandler(PLMLogger plmLogger, boolean advancedStatus) {
		this.plmLogger = plmLogger;
		this.advancedStatus = advancedStatus;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player) || sender.hasPermission("plm.*") || sender.hasPermission("plm.admin")) {
			String commandType = args[0];
			if (commandType.equalsIgnoreCase("reload")) {

			} else if (commandType.equalsIgnoreCase("refresh")) {

			} else if (commandType.equalsIgnoreCase("restore")) {

			} else { // Wrong subcommand
				sender.sendMessage("/plm " + commandType + " is not a valid command!");
			}
		} else { // No Permission, Player
			sender.sendMessage("Sorry, you are not allowed to modify PLM");
			plmLogger.logInfo("[PLM] " + sender.getName() + " tried to execute a command (" + label + ") although he is not permitted to do that");
		}
		return true;
	}
	
	protected void sendMessage(String playerMessage, CommandSender sender, String logMessage, String consoleMessage) {
		if (sender instanceof Player) {	//Player
			if (playerMessage != null) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', playerMessage));
			}
			if (logMessage != null || logMessage != "") {
				plmLogger.logInfo(logMessage);
			}
		} else { //Console
			if (consoleMessage != null) {
				plmLogger.logInfo(consoleMessage);
			}
		}
	}
}
