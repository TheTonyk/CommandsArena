package com.thetonyk.arena.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.NameTagVisibility;

import com.thetonyk.arena.Arena;
import com.thetonyk.arena.Main;

public class PlayerUtils {
	
	public static void clearInventory(Player player) {
		
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);
        player.setItemOnCursor(new ItemStack(Material.AIR));
        
        if (player.getOpenInventory().getType() == InventoryType.CRAFTING) player.getOpenInventory().getTopInventory().clear();
        
    }
	
	public static void clearXp(Player player) {
	
		player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0F);
		
	}
	
	public static void feed(Player player) {
		
        player.setSaturation(5.0F);
        player.setExhaustion(0F);
        player.setFoodLevel(20);
        
    }
	
	public static void heal(Player player) {
		
		player.setHealth(player.getMaxHealth());
		
	}
	
	public static void clearEffects(Player player) {
		
		Collection<PotionEffect> activeEffects = player.getActivePotionEffects();

        for (PotionEffect activeEffect : activeEffects) {
        	
            player.removePotionEffect(activeEffect.getType());
            
        }
		
	}
	
	public static Boolean isNew(Player player) {
		
		Boolean isNew = true;
		
		try {
		
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet name = sql.executeQuery("SELECT * FROM arena_scores WHERE id = '" + PlayerUtils.getId(player.getUniqueId()) + "';");
			
			if (name.next()) isNew = false;
			
			sql.close();
			name.close();
			
		} catch (SQLException exception) {

			Main.arena.getLogger().severe("[PlayerUtils] Error to check if player " + player.getName() + " is new on the arena.");
			
		}
		
		return isNew;
		
	}
	
	public static void joinUpdatePlayer(Player player) {
		
		if (isNew(player)) {
			
			try {
				
				Statement sql = DatabaseUtils.getConnection().createStatement();
				sql.executeUpdate("INSERT INTO arena_scores (`id`, `kills`, `death`, `longshot`, `killstreak`, `hotbar`) VALUES ('" + PlayerUtils.getId(player.getUniqueId()) + "', 0, 0, 0, 0, '');");
				sql.close();
				
			} catch (SQLException exception) {
				
				Main.arena.getLogger().severe("[PlayerUtils] Error to insert new player scores " + player.getName() + ".");
				
			}
			
		}
		
	}
	
	public static void leaveUpdatePlayer(Player player) {
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			
			if (online.getScoreboard().getTeam(player.getName()) == null) continue;
				
			online.getScoreboard().getTeam(player.getName()).unregister();
			
		}
		
	}
	
	public enum Rank {
		
		PLAYER("", "§7Player"), WINNER("§6Winner §8| ", "§6Winner"), FAMOUS("§bFamous §8| ", "§bFamous"), BUILDER("§2Build §8| ", "§2Builder"),STAFF("§cStaff §8| ", "§cStaff"), MOD("§9Mod §8| ", "§9Moderator"), ADMIN("§4Admin §8| ", "§4Admin"), FRIEND("§3Friend §8| ", "§3Friend"), HOST("§cHost §8| ", "§cHost"), ACTIVE_BUILDER("§2Build §8| ", "§2Builder");
		
		String prefix;
		String name;
		
		private Rank(String prefix, String name) {
			
			this.prefix = prefix;
			this.name = name;
			
		}
		
		public String getPrefix() {
			
			return prefix;
			
		}
		
		public String getName() {
			
			return name;
			
		}
		
	}
	
	public static void setRank(String player, Rank rank) {
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE users SET rank = '" + rank + "' WHERE name = '" + player + "';");
			sql.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to update rank of player " + player + ".");
			
		}
		
		if (Bukkit.getPlayer(player) != null && Bukkit.getPlayer(player).isOnline()) {
			
			PermissionsUtils.clearPermissions(Bukkit.getPlayer(player));
			PermissionsUtils.setPermissions(Bukkit.getPlayer(player));
			PermissionsUtils.updateBungeePermissions(Bukkit.getPlayer(player));
		
		}
		
	}
	
	public static Rank getRank(String player) {
		
		Rank rank = Rank.PLAYER;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT rank FROM users WHERE name = '" + player + "';");
			
			if (req.next()) rank = Rank.valueOf(req.getString("rank"));
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[PlayerUtils] Error to get the rank of player " + player + ".");
			
		}
		
		return rank;
		
	}
	
	public static String getRanks() {
		
		String list = "Availables ranks: §aplayer";
		
		for (Rank rank : Rank.values()) {
			
			if (rank.name().toLowerCase().equalsIgnoreCase("player")) continue;
				
			list = list + " §7| §a" + rank.name().toLowerCase();
			
		}
		
		list = list + "§7.";
		return list;
		
	}
	
	public static void updateNametag(String player) {
		
		for (Player players : Bukkit.getOnlinePlayers()) {
			
			if (players.getScoreboard().getTeam(Bukkit.getPlayer(player).getName()) == null) {
				
				players.getScoreboard().registerNewTeam(Bukkit.getPlayer(player).getName());
				players.getScoreboard().getTeam(Bukkit.getPlayer(player).getName()).setAllowFriendlyFire(true);
				players.getScoreboard().getTeam(Bukkit.getPlayer(player).getName()).setCanSeeFriendlyInvisibles(true);
				players.getScoreboard().getTeam(Bukkit.getPlayer(player).getName()).setDisplayName(player + " team");
				players.getScoreboard().getTeam(Bukkit.getPlayer(player).getName()).setNameTagVisibility(NameTagVisibility.ALWAYS);
				
			}
		
			players.getScoreboard().getTeam(Bukkit.getPlayer(player).getName()).setPrefix(getRank(Bukkit.getPlayer(player).getName()).getPrefix() + "§7");
			players.getScoreboard().getTeam(Bukkit.getPlayer(player).getName()).setSuffix("§f");
			players.getScoreboard().getTeam(Bukkit.getPlayer(player).getName()).addEntry(Bukkit.getPlayer(player).getName());
			
			if (Bukkit.getPlayer(player).getScoreboard().getTeam(players.getName()) == null) {
				
				Bukkit.getPlayer(player).getScoreboard().registerNewTeam(players.getName());
				Bukkit.getPlayer(player).getScoreboard().getTeam(players.getName()).setAllowFriendlyFire(true);
				Bukkit.getPlayer(player).getScoreboard().getTeam(players.getName()).setCanSeeFriendlyInvisibles(true);
				Bukkit.getPlayer(player).getScoreboard().getTeam(players.getName()).setDisplayName(players.getName() + " team");
				Bukkit.getPlayer(player).getScoreboard().getTeam(players.getName()).setNameTagVisibility(NameTagVisibility.ALWAYS);
				
			}
		
			Bukkit.getPlayer(player).getScoreboard().getTeam(players.getName()).setPrefix(getRank(players.getName()).getPrefix() + "§7");
			Bukkit.getPlayer(player).getScoreboard().getTeam(players.getName()).setSuffix("§f");
			Bukkit.getPlayer(player).getScoreboard().getTeam(players.getName()).addEntry(players.getName());
		
		}
		
	}
	
	public static Boolean exist(String name) {
		
		Boolean exist = false;
		
		try {
		
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT * FROM users WHERE name = '" + name + "';");
			
			if (req.next()) exist = true;
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[PlayerUtils] Error to check if player  " + name + " is new.");
			
		}
		
		return exist;
		
	}
	
	public static Boolean exist(UUID uuid) {
		
		Boolean exist = false;
		
		try {
		
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT * FROM users WHERE uuid='" + uuid.toString() + "';");
			
			if (req.next()) exist = true;
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[PlayerUtils] Error to check if player with UUID " + uuid.toString() + " is new.");
			
		}
		
		return exist;
		
	}
	
	public static int getId (String name) {
		
		int id = 0;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT id FROM users WHERE name ='" + name + "';");
			
			if (req.next()) id = req.getInt("id");
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[PlayerUtils] Error to get id of player " + name + ".");
			
		}
		
		return id;
		
	}
	
	public static int getId (UUID uuid) {
		
		int id = 0;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT id FROM users WHERE uuid ='" + uuid + "';");
			
			if (req.next()) id = req.getInt("id");
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[PlayerUtils] Error to get id of player with UUID " + uuid + ".");
			
		}
		
		return id;
		
	}
	
	public static void updateScoreboard (Player player) {
		
		for (String score : player.getScoreboard().getEntries()) {
			
			if (!score.startsWith(" ")) continue;
			
			player.getScoreboard().resetScores(score);
			
		}
		
		player.getScoreboard().getObjective("sidebar").getScore(" ").setScore(10);
		player.getScoreboard().getObjective("sidebar").getScore("  §6Kills §8⫸ §a" + Arena.scores.get(player)[0]).setScore(9);
		player.getScoreboard().getObjective("sidebar").getScore("  §6Death §8⫸ §a" + Arena.scores.get(player)[1]).setScore(8);
		
		double ratio = 0;
		if (Arena.scores.get(player)[1] > 0) ratio = (double) Arena.scores.get(player)[0] / (double) Arena.scores.get(player)[1];
		
		DecimalFormat formatRatio = new DecimalFormat("##.##");
		
		player.getScoreboard().getObjective("sidebar").getScore("  §6Ratio §8⫸ §a" + formatRatio.format(ratio)).setScore(7);
		player.getScoreboard().getObjective("sidebar").getScore("  ").setScore(6);
		player.getScoreboard().getObjective("sidebar").getScore("  §6Killstreak §8⫸ §a" + Arena.scores.get(player)[4]).setScore(5);
		player.getScoreboard().getObjective("sidebar").getScore("   ").setScore(4);
		
		DecimalFormat format = new DecimalFormat("##.#");
		
		player.getScoreboard().getObjective("sidebar").getScore("  §6Best Longshot §8⫸ §a" + format.format(Arena.scores.get(player)[3]) + "m").setScore(3);
		player.getScoreboard().getObjective("sidebar").getScore("  §6Best Killstreak §8⫸ §a" + Arena.scores.get(player)[2]).setScore(2);
		player.getScoreboard().getObjective("sidebar").getScore("    ").setScore(1);
		player.getScoreboard().getObjective("sidebar").getScore("  §b@CommandsPVP").setScore(0);
		
	}
	
	public static int getKills (Player player) {
		
		int kills = 0;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT kills FROM arena_scores WHERE id ='" + PlayerUtils.getId(player.getUniqueId()) + "';");
			
			if (req.next()) kills = req.getInt("kills");
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[PlayerUtils] Error to get kills of player " + player.getName() + ".");
			
		}
		
		return kills;
		
	}
	
	public static int getDeath (Player player) {
		
		int death = 0;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT death FROM arena_scores WHERE id ='" + PlayerUtils.getId(player.getUniqueId()) + "';");
			
			if (req.next()) death = req.getInt("death");
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[PlayerUtils] Error to get death of player " + player.getName() + ".");
			
		}
		
		return death;
		
	}
	
	public static int getKillstreak (Player player) {
		
		int killstreak = 0;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT killstreak FROM arena_scores WHERE id ='" + PlayerUtils.getId(player.getUniqueId()) + "';");
			
			if (req.next()) killstreak = req.getInt("killstreak");
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[PlayerUtils] Error to get killstreak of player " + player.getName() + ".");
			
		}
		
		return killstreak;
		
	}
	
	public static int getLongshot (Player player) {
		
		int longshot = 0;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT longshot FROM arena_scores WHERE id ='" + PlayerUtils.getId(player.getUniqueId()) + "';");
			
			if (req.next()) longshot = req.getInt("longshot");
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[PlayerUtils] Error to get longshot of player " + player.getName() + ".");
			
		}
		
		return longshot;
		
	}
	
	public static void setupScore (Player player) {
		
		Integer[] scores = new Integer[5];
		scores[0] = PlayerUtils.getKills(player);
		scores[1] = PlayerUtils.getDeath(player);
		scores[2] = PlayerUtils.getKillstreak(player);
		scores[3] = PlayerUtils.getLongshot(player);
		scores[4] = 0;
		Arena.scores.put(player, scores);
		
	}
	
	public static void updateScores (Player player) {
		
		try {
		
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_scores SET kills = " + Arena.scores.get(player)[0] + ", death = " + Arena.scores.get(player)[1] + ", killstreak = " + Arena.scores.get(player)[2] + ", longshot = " + Arena.scores.get(player)[3] + " WHERE id = '" + PlayerUtils.getId(player.getUniqueId()) + "';");
			sql.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to update scores of player " + player.getName() + ".");
			
		}
		
	}
	
	public static String getHotbar (Player player) {
		
		String hotbar = "";
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT hotbar FROM arena_scores WHERE id ='" + PlayerUtils.getId(player.getUniqueId()) + "';");
			
			if (req.next()) hotbar = req.getString("hotbar");
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Main.arena.getLogger().severe("[PlayerUtils] Error to get hotbar of player " + player.getName() + ".");
			
		}
		
		return hotbar;
		
	}
	
	public static void setHotbar (Player player, String hotbar) {
		
		try {
		
			Statement sql = DatabaseUtils.getConnection().createStatement();
			sql.executeUpdate("UPDATE arena_scores SET hotbar = '" + hotbar + "' WHERE id = '" + PlayerUtils.getId(player.getUniqueId()) + "';");
			sql.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to update hotbar of player " + player.getName() + ".");
			
		}
		
	}
	
	public static int getChatVisibility (Player player) {
		
		int chatVisibility = 0;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT chat FROM settings WHERE id = " + PlayerUtils.getId(player.getUniqueId()) + ";");
			
			if (req.next()) chatVisibility = req.getInt("chat");
			
			sql.close();
			req.close();

		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get chat setting of player " + player.getName() + ".");
			
		}
		
		return chatVisibility;
		
	}
	
	public static int getMentionsState (Player player) {
		
		int mentionsState = 0;
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT mentions FROM settings WHERE id = " + PlayerUtils.getId(player.getUniqueId()) + ";");
			
			if (req.next()) mentionsState = req.getInt("mentions");
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get mentions setting of player " + player.getName() + ".");
			
		}
		
		return mentionsState;
		
	}
	
	public static int getHighestY (int x, int z, World world) {
		
		for (int y = 255; y >= 0; y--) {
			
			if (world.getBlockAt(x, y, z).getType() == Material.AIR) continue;
				
			return y;
			
		}
		
		return 255;
		
	}
	
	public static void removeHotbar (String id) {
		
		try {
			
			Statement sql = DatabaseUtils.getConnection().createStatement();
			ResultSet req = sql.executeQuery("SELECT * FROM arena_scores;");
			
			while (req.next()) {
				
				String hotbar = "";
				
				for (String arena : req.getString("hotbar").split("%")) {
					
					if (arena.split("#")[0].equalsIgnoreCase(id)) continue;
						
					hotbar += arena + "%";
					
				}
				
				sql.executeUpdate("UPDATE arena_scores SET hotbar = '" + hotbar + "' WHERE id = " + req.getInt("id") + ";");
				
			}
			
			sql.close();
			req.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get scores of all players.");
			
		}
		
	}

}
