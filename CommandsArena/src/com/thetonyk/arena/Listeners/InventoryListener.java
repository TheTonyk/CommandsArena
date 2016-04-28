package com.thetonyk.arena.Listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.arena.Inventories.FeaturesInventory;
import com.thetonyk.arena.Utils.ArenaUtils;
import com.thetonyk.arena.Utils.DatabaseUtils;
import com.thetonyk.arena.Utils.DisplayUtils;
import com.thetonyk.arena.Utils.ItemsUtils;
import com.thetonyk.arena.Utils.PlayerUtils;

public class InventoryListener implements Listener {
	
	public static HashMap<UUID, String> editKits = new HashMap<UUID, String>();
	public static HashMap<UUID, String> editHotbar = new HashMap<UUID, String>();
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {	
		
		if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;
			
		if (event.getInventory().getTitle().equals("§8⫸ §4Features")) {
			
			event.setCancelled(true);
			
			if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
				
			if (event.getCurrentItem().getItemMeta().getDisplayName().substring(6).startsWith("UHC")) {
				
				String arena = ArenaUtils.getId(event.getCurrentItem().getItemMeta().getDisplayName().substring(6).split(" - ")[1]);
				Boolean state = true;
				
				if (event.getCurrentItem().getItemMeta().getDisplayName().substring(5, 6).equalsIgnoreCase("a")) state = false;
				else if (event.getCurrentItem().getItemMeta().getDisplayName().substring(5, 6).equalsIgnoreCase("c")) state = true;
				
				ArenaUtils.setUhc(arena, state);
				event.getWhoClicked().openInventory(FeaturesInventory.getFeatures(arena));
				
			}
			
			if (event.getCurrentItem().getItemMeta().getDisplayName().substring(6).startsWith("Meleefun")) {
				
				String arena = ArenaUtils.getId(event.getCurrentItem().getItemMeta().getDisplayName().substring(6).split(" - ")[1]);
				Boolean state = false;
				
				if (event.getCurrentItem().getItemMeta().getDisplayName().substring(5, 6).equalsIgnoreCase("a")) state = false;
				else if (event.getCurrentItem().getItemMeta().getDisplayName().substring(5, 6).equalsIgnoreCase("c")) state = true;
				
				ArenaUtils.setMeleefun(arena, state);
				event.getWhoClicked().openInventory(FeaturesInventory.getFeatures(arena));
				
			}
			
		}
		else if (event.getInventory().getTitle().equals("§8⫸ §4Set hotbar")) {
			
			event.setCancelled(true);
			
			if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
				
			for (String id : ArenaUtils.getArenas()) {
				
				if (!ArenaUtils.getName(id).equals(event.getCurrentItem().getItemMeta().getDisplayName())) continue;
		
				Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).setGameMode(GameMode.SURVIVAL);
				PlayerUtils.clearInventory(Bukkit.getPlayer(event.getWhoClicked().getUniqueId()));
				Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).closeInventory();
				DisplayUtils.sendTitle(Bukkit.getPlayer(event.getWhoClicked().getUniqueId()), "", "§6Open your inventory to set the kit", 2, 30, 2);
				InventoryListener.editHotbar.put(Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).getUniqueId(), id);
				
				String kitRaw = "";
				
				try {
					
					Statement sql = DatabaseUtils.getConnection().createStatement();
					ResultSet arena = sql.executeQuery("SELECT kit FROM arena_arenas WHERE id = '" + id + "';");
					
					if (arena.next()) kitRaw = arena.getString("kit");
					
					sql.close();
					arena.close();
					
				} catch (SQLException exception) {
					
					Bukkit.getLogger().severe("[InventoryListener] Error to get kit of arena " + id + ".");
					
				}
				
				String[] kit = kitRaw.split(";");
				
