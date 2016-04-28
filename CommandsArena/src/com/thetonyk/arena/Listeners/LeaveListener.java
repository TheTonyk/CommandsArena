package com.thetonyk.arena.Listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.arena.Arena;
import com.thetonyk.arena.Utils.ItemsUtils;
import com.thetonyk.arena.Utils.PermissionsUtils;
import com.thetonyk.arena.Utils.PlayerUtils;

public class LeaveListener implements Listener{
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
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
			
		}
		
		PlayerUtils.updateScores(player);
		Arena.scores.remove(player);
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		
		if (Arena.playerBlocks.containsKey(player)) {
		
			for (Location block : Arena.playerBlocks.get(player)) {
				
				block.getBlock().setType(Material.AIR);
				
				if (Arena.blocks.contains(block)) Arena.blocks.remove(block);
				
				if (Arena.water.contains(block)) {
					
					block.getBlock().setType(Material.STATIONARY_WATER);
					Arena.water.remove(block);
					
				}
				
			}
		
			Arena.playerBlocks.remove(player);
			
		}
		
		event.setQuitMessage("§7[§c-§7] " + PlayerUtils.getRank(player.getName()).getPrefix() + "§7" + player.getName());
		
		if (InventoryListener.editKits.containsKey(event.getPlayer().getUniqueId())) InventoryListener.editKits.remove(event.getPlayer().getUniqueId());
		if (PlayerListener.cps.containsKey(event.getPlayer())) PlayerListener.cps.remove(event.getPlayer());
		
		PlayerUtils.leaveUpdatePlayer(player);
		PermissionsUtils.clearPermissions(player);
		
		if (player.isInsideVehicle()) player.leaveVehicle();
		
	}

}
