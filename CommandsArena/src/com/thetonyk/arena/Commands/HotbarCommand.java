package com.thetonyk.arena.Commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.thetonyk.arena.Main;
import com.thetonyk.arena.Inventories.ArenasInventory;

public class HotbarCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!sender.hasPermission("arena.hotbar")) {
			
			sender.sendMessage(Main.NO_PERMS);
    		return true;
    		
		}
		
		if (!Bukkit.getPlayer(sender.getName()).getWorld().getName().equalsIgnoreCase("lobby")) {
			
			sender.sendMessage(Main.PREFIX + "You can't change your hotbar in the arena.");
			return true;
			
		}
		
		Bukkit.getPlayer(sender.getName()).openInventory(ArenasInventory.getArenas("Set hotbar"));
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		return null;
		
	}

}
