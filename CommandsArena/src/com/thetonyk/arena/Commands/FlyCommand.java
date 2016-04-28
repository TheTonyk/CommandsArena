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

public class FlyCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!sender.hasPermission("global.fly.command")) {
			
			sender.sendMessage(Main.NO_PERMS);
    		return true;
    		
		}
		
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /fly <player>");
			return true;
			
		}
		
		Player player = Bukkit.getPlayer(args[0]);
		
		if (player == null) {
			
			sender.sendMessage(Main.PREFIX + "'§6" + args[0] + "§7' is not online.");
			return true;
			
		}
		
		if (player.getAllowFlight()) {
			
			player.setAllowFlight(false);
			player.setFlying(false);
			
			if (sender.getName() != player.getName()) sender.sendMessage(Main.PREFIX + "The player '§6" + player.getName() + "§7' can't no longer fly.");
			
			player.sendMessage(Main.PREFIX + "You are no longer able to fly.");
			
		}
		
		player.setAllowFlight(true);
		
		if (sender.getName() != player.getName()) sender.sendMessage(Main.PREFIX + "The player '§6" + player.getName() + "§7' can now fly.");
		
		player.sendMessage(Main.PREFIX + "You are now able to fly.");
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("global.fly.command")) return null;
		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {

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
