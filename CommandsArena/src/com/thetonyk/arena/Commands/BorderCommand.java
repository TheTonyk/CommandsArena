package com.thetonyk.arena.Commands;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.thetonyk.arena.Main;
import com.thetonyk.arena.Utils.DatabaseUtils;
import com.thetonyk.arena.Utils.WorldUtils;

public class BorderCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("arena.border")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		if (args.length == 0) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /border <world> [size]");
			return true;
			
		}
		
		if (!sender.hasPermission("arena.setborder")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		if (!WorldUtils.exist(args[0])) {
			
			sender.sendMessage(Main.PREFIX + "The world '§6" + args[0] + "§7' doesn't exist.");
			return true;
			
		}
		
		if (args.length == 1) {
			
			int radius = WorldUtils.getSize(args[0]);
			
			sender.sendMessage(Main.PREFIX + "Size of world '§6" + args[0] + "§7': §a" + radius + "§7x§a" + radius + "§7.");
			return true;
			
		}
		
		int size;
		
		try {
			
			size = Integer.parseInt(args[1]);
			
		} catch (Exception e) {
			
			sender.sendMessage(Main.PREFIX + "Please enter a valid size of world.");
			return true;
			
		}
		
		if (size > 10000 || size < 100) {
			
			sender.sendMessage(Main.PREFIX + "Please enter a valid size of world.");
			return true;
			
		}
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_worlds SET size = '" + args[1] + "' WHERE name = '" + args[0] + "';");		
			sql.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[BorderCommand] Error to update size of world " + args[0] + " in database.");
			sender.sendMessage(Main.PREFIX + "Error to update size of world §6" + args[0] + "§7 in database.");
			return true;
			
		}
		
		if (Bukkit.getWorld(args[0]) != null) Bukkit.getWorld(args[0]).getWorldBorder().setSize(size);
		Bukkit.broadcastMessage(Main.PREFIX + "Size of world '§6" + args[0] + "§7' set to: §a" + size + "§7x§a" + size + "§7.");
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("arena.border")) return null;
		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {
			
			for (World world : Bukkit.getWorlds()) {
				
				if (world.getName().equalsIgnoreCase("lobby")) continue;
				
				complete.add(world.getName());
				
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
