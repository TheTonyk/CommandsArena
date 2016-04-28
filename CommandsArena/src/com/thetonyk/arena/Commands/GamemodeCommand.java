package com.thetonyk.arena.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thetonyk.arena.Main;

public class GamemodeCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
		if (!sender.hasPermission("global.gamemode")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
			
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /gamemode <gamemode>");
			return true;
			
		}
						
		GameMode gamemode = null;
		
		try {
			
			int gamemodeNumber = Integer.parseInt(args[0]);
			
			if (gamemodeNumber == 0) gamemodeNumber = 1;
			if (gamemodeNumber == 1) gamemodeNumber = 0;
			
			gamemode = GameMode.values()[gamemodeNumber];
			
		} catch (Exception exception) {
			
			String gamemodeString = args[0].toUpperCase();
			
			try {
			
				gamemode = GameMode.valueOf(gamemodeString);
				
			} catch (Exception exceptionString) {
				
				sender.sendMessage(Main.PREFIX + "Usage: /gamemode <gamemode>");
				return true;
				
			}
			
		}
		
		if (gamemode == null) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /gamemode <gamemode>");
			return true;
			
		}
		
		Player player = Bukkit.getPlayer(sender.getName());
			
		if (args.length > 1 && sender.isOp()) {
			
			if (Bukkit.getPlayer(args[1]) == null) {
				
				sender.sendMessage(Main.PREFIX + "§6" + args[1] + " §7is not online.");
				return true;
				
			}
				
			player = Bukkit.getPlayer(args[1]);
			
		}
		
		if (!sender.isOp() && !Bukkit.getPlayer(sender.getName()).getWorld().getName().equalsIgnoreCase("lobby")) {
			
			sender.sendMessage(Main.PREFIX + "You are not in the lobby.");
			return true;
			
		}
		
		player.setGameMode(gamemode);
		player.sendMessage(Main.PREFIX + "Your gamemode was set to §6" + gamemode.name().toLowerCase() + "§7.");
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("global.gamemode")) return null;
		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {

			for (GameMode gamemode : GameMode.values()) {
				
				complete.add(gamemode.toString().toLowerCase());
				
			}
			
		} else if (args.length == 2) {
			
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
