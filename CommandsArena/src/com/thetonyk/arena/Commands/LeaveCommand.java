package com.thetonyk.arena.Commands;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.arena.Arena;
import com.thetonyk.arena.Main;
import com.thetonyk.arena.Utils.ArenaUtils;
import com.thetonyk.arena.Utils.ItemsUtils;
import com.thetonyk.arena.Utils.PlayerUtils;

public class LeaveCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!sender.hasPermission("arena.leave")) {
			
			sender.sendMessage(Main.NO_PERMS);
    		return true;
    		
		}
		
		Player player = Bukkit.getPlayer(sender.getName());
		
		if (!player.getWorld().getName().equalsIgnoreCase("lobby")) {
			
			player.getWorld().dropItem(player.getLocation(), ItemsUtils.createItem(Material.GOLDEN_APPLE, "§b§lCommandsPVP §r§6Golden Apple", 1, 0));
			player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.ARROW, 8));
			if (new Random().nextDouble() < 0.35) player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.INK_SACK, 1, (short) 4));
			if (new Random().nextDouble() < 0.20) player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.DIAMOND, 1));
			Integer[] scores = Arena.scores.get(player);
			scores[1] = Arena.scores.get(player)[1] + 1;
			if (Arena.scores.get(player)[4] > Arena.scores.get(player)[2]) scores[2] = Arena.scores.get(player)[4];
			scores[4] = 0;
			Arena.scores.put(player, scores);
			PlayerUtils.updateScoreboard(player);
			
		}
		
		new BukkitRunnable() {
			
			public void run() {
				
				if (Arena.playerBlocks.containsKey(player)) {
					
					for (Location block : Arena.playerBlocks.get(player)) {
						
						block.getBlock().setType(Material.AIR);
						
						if (Arena.blocks.contains(block)) Arena.blocks.remove(block);
						
						if (Arena.water.contains(block)) {
							
							block.getBlock().setType(Material.STATIONARY_WATER);
							Arena.water.remove(block);
							
						}
						
					}
					
					Arena.playerBlocks.get(player).clear();
					
				}
				
			}
			
		}.runTaskLater(Main.arena, 5);
		
		player.teleport(Bukkit.getWorld("lobby").getSpawnLocation().add(0.5, 0, 0.5));
		player.setGameMode(GameMode.ADVENTURE);
		
		PlayerUtils.clearInventory(player);
		PlayerUtils.clearXp(player);
		PlayerUtils.feed(player);
		PlayerUtils.heal(player);
		PlayerUtils.clearEffects(player);
		player.setExp(0);
		player.setTotalExperience(0);
		
		ArenaUtils.placeItems(player);
		if (player.hasPermission("global.fly")) player.setAllowFlight(true);
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		return null;
		
	}

}
