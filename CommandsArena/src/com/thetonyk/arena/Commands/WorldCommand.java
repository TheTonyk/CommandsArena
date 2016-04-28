package com.thetonyk.arena.Commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.thetonyk.arena.Main;
import com.thetonyk.arena.Utils.ArenaUtils;
import com.thetonyk.arena.Utils.DatabaseUtils;
import com.thetonyk.arena.Utils.WorldUtils;

public class WorldCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
		if (!sender.hasPermission("arena.world")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
			
		if (args.length > 0) {
					
			if (args[0].equalsIgnoreCase("create")) {
				
				if (args.length < 3) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /world create <name> <size> [seed]");
					return true;
					
				}
					
				if (WorldUtils.exist(args[1])) {
					
					sender.sendMessage(Main.PREFIX + "The world '§6" + args[1] + "§7' is already created.");
					return true;
					
				}
				
				if (args[1].equalsIgnoreCase("world")) {
					
					sender.sendMessage(Main.PREFIX + "The world '§6" + args[1] + "§7' is already created.");
					return true;
					
				}
				
				if (args[1].contains("_end") || args[1].contains("_nether")) {
					
					sender.sendMessage(Main.PREFIX + "You can't created Nether or End world by yourself.");
					return true;
					
				}
				
				String name = args[1];
				int radius = Integer.parseInt(args[2]);
				Boolean nether = false;
				Boolean end = false;
				long seed = new Random().nextLong();
				
				if (args.length > 3) seed = Long.parseLong(args[3]);
				
				sender.sendMessage(Main.PREFIX + "Creation of world '§6" + name + "§7'...");
				sender.sendMessage("§8⫸ §7Size: §a" + radius);
				sender.sendMessage("§8⫸ §7Seed: §a" + seed);
				
				WorldUtils.createWorld(name, Environment.NORMAL, seed, WorldType.NORMAL, radius);
				
				if (nether) WorldUtils.createWorld(name + "_nether", Environment.NETHER, seed, WorldType.NORMAL, radius);	
				
				if (end) WorldUtils.createWorld(name + "_end", Environment.THE_END, seed, WorldType.NORMAL, radius);	
				
				sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' has been created.");
				return true;
					
			}			
			
			if (args[0].equalsIgnoreCase("delete")) {
							
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /world delete <world>");
					return true;
					
				}
				
				for (String id : ArenaUtils.getArenas()) {
					
					if (ArenaUtils.getWorld(id) == args[1]) {
						
						sender.sendMessage(Main.PREFIX + "This world is use for arena " + ArenaUtils.getName(id) + "§7.");
						return true;
						
					}
					
				}
								
				if (args[1].contains("_end") || args[1].contains("_nether")) {
					
					sender.sendMessage(Main.PREFIX + "You can't delete Nether or End world by yourself.");
					return true;
					
				}
								
				if (Bukkit.getWorld(args[1]) == null) {
					
					sender.sendMessage(Main.PREFIX + "The world '§6" + args[1] + "§7' doesn't exist.");
					return true;
					
				}
					
											
				if (Bukkit.getWorld(args[1]).getName().equalsIgnoreCase("lobby")) {
					
					sender.sendMessage(Main.PREFIX + "You can't delete the lobby.");
					return true;
					
				}
											
				WorldUtils.deleteWorld(args[1]);
											
				sender.sendMessage(Main.PREFIX + "The world '§6" + args[1] + "§7' has been deleted.");	
				
				if (WorldUtils.exist(args[1] + "_nether")) WorldUtils.deleteWorld(args[1] + "_nether");
				if (WorldUtils.exist(args[1] + "_end")) WorldUtils.deleteWorld(args[1] + "_end");
				return true;						
							
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
							
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /world reload <world>");
					return true;
					
				}
				
				for (String id : ArenaUtils.getArenas()) {
					
					if (ArenaUtils.getWorld(id) == args[1]) {
						
						sender.sendMessage(Main.PREFIX + "This world is use for arena " + ArenaUtils.getName(id) + "§7.");
						return true;
						
					}
					
				}
									
				WorldUtils.unloadWorld(args[1]);
				WorldUtils.loadWorld(args[1]);
										
				sender.sendMessage(Main.PREFIX + "The world '§6" + Bukkit.getWorld(args[1]).getName() + "§7' has been reloaded.");
				return true;					
							
			}
			
			if (args[0].equalsIgnoreCase("list")) {
							
				sender.sendMessage(Main.PREFIX + "Existing worlds:");
							
				if (Bukkit.getWorld("lobby") != null) sender.sendMessage(Main.PREFIX + "§7" + Bukkit.getWorld("lobby").getName() + " §8- §a" + Bukkit.getWorld("lobby").getEnvironment().name() + "§7.");
				else sender.sendMessage(Main.PREFIX + "§clobby §8- §aNORMAL§7.");
							
				try {
					
					Statement sql = DatabaseUtils.getConnection().createStatement();
					ResultSet worlds = sql.executeQuery("SELECT * FROM arena_worlds;");
					
					String world = null;
					String color = "§7";
					
					while (worlds.next()) {
						
						world = worlds.getString("name");
						
						switch (worlds.getString("environment")) {
						
						case "NORMAL":
							color = "§a";
							break;
						case "NETHER":
							color = "§c";
							break;
						case "THE_END":
							color = "§9";
							break;
						default:
							break;
							
						}
						
						if (Bukkit.getWorld(world) != null) sender.sendMessage(Main.PREFIX + "§7" + world + " §8- " + color + worlds.getString("environment") + "§7.");		
						else sender.sendMessage(Main.PREFIX + "§c" + world + " §8- " + color + worlds.getString("environment") + "§7.");						
						
					}
					
					sql.close();
					worlds.close();
				
				} catch (SQLException exception) {
					
					Main.arena.getLogger().severe("[WorldUtils] Error to fetch informations of worlds in DB.");
					sender.sendMessage(Main.PREFIX + "§cError to fetch informations of worlds in DB.");
					
				}
				
				return true;
							
			}
			
			if (args[0].equalsIgnoreCase("tp")) {
							
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /world tp <world>");
					return true;
					
				}
				
				if (!WorldUtils.exist(args[1]) && !args[1].equalsIgnoreCase("lobby")) {
					
					sender.sendMessage(Main.PREFIX + "The world '§6" + args[1] + "§7' doesn't exist.");
					return true;
					
				}
				
				if (args[1].equalsIgnoreCase("lobby")) {
					
					Bukkit.getPlayer(sender.getName()).teleport(Bukkit.getWorld(args[1]).getSpawnLocation());	
					sender.sendMessage(Main.PREFIX + "You have been teleported to world '§6" + args[1] + "§7'.");
					return true;
					
				}
								
				if (Bukkit.getWorld(args[1]) == null) {
						
					sender.sendMessage(Main.PREFIX + "The world '§6" + args[1] + "§7' is not loaded.");
					
				}
				
				Bukkit.getPlayer(sender.getName()).teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
				sender.sendMessage(Main.PREFIX + "You have been teleported to world '§6" + args[1] + "§7'.");
				return true;
							
			}
		
		}
		
		sender.sendMessage(Main.PREFIX + "Usage of /world:");
		sender.sendMessage("§8⫸ §6/world create §8- §7Create a world.");
		sender.sendMessage("§8⫸ §6/world delete §8- §7Delete a world.");
		sender.sendMessage("§8⫸ §6/world reload §8- §7Reload a world.");
		sender.sendMessage("§8⫸ §6/world list §8- §7List all worlds.");
		sender.sendMessage("§8⫸ §6/world tp §8- §7Teleport to a world.");
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("arena.world")) return null;
		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {

			complete.add("create");
			complete.add("delete");
			complete.add("reload");
			complete.add("list");
			complete.add("tp");
			
		} else if (args.length == 2) {
			
			if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("tp")) {
				
				for (World world : Bukkit.getWorlds()) {
					
					if (world.getName().equalsIgnoreCase("lobby")) {
						
						continue;
						
					}
					
					complete.add(world.getName());
					
				}
				
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
