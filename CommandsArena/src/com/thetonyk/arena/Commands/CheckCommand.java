package com.thetonyk.arena.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.arena.Main;
import com.thetonyk.arena.Listeners.PlayerListener;
import com.thetonyk.arena.Utils.DisplayUtils;

public class CheckCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
		if (!sender.hasPermission("arena.check")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
			
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /check <player> [seconds]");
			return true;
			
		}
		
		if (Bukkit.getPlayer(args[0]) == null) {
			
			sender.sendMessage(Main.PREFIX + "The player '§6" + args[0] + "§7' is not online.");
			return true;
			
		}
		
		Player player = Bukkit.getPlayer(args[0]);
		int time = 30;
					
		if (args.length > 2) {
			
			try {
				
				time = Integer.parseInt(args[1]);
				
			} catch (Exception exception) {
				
				sender.sendMessage(Main.PREFIX + "The time is not valid.");
				return true;
				
			}
			
			if (time < 1 || time > 600) {
				
				sender.sendMessage(Main.PREFIX + "The time is not valid.");
				return true;
				
			}
			
		}
		
		if (PlayerListener.cps.containsKey(player)) {
			
			sender.sendMessage(Main.PREFIX + "This player is already checked by someone for now.");
			return true;
			
		}
		
		PlayerListener.cps.put(player, 0);
		
		BukkitRunnable update = new BukkitRunnable() {
			
			int i = 0;
			int maxCps = 0;
			
			public void run() {
				
				if (Bukkit.getPlayer(sender.getName()) == null || !Bukkit.getPlayer(sender.getName()).isOnline()) {
					
					cancel();
					PlayerListener.cps.remove(player);
					
				}
				
				if (i > 20) {
					
					i = 0;
					if (PlayerListener.cps.get(player) > maxCps) maxCps = PlayerListener.cps.get(player);
					PlayerListener.cps.put(player, 0);
					
				}
				
				DisplayUtils.sendActionBar(Bukkit.getPlayer(sender.getName()), "§a§l" + player.getName() + " §8⫸ §7Ping: §6" + ((CraftPlayer) player).getHandle().ping + "§7ms §8| §7CPS: §6" + PlayerListener.cps.get(player) + " §8| §7Max CPS: §6" + maxCps);
				i++;
			}
			
		};
		
		update.runTaskTimer(Main.arena, 1, 1);
		
		new BukkitRunnable() {
			
			public void run() {
				
				if (Bukkit.getPlayer(sender.getName()) == null || !Bukkit.getPlayer(sender.getName()).isOnline()) return;
				
				update.cancel();
				PlayerListener.cps.remove(player);
				DisplayUtils.sendActionBar(Bukkit.getPlayer(sender.getName()), "");
				
			}
			
		}.runTaskLater(Main.arena, (time * 20));
		
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("arena.check")) return null;
    		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {

			for (Player player : Bukkit.getOnlinePlayers()) {
				
				complete.add(player.getName());
				
			}
			
		} else if (args.length == 2) {
			
			complete.add("15");
			complete.add("30");
			complete.add("45");
			complete.add("60");
			complete.add("90");
			complete.add("120");
			complete.add("300");
			
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
