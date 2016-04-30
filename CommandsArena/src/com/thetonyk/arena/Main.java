package com.thetonyk.arena;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import com.thetonyk.arena.Utils.WorldUtils;
import com.thetonyk.arena.Listeners.MessengerListener;
import com.thetonyk.arena.Commands.ArenaCommand;
import com.thetonyk.arena.Commands.BorderCommand;
import com.thetonyk.arena.Commands.ButcherCommand;
import com.thetonyk.arena.Commands.CheckCommand;
import com.thetonyk.arena.Commands.ClearCommand;
import com.thetonyk.arena.Commands.FlyCommand;
import com.thetonyk.arena.Commands.GamemodeCommand;
import com.thetonyk.arena.Commands.HotbarCommand;
import com.thetonyk.arena.Commands.LagCommand;
import com.thetonyk.arena.Commands.LeaveCommand;
import com.thetonyk.arena.Commands.PregenCommand;
import com.thetonyk.arena.Commands.RankCommand;
import com.thetonyk.arena.Commands.WhitelistCommand;
import com.thetonyk.arena.Commands.WorldCommand;
import com.thetonyk.arena.Listeners.ChatListener;
import com.thetonyk.arena.Listeners.EnvironmentListener;
import com.thetonyk.arena.Listeners.InventoryListener;
import com.thetonyk.arena.Listeners.JoinListener;
import com.thetonyk.arena.Listeners.LeaveListener;
import com.thetonyk.arena.Listeners.PlayerListener;
import com.thetonyk.arena.Utils.ArenaUtils;
import com.thetonyk.arena.Utils.DisplayUtils;
import com.thetonyk.arena.Utils.ItemsUtils;
import com.thetonyk.arena.Utils.PermissionsUtils;
import com.thetonyk.arena.Utils.PlayerUtils;

public class Main extends JavaPlugin {
	
	public static Main arena;
	
	public static final String NO_PERMS = "§fUnknown command.";
	public static final String PREFIX = "§a§lArena §8⫸ §7";
	
