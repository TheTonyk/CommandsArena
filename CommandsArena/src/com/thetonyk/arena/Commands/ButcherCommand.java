package com.thetonyk.arena.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;

import com.thetonyk.arena.Main;
import com.thetonyk.arena.Utils.WorldUtils;

public class ButcherCommand implements CommandExecutor, TabCompleter {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("global.butcher")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /butcher <world>");
			return true;
			
		}
		
		if (!WorldUtils.exist(args[0]) && !args[0].equalsIgnoreCase("lobby")) {
			
			sender.sendMessage(Main.PREFIX + "The world '§6" + args[0] + "§7' doesn't exist.");
			return true;
			
		}
						
		if (Bukkit.getWorld(args[0]) == null) {
				
			sender.sendMessage(Main.PREFIX + "The world '§6" + args[1] + "§7' is not loaded.");
			return true;
			
		}
		
		int count = 0;
			
		for (Entity entity : Bukkit.getWorld(args[0]).getEntities()) {
			
			switch (entity.getType()) {
			
				case ARMOR_STAND:
				case ARROW:
				case BOAT:
				case CHICKEN:
				case COMPLEX_PART:
				case COW:
				case DROPPED_ITEM:
				case EGG:
				case ENDER_CRYSTAL:
				case ENDER_DRAGON:
				case ENDER_PEARL:
				case ENDER_SIGNAL:
				case EXPERIENCE_ORB:
				case FALLING_BLOCK:
				case FIREWORK:
				case FISHING_HOOK:
				case HORSE:
				case IRON_GOLEM:
				case ITEM_FRAME:
				case LEASH_HITCH:
				case LIGHTNING:
				case MINECART:
				case MINECART_CHEST:
				case MINECART_COMMAND:
				case MINECART_FURNACE:
				case MINECART_HOPPER:
				case MINECART_MOB_SPAWNER:
				case MINECART_TNT:
				case MUSHROOM_COW:
				case OCELOT:
				case PAINTING:
				case PIG:
				case PLAYER:
				case PRIMED_TNT:
				case RABBIT:
				case SHEEP:
				case SNOWBALL:
				case SNOWMAN:
				case SPLASH_POTION:
				case SQUID:
				case THROWN_EXP_BOTTLE:
				case UNKNOWN:
				case VILLAGER:
				case WEATHER:
				case WOLF:
					continue;
				default:
					break;
			
			}
			
			entity.remove();
			count++;
			
		}
		
		Bukkit.broadcastMessage(Main.PREFIX + "§a" + count + "§7 entites killed.");
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("global.butcher")) return null;
		
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

