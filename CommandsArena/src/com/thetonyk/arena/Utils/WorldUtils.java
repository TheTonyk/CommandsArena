package com.thetonyk.arena.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import com.thetonyk.arena.Main;
import com.thetonyk.arena.Utils.FileUtils;

public class WorldUtils {

	public static void loadWorld(String world) {
			
		Environment environment = Environment.NORMAL;
		long seed = -89417720380802761l;
		WorldType type = WorldType.NORMAL;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet worldDB = sql.executeQuery("SELECT * FROM arena_worlds WHERE name='" + world + "';");
			
			if (worldDB.next()) {
			
				environment = Environment.valueOf(worldDB.getString("environment"));
				seed = worldDB.getLong("seed");
				type = WorldType.valueOf(worldDB.getString("type"));
			
			}
			
			sql.close();
			worldDB.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[WorldUtils] Error to fetch informations of world " + world + " in DB.");
			return;
			
		}
		
		WorldCreator worldCreator = new WorldCreator(world);
		worldCreator.environment(environment);
		worldCreator.generateStructures(true);
		worldCreator.generatorSettings("{\"useCaves\":false,\"useStrongholds\":false,\"useVillages\":false,\"useMineShafts\":false,\"useTemples\":false,\"useRavines\":false,\"useMonuments\":false,\"graniteSize\":1,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":0,\"dioriteSize\":1,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":0,\"andesiteSize\":1,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":0,\"useLavaLakes\":true}");
		worldCreator.seed(seed);
		worldCreator.type(type);
		
		World newWorld = worldCreator.createWorld();
		newWorld.setDifficulty(Difficulty.HARD);
		newWorld.save();
		
	}
	
	public static void unloadWorld(String world) {
		
		try {
			
			Bukkit.unloadWorld(world, true);
			
		} catch (Exception exception) {
			
			Main.arena.getLogger().severe("[WorldUtils] Error to unload word " + world + ".");
			
		}
		
	}
	
	public static void deleteWorld(String world) {
				
		World oldWorld = Bukkit.getWorld(world);

		for (Player player : oldWorld.getPlayers()) {
			
			player.teleport(Bukkit.getWorld("lobby").getSpawnLocation().add(0.5, 0, 0.5));
			
		}
		
		unloadWorld(world);
		FileUtils.delete(oldWorld.getWorldFolder());
			
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("DELETE FROM arena_worlds WHERE name='" + world + "';");
			sql.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[WorldUtils] Error to delete world " + world + " in database.");
			
		}
		
	}
	

	public static Boolean exist(String world) {
		
		Boolean exist = false;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet name = sql.executeQuery("SELECT * FROM arena_worlds WHERE name='" + world + "';");
			
			if (name.next()) exist = true;
			
			sql.close();
			name.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[WorldUtils] Error to check if world " + world + " exist.");
			
		}
		
		return exist;
		
	}
	
	public static void loadAllWorlds() {

		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet worlds = sql.executeQuery("SELECT * FROM arena_worlds;");
			
			while (worlds.next()) {
				
				loadWorld(worlds.getString("name"));
				Bukkit.getWorld(worlds.getString("name")).setPVP(true);
				Bukkit.getWorld(worlds.getString("name")).setTime(6000);
				
			}
			
			sql.close();
			worlds.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[WorldUtils] Error to fetch all worlds.");
			
		}
		
		Bukkit.getWorld("lobby").setPVP(false);
		Bukkit.getWorld("lobby").setTime(18000);
		
	}
	
	public static void createWorld(String world, Environment environment, long seed, WorldType type, int radius) {
		
		WorldCreator worldCreator = new WorldCreator(world);
		
		worldCreator.environment(environment);
		worldCreator.generateStructures(true);
		worldCreator.generatorSettings("{\"useCaves\":false,\"useStrongholds\":false,\"useVillages\":false,\"useMineShafts\":false,\"useTemples\":false,\"useRavines\":false,\"useMonuments\":false,\"graniteSize\":1,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":0,\"dioriteSize\":1,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":0,\"andesiteSize\":1,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":0,\"useLavaLakes\":true}");
		worldCreator.seed(seed);
		worldCreator.type(type);
		
		World newWorld = worldCreator.createWorld();
		
		newWorld.setDifficulty(Difficulty.HARD);
		newWorld.setSpawnLocation(0, 200, 0);
		newWorld.getWorldBorder().setSize(radius);
		newWorld.getWorldBorder().setCenter(0, 0);
		newWorld.getWorldBorder().setDamageBuffer(0);
		newWorld.getWorldBorder().setDamageAmount(1);
		newWorld.getWorldBorder().setWarningDistance(15);
		newWorld.getWorldBorder().setWarningTime(1);
		newWorld.setGameRuleValue("spectatorsGenerateChunks", "false");
		newWorld.setGameRuleValue("mobGriefing", "false");
		newWorld.setGameRuleValue("doMobSpawning", "false");
		newWorld.setGameRuleValue("doMobLoot", "false");
		newWorld.setGameRuleValue("doFireTick", "false");
		newWorld.setGameRuleValue("doEntityDrops", "false");
		newWorld.setGameRuleValue("doDaylightCycle", "false");
		newWorld.setTime(6000);
		newWorld.save();
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("INSERT INTO arena_worlds (`name`, `environment`, `seed`, `type`, `size`) VALUES ('" + world + "', '" + environment.name() + "', '" + seed + "', '" + type.name() + "', '" + radius + "');");
			sql.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[WorldUtils] Error to insert new world " + world + ".");
			
		}
		
	}
	
	public static int getSize (String world) {
		
		int size = 100;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT size FROM arena_worlds WHERE name='" + world + "';");
			
			if (req.next()) size = req.getInt("size");
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[WorldUtils] Error to get size if world " + world + ".");
			
		}
		
		return size;
		
	}
	
}