	@Override
	public void onEnable() {
		
		getLogger().info("Arena UHC Plugin has been enabled.");
		getLogger().info("Plugin by TheTonyk for CommandsPVP");
		
		arena = this;
		
		WorldUtils.loadAllWorlds();
		DisplayUtils.redditHearts();
		Bukkit.clearRecipes();
		
		for (String id : ArenaUtils.getArenas()) {
			
			Arena.meleefun.put(id,(ArenaUtils.getMeleefun(id)));
			
		}
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "CommandsBungee");
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			PermissionsUtils.clearPermissions(player);
			PermissionsUtils.setPermissions(player);
			PermissionsUtils.updateBungeePermissions(player);
			
		}
		
		Main.setRecipes();
		
		this.getCommand("world").setExecutor(new WorldCommand());
		this.getCommand("whitelist").setExecutor(new WhitelistCommand());
		this.getCommand("rank").setExecutor(new RankCommand());
		this.getCommand("pregen").setExecutor(new PregenCommand());
		this.getCommand("border").setExecutor(new BorderCommand());
		this.getCommand("butcher").setExecutor(new ButcherCommand());
		this.getCommand("clear").setExecutor(new ClearCommand());
		this.getCommand("gamemode").setExecutor(new GamemodeCommand());
		this.getCommand("arena").setExecutor(new ArenaCommand());
		this.getCommand("lag").setExecutor(new LagCommand());
		this.getCommand("fly").setExecutor(new FlyCommand());
		this.getCommand("leave").setExecutor(new LeaveCommand());
		this.getCommand("hotbar").setExecutor(new HotbarCommand());
		this.getCommand("check").setExecutor(new CheckCommand());
		
		PluginManager manager = Bukkit.getPluginManager();
		
		manager.registerEvents(new JoinListener(), this);
		manager.registerEvents(new LeaveListener(), this);
		manager.registerEvents(new PlayerListener(), this);
		manager.registerEvents(new ChatListener(), this);
		manager.registerEvents(new EnvironmentListener(), this);
		manager.registerEvents(new InventoryListener(), this);
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessengerListener());
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			player.getScoreboard().registerNewObjective("below", "dummy");
			player.getScoreboard().getObjective("below").setDisplaySlot(DisplaySlot.BELOW_NAME);
			player.getScoreboard().getObjective("below").setDisplayName("§4♥");
			player.getScoreboard().registerNewObjective("list", "dummy");
			player.getScoreboard().getObjective("list").setDisplaySlot(DisplaySlot.PLAYER_LIST);
			player.getScoreboard().registerNewObjective("sidebar", "dummy");
			player.getScoreboard().getObjective("sidebar").setDisplaySlot(DisplaySlot.SIDEBAR);
			player.getScoreboard().getObjective("sidebar").setDisplayName(Main.PREFIX + "§oStats");
			
			PlayerUtils.setupScore(player);	
			PlayerUtils.updateScoreboard(player);	
			PlayerUtils.updateNametag(player.getName());
			
		}
		
		new BukkitRunnable() {
			
			public void run() {
				
				for (Player scoreboard : Bukkit.getOnlinePlayers()) {
				
					for (Player player : Bukkit.getOnlinePlayers()) {
						
						scoreboard.getScoreboard().getObjective("below").getScore(player.getName()).setScore((int) (((player.getHealth()) / 2) * 10));
						scoreboard.getScoreboard().getObjective("list").getScore(player.getName()).setScore((int) (((player.getHealth()) / 2) * 10));
						
					}
					
				}
				
			}
			
		}.runTaskTimer(Main.arena, 1, 1);
		
		new BukkitRunnable() {
			
			public void run() {
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					if (player.getWorld().getName().equalsIgnoreCase("lobby") && player.getGameMode() == GameMode.ADVENTURE) ArenaUtils.updateNames(player);
					
					DisplayUtils.sendTab(player);
					
				}
				
			}
			
		}.runTaskTimer(Main.arena, 1, 20);
		
		new BukkitRunnable() {
			
			public void run() {
				
				for (Player player : Bukkit.getWorld("lobby").getPlayers()) {
					
					DisplayUtils.sendActionBar(player, "§7Use §a/hotbar §7to customize your kit");
					
				}
				
			}
			
		}.runTaskTimer(Main.arena, 1, 40);
		
		new BukkitRunnable() {
			
			public void run() {

				for (Player player : Bukkit.getOnlinePlayers()) {
					
					if (player.getWorld().getName().equalsIgnoreCase("lobby")) continue;
					
					ArenaUtils.updateCompass(player);
					
				}

			}
			
		}.runTaskTimer(Main.arena, 1, 600);
			
		new BukkitRunnable() {
			
			public void run() {

				for (String id : ArenaUtils.getArenas()) {
					
					if (Bukkit.getWorld(ArenaUtils.getWorld(id)).getPlayers().size() < 5) {
						
						ArenaUtils.setSize(id, 100);
						continue;
						
					}
					
					if ((Bukkit.getWorld(ArenaUtils.getWorld(id)).getPlayers().size() * 25) < WorldUtils.getSize(ArenaUtils.getWorld(id))) {
						
						ArenaUtils.setSize(id, (Bukkit.getWorld(ArenaUtils.getWorld(id)).getPlayers().size() * 25));
						continue;
						
					}
					
					ArenaUtils.setSize(id, WorldUtils.getSize(ArenaUtils.getWorld(id)));
					
				}

			}
			
		}.runTaskTimer(Main.arena, 1, 6000);
		
		new BukkitRunnable() {
			
			public void run() {

				for (Location block : Arena.blocks) {
					
					block.getBlock().setType(Material.AIR);
					
				}
				
				for (Player player : Arena.playerBlocks.keySet()) {
					
					for (Location block : Arena.playerBlocks.get(player)) {
						
						block.getBlock().setType(Material.AIR);
						
					}
					
				}
				
				for (Location block : Arena.water) {
					
					block.getBlock().setType(Material.STATIONARY_WATER);
					
				}
				
				Arena.blocks.clear();
				Arena.playerBlocks.clear();
				Arena.water.clear();

			}
			
		}.runTaskTimer(Main.arena, 1, 12000);
		
		new BukkitRunnable() {
			
			public void run() {
				
				for (Player player : PlayerListener.cps.keySet()) {
					
					PlayerListener.cps.put(player, 0);
					
				}
				
			}
			
		}.runTaskTimer(Main.arena, 20, 20);
		
	}
	
	@Override
	public void onDisable() {
		
		getLogger().info("Arena UHC Plugin has been disabled.");
		
		for (Location block : Arena.blocks) {
			
			block.getBlock().setType(Material.AIR);
			
		}
		
		for (Player player : Arena.playerBlocks.keySet()) {
			
			for (Location block : Arena.playerBlocks.get(player)) {
				
				block.getBlock().setType(Material.AIR);
				
			}
			
		}
		
		for (Location block : Arena.water) {
			
			block.getBlock().setType(Material.STATIONARY_WATER);
			
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			PlayerUtils.updateScores(player);
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			
		}
		
		Arena.scores.clear();
		Arena.blocks.clear();
		Arena.playerBlocks.clear();
		Arena.water.clear();
		
		arena = null;
		
	}
	
	private static void setRecipes() {
		
		for (int i = 0; i < TreeSpecies.values().length; i++) {
			
			for (int y = 0; y < TreeSpecies.values().length; y++) {
				
				ShapelessRecipe stick = new ShapelessRecipe(ItemsUtils.createItem(Material.STICK, "§b§lCommandsPVP §r§6Stick", 4, 0));
				stick.addIngredient(new ItemStack(Material.WOOD, (short) i).getData());
				stick.addIngredient(new ItemStack(Material.WOOD, (short) y).getData());
				Bukkit.addRecipe(stick);
			
			}
			
		}
		
		ShapedRecipe diamondSword1 = new ShapedRecipe(ItemsUtils.createItem(Material.DIAMOND_SWORD, "§b§lCommandsPVP §r§6Sword", 1, 0));
		diamondSword1.shape("A  ", "A  ", "B  ");
		diamondSword1.setIngredient('A', Material.DIAMOND);
		diamondSword1.setIngredient('B', Material.STICK);
		Bukkit.addRecipe(diamondSword1);
		
		ShapedRecipe diamondSword2 = new ShapedRecipe(ItemsUtils.createItem(Material.DIAMOND_SWORD, "§b§lCommandsPVP §r§6Sword", 1, 0));
		diamondSword2.shape(" A ", " A ", " B ");
		diamondSword2.setIngredient('A', Material.DIAMOND);
		diamondSword2.setIngredient('B', Material.STICK);
		Bukkit.addRecipe(diamondSword2);
		
		ShapedRecipe diamondSword3 = new ShapedRecipe(ItemsUtils.createItem(Material.DIAMOND_SWORD, "§b§lCommandsPVP §r§6Sword", 1, 0));
		diamondSword3.shape("  A", "  A", "  B");
		diamondSword3.setIngredient('A', Material.DIAMOND);
		diamondSword3.setIngredient('B', Material.STICK);
		Bukkit.addRecipe(diamondSword3);
		
		ShapedRecipe diamondHelmet1 = new ShapedRecipe(ItemsUtils.createItem(Material.DIAMOND_HELMET, "§b§lCommandsPVP §r§6Helmet", 1, 0));
		diamondHelmet1.shape("AAA", "A A", "   ");
		diamondHelmet1.setIngredient('A', Material.DIAMOND);
		Bukkit.addRecipe(diamondHelmet1);
		
		ShapedRecipe diamondHelmet2 = new ShapedRecipe(ItemsUtils.createItem(Material.DIAMOND_HELMET, "§b§lCommandsPVP §r§6Helmet", 1, 0));
		diamondHelmet2.shape("   ", "AAA", "A A");
		diamondHelmet2.setIngredient('A', Material.DIAMOND);
		Bukkit.addRecipe(diamondHelmet2);
		
		ShapedRecipe diamondChesteplate = new ShapedRecipe(ItemsUtils.createItem(Material.DIAMOND_CHESTPLATE, "§b§lCommandsPVP §r§6Chestplate", 1, 0));
		diamondChesteplate.shape("A A", "AAA", "AAA");
		diamondChesteplate.setIngredient('A', Material.DIAMOND);
		Bukkit.addRecipe(diamondChesteplate);
		
		ShapedRecipe diamondLeggings = new ShapedRecipe(ItemsUtils.createItem(Material.DIAMOND_LEGGINGS, "§b§lCommandsPVP §r§6Leggings", 1, 0));
		diamondLeggings.shape("AAA", "A A", "A A");
		diamondLeggings.setIngredient('A', Material.DIAMOND);
		Bukkit.addRecipe(diamondLeggings);
		
		ShapedRecipe diamondBoots1 = new ShapedRecipe(ItemsUtils.createItem(Material.DIAMOND_BOOTS, "§b§lCommandsPVP §r§6Boots", 1, 0));
		diamondBoots1.shape("A A", "A A", "   ");
		diamondBoots1.setIngredient('A', Material.DIAMOND);
		Bukkit.addRecipe(diamondBoots1);

		ShapedRecipe diamondBoots2 = new ShapedRecipe(ItemsUtils.createItem(Material.DIAMOND_BOOTS, "§b§lCommandsPVP §r§6Boots", 1, 0));
		diamondBoots2.shape("   ", "A A", "A A");
		diamondBoots2.setIngredient('A', Material.DIAMOND);
		Bukkit.addRecipe(diamondBoots2);
		
	}
	
}
