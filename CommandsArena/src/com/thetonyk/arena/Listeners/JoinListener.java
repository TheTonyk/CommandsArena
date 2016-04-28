package com.thetonyk.arena.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.thetonyk.arena.Main;
import com.thetonyk.arena.Utils.ArenaUtils;
import com.thetonyk.arena.Utils.DisplayUtils;
import com.thetonyk.arena.Utils.PlayerUtils;
import com.thetonyk.arena.Utils.PermissionsUtils;

public class JoinListener implements Listener {
	
	@EventHandler
	public void onConnect(PlayerLoginEvent event) {
		
		PermissionsUtils.setPermissions(event.getPlayer());
		
		if (event.getResult() != Result.KICK_WHITELIST) return;
			
		if (event.getPlayer().isOp() || event.getPlayer().hasPermission("global.bypasswhitelist")) {
			
			event.allow();
			return;
			
		}
		
		event.setKickMessage("§8⫸ §7You are not whitelisted §8⫷");
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		event.setJoinMessage("§7[§a+§7] " + PlayerUtils.getRank(player.getName()).getPrefix() + "§7" + player.getName());
		
		if (player.isDead()) player.spigot().respawn();
		
		PlayerUtils.joinUpdatePlayer(player);
		
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
		
		if (player.hasPermission("global.fly")) player.setAllowFlight(true);
		
		new BukkitRunnable() {
			
			public void run() {
				
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				
				out.writeUTF("PlayerCount");
				out.writeUTF("arena1.8");
				
				player.sendPluginMessage(Main.arena, "BungeeCord", out.toByteArray());
				
			}
			
		}.runTaskLater(Main.arena, 1);
		
		new BukkitRunnable() {
			
			public void run() {
				
				DisplayUtils.sendTitle(player, "§aCommandsPVP", "§7Welcome on the §aUHC Arena §7⋯ §a" + MessengerListener.playerCount.get("arena1.8") + " §7players onlines", 1, 20, 10);
				
			}
			
		}.runTaskLater(Main.arena, 5);
		
	}

}
