package com.thetonyk.arena.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thetonyk.arena.Main;
import com.thetonyk.arena.Utils.PlayerUtils;

public class ClearCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
		if (!sender.hasPermission("global.clear")) {
			
			sender.sendMessage(Main.NO_PERMS);
    		return true;
    		
		}
			
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /clear <all|player>");
			return true;
			
		}
					
		if (args[0].equalsIgnoreCase("all")) {
			
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				
				PlayerUtils.clearInventory(onlinePlayer);
				
			}
			
			Bukkit.broadcastMessage(Main.PREFIX + "All players inventories are been cleard.");
			return true;
			
		}
			
		Player player = Bukkit.getPlayer(args[0]);
		
		if (player == null) {
			
			sender.sendMessage(Main.PREFIX + "'§6" + args[0] + "§7' is not online.");
			return true;
			
		}
				
		PlayerUtils.clearInventory(player);
		
		if (sender.getName() != player.getName()) sender.sendMessage(Main.PREFIX + "The inventory of '§6" + player.getName() + "§7' has been cleared.");
		
		player.sendMessage(Main.PREFIX + "Your inventory was cleared.");
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("global.clear")) return null;
    		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {
			
			complete.add("all");
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				
				complete.add(player.getName());
				
			}
			
		}
		
		List<String> tabCompletions = new ArrayList<String>();
		
		if (args[args.length - 1].isEmpty()) {
			
			for (String type : complete) {
				
				tabCompletions.add(type);
				
			}
			
		} else {
			
			for (String type : complete) {
				
				if (type.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) tabCompletions.add(type);
				
			}
			
		}
		
		return tabCompletions;
		
	}

}
