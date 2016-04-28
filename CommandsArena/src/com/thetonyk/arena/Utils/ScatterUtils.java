package com.thetonyk.arena.Utils;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.thetonyk.arena.Main;

public class ScatterUtils {
	
	public static Location getSpawns(World world, int size) {
			
		for (int i = 0; i < 3000; i++) {
			
			if (i == 2999) {
				
				for (Player playerSend : Bukkit.getOnlinePlayers()) {
					
					if (!playerSend.hasPermission("arena.warning")) continue;
						
					playerSend.sendMessage(Main.PREFIX + "Error to teleport a player.");
					
				}
				
			}
			
			Random random = new Random();
			int x = random.nextInt(size) - (size / 2);
			int z = random.nextInt(size) - (size / 2);
			Location spawn = new Location(world, x + 0.5, 0, z + 0.5);
			Boolean valid = true;
			
			if (PlayerUtils.getHighestY(x, z, world) < 60) valid = false;
			
			Material block = new Location(world, x + 0.5, PlayerUtils.getHighestY(x, z, world), z + 0.5).getBlock().getType();
			
			switch (block) {
			
				case CACTUS:
				case LAVA:
				case STATIONARY_LAVA:
				case STATIONARY_WATER:
				case WATER:
				case LEAVES:
				case LEAVES_2:
					valid = false;
					break;
				default:
					break;
			
			}
			
			if (valid) {
				
				spawn.setY(PlayerUtils.getHighestY((int) spawn.getX(), (int) spawn.getZ(), world) + 1);
				return spawn;
				
			}
			
		}
		
		return new Location(world, 0, PlayerUtils.getHighestY(0, 0, world), 0);
		
	}
	
}
