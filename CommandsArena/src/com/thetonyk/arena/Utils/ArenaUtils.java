package com.thetonyk.arena.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.arena.Arena;
import com.thetonyk.arena.Main;

public class ArenaUtils {

	public static void create(String id, String world, int slot, String name) {
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("INSERT INTO arena_arenas (`id`, `name`, `slot`, `world`, `kit`, `state`, `size`, `uhc`, `meleefun`) VALUES ('" + id + "', '" + name + "', " + slot + ", '" + world + "', '', 0, 100, 1, 0);");
			sql.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[ArenaUtils] Error to insert new arena " + id + ".");
			return;
			
		}
		
		for (Player player : Bukkit.getWorld("lobby").getPlayers()) {
			
			if (player.getGameMode() != GameMode.ADVENTURE) continue;
			
			ArenaUtils.placeItems(player);
		
		}
		
	}
	
	public static void reload() {
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet arenas = sql.executeQuery("SELECT * FROM arena_arenas;");
			
			while (arenas.next()) {
				
				WorldUtils.unloadWorld(arenas.getString("world"));
				WorldUtils.loadWorld(arenas.getString("world"));
				
			}
			
			sql.close();
			arenas.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[ArenaUtils] Error to fetch all arenas.");
			return;
			
		}
		
		for (Player player : Bukkit.getWorld("lobby").getPlayers()) {
			
			if (player.getGameMode() != GameMode.ADVENTURE) return;
			
			ArenaUtils.placeItems(player);
		
		}
		
	}
	
	public static void setName(String id, String name) {
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_arenas SET name = '" + name + "' WHERE id = '" + id + "';");
			sql.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to update name of arena " + id + ".");
			
		}
		
	}
	
	public static void setWorld(String id, String world) {
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_arenas SET world = '" + world + "' WHERE id = '" + id + "';");
			sql.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to update world of arena " + id + ".");
			
		}
		
	}
	
	public static void setSlot(String id, int slot) {
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_arenas SET slot = '" + slot + "' WHERE id = '" + id + "';");
			sql.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to update slot of arena " + id + ".");
			
		}
		
	}
	
	public static List<String> getArenas() {
		
		List<String> list = new ArrayList<String>();
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet arenas = sql.executeQuery("SELECT * FROM arena_arenas;");
			
			while (arenas.next()) {
				
				list.add(arenas.getString("id"));
				
			}
			
			sql.close();
			arenas.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[ArenaUtils] Error to fetch all arenas.");
			
		}
		
		return list;
		
	}
	
	public static String getName(String id) {
		
		String name = null;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet arenas = sql.executeQuery("SELECT * FROM arena_arenas WHERE id = '" + id + "';");
			
			if (arenas.next()) name = arenas.getString("name");
			
			sql.close();
			arenas.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[ArenaUtils] Error to get name of arena " + id + ".");
			
		}
		
		return name;
		
	}
	
	public static int getSlot(String id) {
		
		int slot = -1;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet arenas = sql.executeQuery("SELECT * FROM arena_arenas WHERE id = '" + id + "';");
			
			if (arenas.next()) slot = arenas.getInt("slot");
			
			sql.close();
			arenas.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[ArenaUtils] Error to get slot of arena " + id + ".");
			
		}
		
		return slot;
		
	}

	public static String getWorld(String id) {
	
		String world = null;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet arenas = sql.executeQuery("SELECT * FROM arena_arenas WHERE id = '" + id + "';");
			
			if (arenas.next()) world = arenas.getString("world");
			
			sql.close();
			arenas.close();
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[ArenaUtils] Error to get world of arena " + id + ".");
			
		}
		
		return world;
		
	}
	
	public static void delete(String id) {
		
		for (Player player : Bukkit.getWorld(getWorld(id)).getPlayers()) {
			
			player.teleport(Bukkit.getWorld("lobby").getSpawnLocation().add(0.5, 0, 0.5));
			player.setGameMode(GameMode.ADVENTURE);
			PlayerUtils.clearInventory(player);
			PlayerUtils.clearXp(player);
			PlayerUtils.feed(player);
			PlayerUtils.heal(player);
			PlayerUtils.clearEffects(player);
			player.setExp(0);
			player.setTotalExperience(0);
			
		}
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("DELETE FROM arena_arenas WHERE id = '" + id + "';");
			sql.close();
		
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to delete arena " + id + ".");
			return;
			
		}
		
		for (Player player : Bukkit.getWorld("lobby").getPlayers()) {
			
			if (player.getGameMode() != GameMode.ADVENTURE) continue;
			
			ArenaUtils.placeItems(player);
		
		}
		
		PlayerUtils.removeHotbar(id);
		
	}
	
	public static String getId(String name) {
		
		for (String id : getArenas()) {
			
			if (!getName(id).equals(name)) continue;
				
			return id;
			
		}
		
		return null;
		
	}
	
	public static void joinArena(Player player, String id) {
		
		if (!ArenaUtils.isEnabled(id)) {
			
			player.sendMessage(Main.PREFIX + "The arena '§6" + ArenaUtils.getName(id) + "§7' is disabled.");
			return;
			
		}
		
		player.teleport(ScatterUtils.getSpawns(Bukkit.getWorld(ArenaUtils.getWorld(id)), ArenaUtils.getSize(id)));
		player.setGameMode(GameMode.SURVIVAL);
		PlayerUtils.clearInventory(player);
		PlayerUtils.clearXp(player);
		PlayerUtils.feed(player);
		PlayerUtils.heal(player);
		PlayerUtils.clearEffects(player);
		player.setExp(0);
		player.setTotalExperience(0);
		DisplayUtils.sendActionBar(player, "");
		
		String kitRaw = "";
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet arena = sql.executeQuery("SELECT kit FROM arena_arenas WHERE id = '" + id + "';");
			
			if (arena.next()) kitRaw = arena.getString("kit");
			
			sql.close();
			arena.close();
			
		
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to get kit of arena " + id + ".");
			
		}
		
		String[] kit = kitRaw.split(";");
		String hotbar = PlayerUtils.getHotbar(player);
		
		for (String arena : hotbar.split("%")) {
			
			if (!arena.split("#")[0].equalsIgnoreCase(id)) continue;
				
			kit = arena.split("#")[1].split(";");
			break;
			
		}
		
		for (int i = 0; i < kit.length; i++) {
			
			ItemStack item = ItemsUtils.unserializeItemStack(kit[i]);
			player.getInventory().setItem(i, item);
			
		}
		
		Arena.nodamages.add(player);
		DisplayUtils.sendTitle(player, "", "§7Teaming §cnot allowed", 0, 35, 5);
		
		new BukkitRunnable() {
			
			public void run() {
				
				ArenaUtils.updateCompass(player);
				
			}
			
		}.runTaskLater(Main.arena, 20);
		
		new BukkitRunnable() {
			
			public void run() {
				
				Arena.nodamages.remove(player);
				
			}
			
		}.runTaskLater(Main.arena, 60);
		
	}
	
	public static void saveItems(String id, ItemStack[] items) {
		
		String itemsString = "";
		
		for (int i = 0; i < items.length; i++) {
			
			itemsString += ItemsUtils.serializeItemStack(items[i]) + ";";
			
		}
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_arenas SET kit = '" + itemsString + "' WHERE id = '" + id + "';");
			sql.close();
		
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to update world of arena " + id + ".");
			
		}
		
	}
	
	public static void placeItems (Player player) {
		
		PlayerUtils.clearInventory(player);
		
		for (String id : ArenaUtils.getArenas()) {
			
			ItemStack item = ItemsUtils.createItem(Material.DIAMOND_SWORD, ArenaUtils.getName(id) + " §8(§a" + Bukkit.getWorld(ArenaUtils.getWorld(id)).getPlayers().size() + " §7players§8)", 1, 0);
			item = ItemsUtils.addGlow(item);
			item = ItemsUtils.hideFlags(item);
			
			player.getInventory().setItem(ArenaUtils.getSlot(id), item);
			
		}
		
	}
	
	public static void updateNames (Player player) {
		
		for (String id : ArenaUtils.getArenas()) {
			
			if (player.getInventory().getItem(ArenaUtils.getSlot(id)) == null) continue;
				
			ItemMeta meta = player.getInventory().getItem(ArenaUtils.getSlot(id)).getItemMeta();
			meta.setDisplayName(ArenaUtils.getName(id) + " §8(§a" + Bukkit.getWorld(ArenaUtils.getWorld(id)).getPlayers().size() + " §7players§8)");
			player.getInventory().getItem(ArenaUtils.getSlot(id)).setItemMeta(meta);
			
		}
		
	}
	
	public static void enable (String id) {
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_arenas SET state = 1 WHERE id = '" + id + "';");
			sql.close();
		
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to enable arena " + id + ".");
			
		}
		
	}
	
	public static void disable (String id) {
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_arenas SET state = 0 WHERE id = '" + id + "';");
			sql.close();
		
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to disable arena " + id + ".");
			
		}
		
		for (Player player : Bukkit.getWorld(ArenaUtils.getWorld(id)).getPlayers()) {
			
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
			
		}
		
	}
	
	public static Boolean isEnabled (String id) {
		
		Boolean enabled = false;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet arena = sql.executeQuery("SELECT state FROM arena_arenas WHERE id = '" + id + "';");
			
			if (arena.next()) enabled = arena.getInt("state") == 1 ? true : false;
			
			sql.close();
			arena.close();
			
		
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to get state of arena " + id + ".");
			
		}
		
		return enabled;
		
	}
	
	public static int getSize (String id) {
		
		int size = 100;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet arena = sql.executeQuery("SELECT size FROM arena_arenas WHERE id = '" + id + "';");
			
			if (arena.next()) size = arena.getInt("size");
			
			sql.close();
			arena.close();
			
		
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to get size of arena " + id + ".");
			
		}
		
		return size;
		
	}
	
	public static void setSize (String id, int size) {
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_arenas SET size = " + size + " WHERE id = '" + id + "';");
			sql.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to set size of arena " + id + ".");
			
		}
		
	}
	
	public static Boolean getUhc (String id) {
		
		Boolean uhc = true;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet arena = sql.executeQuery("SELECT uhc FROM arena_arenas WHERE id = '" + id + "';");
			
			if (arena.next()) uhc = arena.getInt("uhc") == 1 ? true : false;
			
			sql.close();
			arena.close();
			
		
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to get uhc state of arena " + id + ".");
			
		}
		
		return uhc;
		
	}
	
	public static void setUhc (String id, Boolean state) {
		
		int number = state ? 1 : 0;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_arenas SET uhc = " + number + " WHERE id = '" + id + "';");
			sql.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to enable UHC in arena " + id + ".");
			
		}
		
	}
	
	public static Boolean getMeleefun (String id) {
		
		Boolean meleefun = false;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet arena = sql.executeQuery("SELECT meleefun FROM arena_arenas WHERE id = '" + id + "';");
			
			if (arena.next()) meleefun = arena.getInt("meleefun") == 1 ? true : false;
			
			sql.close();
			arena.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to get meleefun state of arena " + id + ".");
			
		}
		
		return meleefun;
		
	}
	
	public static void setMeleefun (String id, Boolean state) {
		
		int number = state ? 1 : 0;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_arenas SET meleefun = " + number + " WHERE id = '" + id + "';");
			sql.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[ArenaUtils] Error to enable meleefun in arena " + id + ".");
			
		}
		
	}
	
	public static void updateCompass(Player player) {
		
		if (player.getWorld().getPlayers().size() <= 1) return;
				
		double nearestSize = (double) WorldUtils.getSize(player.getWorld().getName());
		Player nearestPlayer = null;
		
		for (Player potential : player.getWorld().getPlayers()) {
			
			if (potential == player) continue;
				
			if (player.getLocation().distance(potential.getLocation()) >= nearestSize) continue;
					
			nearestSize = player.getLocation().distance(potential.getLocation());
			nearestPlayer = potential;
			
		}
		
		player.setCompassTarget(nearestPlayer.getLocation());
		DisplayUtils.sendActionBar(player, "§7Nearest player is '§6" + nearestPlayer.getName() + "§7' §8(§a" + (int) nearestSize + "§7 blocks§8)");
		
	}
	
} 
