package com.thetonyk.arena.Inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.arena.Utils.ArenaUtils;
import com.thetonyk.arena.Utils.ItemsUtils;

public class ArenasInventory {
	
	public static Inventory getArenas(String name) {
		
		Inventory inventory = Bukkit.createInventory(null, 9, "§8⫸ §4" + name);
		
		for (String id : ArenaUtils.getArenas()) {
			
			ItemStack item = ItemsUtils.createItem(Material.DIAMOND_SWORD, ArenaUtils.getName(id), 1, 0);
			item = ItemsUtils.addGlow(item);
			item = ItemsUtils.hideFlags(item);
			
			inventory.setItem(ArenaUtils.getSlot(id), item);
			
		}
		
		ItemStack separator = ItemsUtils.createItem(Material.STAINED_GLASS_PANE, "§7CommandsPVP", 1, 7);
		
		for (int i = 0; i < inventory.getSize(); i++) {
			
			if (inventory.getItem(i) == null) inventory.setItem(i, separator);
			
		}
		
		return inventory;
		
	}

}
