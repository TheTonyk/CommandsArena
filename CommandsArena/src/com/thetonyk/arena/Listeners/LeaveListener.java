package com.thetonyk.arena.Listeners;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.thetonyk.arena.Arena;
import com.thetonyk.arena.Main;
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
			
			if (event.getPlayer().getKiller() != null && event.getPlayer() != event.getPlayer().getKiller()) {
				
				scores = Arena.scores.get(event.getPlayer().getKiller());
				scores[0] = Arena.scores.get(event.getPlayer().getKiller())[0] + 1;
				scores[4] = Arena.scores.get(event.getPlayer().getKiller())[4] + 1;
				Arena.scores.put(event.getPlayer().getKiller(), scores);
				
				PlayerUtils.updateScoreboard(event.getPlayer().getKiller());
				
				NumberFormat format = new DecimalFormat("##.#");
				event.getPlayer().getKiller().sendMessage(Main.PREFIX + "You killed '§6" + event.getPlayer().getName() + "§7' §8(§7" + format.format((event.getPlayer().getKiller().getHealth() / 2) * 10) + "%§8)");
				event.getPlayer().sendMessage(Main.PREFIX + "You have been killed by '§6" + event.getPlayer().getKiller().getName() + "§7' §8(§7" + format.format((event.getPlayer().getKiller().getHealth() / 2) * 10) + "%§8)");
				event.getPlayer().getKiller().playSound(event.getPlayer().getKiller().getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
				event.getPlayer().getKiller().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, true));
				event.getPlayer().getKiller().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1, false, true));
				
				double health = event.getPlayer().getKiller().getHealth() + 4;
				
				event.getPlayer().getKiller().setLevel(event.getPlayer().getKiller().getLevel() + 1);
				if (health > 20) health = 20;
				event.getPlayer().getKiller().setHealth(health);
				
			}
			
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
