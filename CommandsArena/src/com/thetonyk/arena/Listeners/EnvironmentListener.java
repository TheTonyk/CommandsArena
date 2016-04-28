package com.thetonyk.arena.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.thetonyk.arena.Main;
import com.thetonyk.pregenerator.DisplayUtils;
import com.thetonyk.pregenerator.Events.WorldBorderFillFinishedEvent;
import com.thetonyk.pregenerator.Events.WorldBorderFillStartEvent;

public class EnvironmentListener implements Listener {

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onPregenBegin(WorldBorderFillStartEvent event) {
		
		Bukkit.broadcastMessage(Main.PREFIX + "Pregeneration of world '§6" + event.getWorld().getName() + "§7' started.");
		
	}
	
	@EventHandler
	public void onPregenFinished(WorldBorderFillFinishedEvent event) {
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pregenerator clear all");
		
		String message = "§7Pregeneration of world '§6" + event.getWorld().getName() + "§7'... (§a100.00%§7)";
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			DisplayUtils.sendActionBar(player, message);
			
		}
		
		Bukkit.broadcastMessage(Main.PREFIX + "Pregeneration of world '§6" + event.getWorld().getName() + "§7' finished.");
				
	}
	
	@EventHandler
	public void onRedstoneUpdate(BlockRedstoneEvent event) {
		
		if (event.getBlock().getWorld().getName().equalsIgnoreCase("lobby")) {
			
			if (event.getBlock().getType() == Material.REDSTONE_LAMP_ON) event.setNewCurrent(event.getOldCurrent());
			
		}
		
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		
		if (event.getEntity().getWorld().getName().equalsIgnoreCase("lobby")) {
			
			if (event.getEntityType() == EntityType.ENDER_CRYSTAL) event.setCancelled(true);
			
		}
		
	}
	
	@EventHandler
	public void onGrow(BlockGrowEvent event) {
		
		if (event.getBlock().getWorld().getName().equalsIgnoreCase("lobby")) {
			
			if (event.getNewState().getType().equals(Material.SUGAR_CANE_BLOCK)) event.setCancelled(true);
			
		}
		
	}
	
	@EventHandler
	public void onPhysics(BlockPhysicsEvent event) {
		
		if (event.getBlock().getType() == Material.STATIONARY_WATER || event.getBlock().getType() == Material.WATER || event.getBlock().getType() == Material.STATIONARY_LAVA || event.getBlock().getType() == Material.LAVA || event.getChangedType() == Material.STATIONARY_WATER || event.getChangedType() == Material.WATER || event.getChangedType() == Material.STATIONARY_LAVA || event.getChangedType() == Material.LAVA) event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onChange(BlockFromToEvent event) {
		
		if (event.getBlock().getType() == Material.STATIONARY_WATER || event.getBlock().getType() == Material.WATER) event.setCancelled(true);
		
	}
	
}
