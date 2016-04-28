package com.thetonyk.arena.Inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.arena.Utils.ArenaUtils;
import com.thetonyk.arena.Utils.ItemsUtils;

public class FeaturesInventory {
	
	public static Inventory getFeatures(String id) {
		
		Inventory inventory = Bukkit.createInventory(null, 9, "§8⫸ §4Features");
		
		if (ArenaUtils.getUhc(id)) {
			
			ItemStack uhc = ItemsUtils.createItem(Material.GOLDEN_APPLE, "§8⫸ §aUHC§8 - " + ArenaUtils.getName(id), 1, 0);
			ItemsUtils.addGlow(uhc);
			ItemsUtils.hideFlags(uhc);
			
			inventory.setItem(0, uhc);
			
		} else {
			
			ItemStack uhc = ItemsUtils.createItem(Material.GOLDEN_APPLE, "§8⫸ §cUHC§8 - " + ArenaUtils.getName(id), 1, 0);
			ItemsUtils.hideFlags(uhc);
			
			inventory.setItem(0, uhc);
			
		}

		if (ArenaUtils.getMeleefun(id)) {
			
			ItemStack uhc = ItemsUtils.createItem(Material.DIAMOND_SWORD, "§8⫸ §aMeleefun§8 - " + ArenaUtils.getName(id), 1, 0);
			ItemsUtils.addGlow(uhc);
			ItemsUtils.hideFlags(uhc);
			
			inventory.setItem(1, uhc);
			
		} else {
			
			ItemStack uhc = ItemsUtils.createItem(Material.DIAMOND_SWORD, "§8⫸ §cMeleefun§8 - " + ArenaUtils.getName(id), 1, 0);
			ItemsUtils.hideFlags(uhc);
			
			inventory.setItem(1, uhc);
			
		}
		
		ItemStack separator = ItemsUtils.createItem(Material.STAINED_GLASS_PANE, "§7CommandsPVP", 1, 7);
		
		for (int i = 0; i < inventory.getSize(); i++) {
			
			if (inventory.getItem(i) == null) inventory.setItem(i, separator);
			
		}
		
		return inventory;
		
	}

}