				for (int i = 0; i < kit.length; i++) {
					
					ItemStack item = ItemsUtils.unserializeItemStack(kit[i]);
					Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).getInventory().setItem(i, item);
					
				}
				
				return;
				
			}
			
			return;
			
		}
		
		if (event.getInventory().getTitle().equals("§8⫸ §4Set kits")) {
			
			event.setCancelled(true);
			
			if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;	
				

			for (String id : ArenaUtils.getArenas()) {
				
				if (!ArenaUtils.getName(id).equals(event.getCurrentItem().getItemMeta().getDisplayName())) continue;
		
				Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).setGameMode(GameMode.CREATIVE);
				PlayerUtils.clearInventory(Bukkit.getPlayer(event.getWhoClicked().getUniqueId()));
				Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).closeInventory();
				DisplayUtils.sendTitle(Bukkit.getPlayer(event.getWhoClicked().getUniqueId()), "", "§6Open your inventory to set the kit", 2, 10, 2);
				InventoryListener.editKits.put(Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).getUniqueId(), id);
				Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).teleport(new Location(Bukkit.getWorld("lobby"), 500, 1.5, 0));
				
				String kitRaw = "";
				
				try {
					
					Statement sql = DatabaseUtils.getConnection().createStatement();
					ResultSet arena = sql.executeQuery("SELECT kit FROM arena_arenas WHERE id = '" + id + "';");
					
					arena.next();
					kitRaw = arena.getString("kit");
					
					sql.close();
					arena.close();
					
				
				} catch (SQLException e) {
					
					Bukkit.getLogger().severe("§7[ArenaUtils] §cError to get kit of arena §6" + id + "§c.");
					
				}
				
				String[] kit = kitRaw.split(";");
				
				for (int i = 0; i < kit.length; i++) {
					
					ItemStack item = ItemsUtils.unserializeItemStack(kit[i]);
					
					Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).getInventory().setItem(i, item);
					
				}
				
				return;
					
			}
			
			return;
		
		}	
		
		if (event.getInventory().getTitle().equals("§8⫸ §4Set features")) {
			
			event.setCancelled(true);
			
			if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;	
				
			for (String id : ArenaUtils.getArenas()) {
				
				if (!ArenaUtils.getName(id).equals(event.getCurrentItem().getItemMeta().getDisplayName())) continue;
					
				event.getWhoClicked().openInventory(FeaturesInventory.getFeatures(id));				
				return;
			}
		
		}	
			
		if (event.getWhoClicked().getWorld().getName().equals("lobby") && event.getAction() == InventoryAction.HOTBAR_SWAP) {

			event.setCancelled(true);
			return;
				
		}
				
		if (event.getWhoClicked().getWorld().getName().equals("lobby") && !InventoryListener.editHotbar.containsKey(event.getWhoClicked().getUniqueId())) {
					
			event.setCancelled(true);
			return;
		
		}
			
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClose(InventoryCloseEvent event) {

		if (event.getInventory().getType() == InventoryType.ANVIL) {
			
			if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) return;
				
			if (!(event.getInventory().getHolder() instanceof BlockState)) return;
				
			if (!PlayerListener.anvils.contains(((BlockState) event.getInventory().getHolder()).getLocation())) return;
						
			for (Location location : PlayerListener.anvils) {
				
				if (!location.equals(((BlockState) event.getInventory().getHolder()).getLocation())) continue;
					
				event.getPlayer().getWorld().getBlockAt(location).setData((byte) 1);
				
			}
			
		}
		
		if (editHotbar.containsKey(event.getPlayer().getUniqueId())) {
			
			String hotbar = "";
			
			if (!PlayerUtils.getHotbar(Bukkit.getPlayer(event.getPlayer().getUniqueId())).isEmpty()) {
				
				for (String arena : PlayerUtils.getHotbar(Bukkit.getPlayer(event.getPlayer().getUniqueId())).split("%")) {
					
					if (arena.split("#")[0].equalsIgnoreCase(editHotbar.get(event.getPlayer().getUniqueId()))) continue;
						
					hotbar += arena + "%";
					
				}
				
			}
			
			String kit = editHotbar.get(event.getPlayer().getUniqueId()) + "#";
			
			ItemStack[] items= (ItemStack[]) ArrayUtils.addAll(event.getPlayer().getInventory().getContents(), event.getPlayer().getInventory().getArmorContents());
			
			for (int i = 0; i < items.length; i++) {
				
				kit += ItemsUtils.serializeItemStack(items[i]) + ";";
				
			}
			
			hotbar += kit + "%";
			
			PlayerUtils.setHotbar(Bukkit.getPlayer(event.getPlayer().getUniqueId()), hotbar);
			DisplayUtils.sendTitle(Bukkit.getPlayer(event.getPlayer().getUniqueId()), "", "§aYour hotbar has been saved.", 2, 10, 2);
			event.getPlayer().setGameMode(GameMode.ADVENTURE);
			PlayerUtils.clearInventory(Bukkit.getPlayer(event.getPlayer().getUniqueId()));
			InventoryListener.editHotbar.remove(event.getPlayer().getUniqueId());
			ArenaUtils.placeItems(Bukkit.getPlayer(event.getPlayer().getUniqueId()));
			
		}
		
	}

}
