package com.thetonyk.arena.Listeners;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.arena.Arena;
import com.thetonyk.arena.Main;
import com.thetonyk.arena.Utils.ArenaUtils;
import com.thetonyk.arena.Utils.DisplayUtils;
import com.thetonyk.arena.Utils.ItemsUtils;
import com.thetonyk.arena.Utils.PlayerUtils;

public class PlayerListener implements Listener {
	
	public static List<Location> anvils = new ArrayList<Location>();
	public static Map<Player, Integer> cps = new HashMap<Player, Integer>();
	
	public static List<Block> getNearbyBlocks (Location location, int radius) {
		
        List<Block> blocks = new ArrayList<Block>();
        
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
        	
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
            	
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                	
                   blocks.add(location.getWorld().getBlockAt(x, y, z));
                   
                }
                
            }
            
        }
        
        return blocks;
        
    }
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
			Block block = event.getBlock();
			World world = block.getWorld();
		
			if (world.getName().equals("lobby") && event.getPlayer().hasPermission("global.build") && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
			
			if (event.getBlock().getType() == Material.LONG_GRASS || event.getBlock().getType() == Material.DOUBLE_PLANT) {
				
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				return;
				
			}
			
			if (!Arena.blocks.contains(event.getBlock().getLocation())) {
				
				event.setCancelled(true);
				return;
				
			}
			
			if (Arena.blocks.contains(event.getBlock().getLocation())) Arena.blocks.remove(event.getBlock().getLocation());
			
			for (Player player : Arena.playerBlocks.keySet()) {
				
				if (Arena.playerBlocks.get(player).contains(event.getBlock().getLocation())) Arena.playerBlocks.get(player).remove(event.getBlock().getLocation());
				
			}
			
			new BukkitRunnable() {
				
				public void run() {
					
					if (Arena.water.contains(event.getBlock().getLocation())) {
						
						event.getBlock().setType(Material.STATIONARY_WATER);
						Arena.water.remove(event.getBlock().getLocation());
						
					}
					
				}
				
			}.runTaskLater(Main.arena, 1);
			
	}
	
	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		
		if (event.getBlockClicked().getWorld().getName().equals("lobby") && event.getPlayer().hasPermission("global.build") && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		
		if (event.getBlockClicked().getWorld().getName().equals("lobby") && !event.getPlayer().hasPermission("global.build")) {
			
			event.setCancelled(true);
			return;
			
		}
		
		Location location = new Location(event.getBlockClicked().getWorld(), event.getBlockClicked().getX() + event.getBlockFace().getModX(), event.getBlockClicked().getY() + event.getBlockFace().getModY(), event.getBlockClicked().getZ() + event.getBlockFace().getModZ());
		
		if (event.getItemStack().getType() == Material.LAVA_BUCKET) {
			
			event.setCancelled(true);
			return;
			
		}
		
		if (!Arena.blocks.contains(location)) {
			
			event.setCancelled(true);
			return;
			
		}
		
		if (Arena.blocks.contains(location)) Arena.blocks.remove(location);
		
		for (Player player : Arena.playerBlocks.keySet()) {
			
			if (Arena.playerBlocks.get(player).contains(location)) Arena.playerBlocks.get(player).remove(location);
			
		}
		
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
	
		Block block = event.getBlock();
		World world = block.getWorld();
	
		if (world.getName().equals("lobby") && event.getPlayer().hasPermission("global.build") && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		
		if (event.getBlockReplacedState().getType() == Material.LAVA || event.getBlockReplacedState().getType() == Material.STATIONARY_LAVA) {
			
			event.setCancelled(true);
			return;
			
		}
		
		Arena.blocks.add(event.getBlock().getLocation());
		
		if (event.getBlockReplacedState().getType() == Material.STATIONARY_WATER || event.getBlockReplacedState().getType() == Material.WATER) Arena.water.add(event.getBlock().getLocation());
		
		if (!Arena.playerBlocks.containsKey(event.getPlayer())) Arena.playerBlocks.put(event.getPlayer(), new ArrayList<Location>());
		
		Arena.playerBlocks.get(event.getPlayer()).add(event.getBlock().getLocation());
		
		Location loc = event.getBlock().getLocation();
		
		for (int i = 0; i < 3; i++) {
 		
			for (Block nearBlock : PlayerListener.getNearbyBlocks(loc, 1)) {
				
				if (nearBlock.getType() != Material.AIR) {
				
					if (!Arena.blocks.contains(nearBlock.getLocation())) return;
					
				}
				
			}
			
			loc.setY(loc.getY() - 1);
		
		}
		
		for (Block nearBlock : PlayerListener.getNearbyBlocks(event.getBlock().getLocation(), 4)) {
			
			if (Arena.blocks.contains(nearBlock.getLocation())) {
				
				nearBlock.setType(Material.AIR);
				Arena.blocks.remove(nearBlock.getLocation());
				
			}
			
			for (Player player : Arena.playerBlocks.keySet()) {
				
				if (Arena.playerBlocks.get(player).contains(nearBlock.getLocation())) {
					
					nearBlock.setType(Material.AIR);
					Arena.playerBlocks.get(player).remove(nearBlock.getLocation());
					
				}
				
			}
			
			if (Arena.water.contains(nearBlock.getLocation())) {
				
				nearBlock.setType(Material.STATIONARY_WATER);
				Arena.water.remove(nearBlock.getLocation());
				
			}
			
		}
		
		return;
		
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		
		if (event.getBlockClicked().getWorld().getName().equals("lobby") && event.getPlayer().hasPermission("global.build") && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		
		if (event.getBlockClicked().getWorld().getName().equals("lobby") && !event.getPlayer().hasPermission("global.build")) {
			
			event.setCancelled(true);
			return;
			
		}
		
		if (event.getBlockClicked().getWorld().getBlockAt(new Location(event.getBlockClicked().getWorld(), event.getBlockClicked().getX() + event.getBlockFace().getModX(), event.getBlockClicked().getY() + event.getBlockFace().getModY(), event.getBlockClicked().getZ() + event.getBlockFace().getModZ())).getType() == Material.STATIONARY_LAVA || event.getBlockClicked().getWorld().getBlockAt(new Location(event.getBlockClicked().getWorld(), event.getBlockClicked().getX() + event.getBlockFace().getModX(), event.getBlockClicked().getY() + event.getBlockFace().getModY(), event.getBlockClicked().getZ() + event.getBlockFace().getModZ())).getType() == Material.LAVA || event.getBlockClicked().getWorld().getBlockAt(new Location(event.getBlockClicked().getWorld(), event.getBlockClicked().getX() + event.getBlockFace().getModX(), event.getBlockClicked().getY() + event.getBlockFace().getModY(), event.getBlockClicked().getZ() + event.getBlockFace().getModZ())).getType() == Material.WATER || event.getBlockClicked().getWorld().getBlockAt(new Location(event.getBlockClicked().getWorld(), event.getBlockClicked().getX() + event.getBlockFace().getModX(), event.getBlockClicked().getY() + event.getBlockFace().getModY(), event.getBlockClicked().getZ() + event.getBlockFace().getModZ())).getType() == Material.STATIONARY_WATER) {
			
			event.setCancelled(true);
			return;
			
		}
		
		Arena.blocks.add(new Location(event.getBlockClicked().getWorld(), event.getBlockClicked().getX() + event.getBlockFace().getModX(), event.getBlockClicked().getY() + event.getBlockFace().getModY(), event.getBlockClicked().getZ() + event.getBlockFace().getModZ()));
		
		if (!Arena.playerBlocks.containsKey(event.getPlayer())) Arena.playerBlocks.put(event.getPlayer(), new ArrayList<Location>());
		
		Arena.playerBlocks.get(event.getPlayer()).add(new Location(event.getBlockClicked().getWorld(), event.getBlockClicked().getX() + event.getBlockFace().getModX(), event.getBlockClicked().getY() + event.getBlockFace().getModY(), event.getBlockClicked().getZ() + event.getBlockFace().getModZ()));
		
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			
			if (cps.containsKey(event.getPlayer())) cps.put(event.getPlayer(), cps.get(event.getPlayer()) + 1);
			
		}
	
		if (!event.getPlayer().getWorld().getName().equals("lobby")) return;
			
		if (event.getAction() == Action.PHYSICAL) {
			
			event.setCancelled(true);
			return;
			
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().equals(Material.ANVIL)) {
			
			anvils.add(event.getClickedBlock().getLocation());
			return;
			
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
			
			if (!((Sign) event.getClickedBlock().getState()).getLine(0).isEmpty()) {
			
				if (((Sign) event.getClickedBlock().getState()).getLine(0).substring(4).equalsIgnoreCase("Names")) {
					
					if (event.getPlayer().getInventory().getItemInHand() != null) {
					
						ItemMeta meta = event.getPlayer().getInventory().getItemInHand().getItemMeta();
						
						if (meta.getDisplayName() == null) {
							
							DisplayUtils.sendTitle(event.getPlayer(), "", "§6Rename the item first!", 1, 5, 1);
							return;
							
						}
						
						meta.setDisplayName("§b§lCommandsPVP §r§6" + meta.getDisplayName());
						event.getPlayer().getInventory().getItemInHand().setItemMeta(meta);
						
					}
					
				}
				else if (((Sign) event.getClickedBlock().getState()).getLine(0).substring(4).equalsIgnoreCase("Save")) {
									
					if (InventoryListener.editKits.containsKey(event.getPlayer().getUniqueId())) {
						
						String id = InventoryListener.editKits.get(event.getPlayer().getUniqueId());
						InventoryListener.editKits.remove(event.getPlayer().getUniqueId());
						ArenaUtils.saveItems(id, (ItemStack[]) ArrayUtils.addAll(event.getPlayer().getInventory().getContents(), event.getPlayer().getInventory().getArmorContents()));
						PlayerUtils.removeHotbar(id);
						Bukkit.broadcastMessage(Main.PREFIX + "The kit of arena " + ArenaUtils.getName(id) + " §7was updated. You need to re-do your custom hotbar.");
						
					}
					
					event.getPlayer().setGameMode(GameMode.ADVENTURE);
					PlayerUtils.clearInventory(event.getPlayer());
					event.getPlayer().teleport(Bukkit.getWorld("lobby").getSpawnLocation().add(0.5, 0, 0.5));
					ArenaUtils.placeItems(event.getPlayer());
					
				}
				else if (((Sign) event.getClickedBlock().getState()).getLine(0).substring(4).equalsIgnoreCase("Unbreakable")) {
					
					if (event.getPlayer().getInventory().getItemInHand() != null) {
					
						ItemMeta meta = event.getPlayer().getInventory().getItemInHand().getItemMeta();
						meta.spigot().setUnbreakable(true);
						event.getPlayer().getInventory().getItemInHand().setItemMeta(meta);
						return;
						
					}
					
				}
				
			}
			
		}
		else if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem() != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
			
			for (String id : ArenaUtils.getArenas()) {
				
				if (!event.getItem().getItemMeta().getDisplayName().startsWith(ArenaUtils.getName(id))) continue;
					
				event.setCancelled(true);
				event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, 1);
				ArenaUtils.joinArena(event.getPlayer(), id);
				return;
				
			}
			
			return;
				
		}
		
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		
		if (event.getEntity() instanceof Player && Arena.nodamages.contains((Player) event.getEntity())) {
			
			event.setCancelled(true);
			
		}
		
		if (event.getEntity() instanceof Player || event.getEntity() instanceof Minecart ||event.getEntity() instanceof ArmorStand || event.getEntity() instanceof Painting || event.getEntity() instanceof ItemFrame) {
			
			if (!event.getEntity().getWorld().getName().equals("lobby")) return;
				
			event.setCancelled(true);
			
		}

	}
	
	@EventHandler
	public void onArmorStandInteract(PlayerArmorStandManipulateEvent event) {
		
			
		if (event.getRightClicked().getWorld().getName().equals("lobby") && !event.getPlayer().hasPermission("global.build") && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			
			event.setCancelled(true);
			
		}
		
	}
	
	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event) {
			
		event.setCancelled(true);
		event.setFoodLevel(20);
		
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onHeal(EntityRegainHealthEvent event) {
		
		if (!(event.getEntity() instanceof Player)) return;
			
		Boolean regen = false;
		
		for (String id : ArenaUtils.getArenas()) {
			
			if (ArenaUtils.getWorld(id).equalsIgnoreCase(event.getEntity().getWorld().getName())) regen = !ArenaUtils.getUhc(id);
			
		}
		
		if (!regen) {
		
			if (event.getRegainReason() == RegainReason.REGEN || event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.CUSTOM) event.setCancelled(true);
		
		}
			
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			
			if (!(((Arrow) event.getDamager()).getShooter() instanceof Player)) return;
				
			if (((Player) ((Arrow) event.getDamager()).getShooter()).getLocation().distance(event.getEntity().getLocation()) > 49.99) {
				
				DecimalFormat format = new DecimalFormat("##.##");
				Bukkit.broadcastMessage(Main.PREFIX + "The player '§6" + ((Player) ((Arrow) event.getDamager()).getShooter()).getName() + "§7' got a longshot of §a" + format.format(((Player) ((Arrow) event.getDamager()).getShooter()).getLocation().distance(event.getEntity().getLocation())) + "m§7.");
				
				Integer[] scores = Arena.scores.get((Player) ((Arrow) event.getDamager()).getShooter());
				if (((int) Math.floor(((Player) ((Arrow) event.getDamager()).getShooter()).getLocation().distance(event.getEntity().getLocation()))) > Arena.scores.get(((Player) ((Arrow) event.getDamager()).getShooter()))[3]) scores[3] = (int) Math.floor(((Player) ((Arrow) event.getDamager()).getShooter()).getLocation().distance(event.getEntity().getLocation()));
				Arena.scores.put((Player) ((Arrow) event.getDamager()).getShooter(), scores);
				
				PlayerUtils.updateScoreboard(((Player) ((Arrow) event.getDamager()).getShooter()));
				
			}
				
			new BukkitRunnable() {
				
				public void run() {
					
					double health = ((Player) event.getEntity()).getHealth();
					String display = "§4";
					int count = 0;
					
					for (int i = 0; i <= health; i++) {
						
						display = display + "❤";
						health = health - 1;
						count++;
						
					}
					
					if (health > 0) {
						
						display = display + "§c❤";
						count++;
						
					}
					
					display = display + "§f";
					
					while (count < 10) {
						
						display = display + "❤";
						count++;
						
					}
					
					NumberFormat format = new DecimalFormat("##.#");
					display = display +  " §7⫸ §6" + format.format(((((Player) event.getEntity()).getHealth()) / 2) * 10) + "%";
					DisplayUtils.sendActionBar((Player) (((Arrow) event.getDamager()).getShooter()), display);
					
				}
				
			}.runTaskLater(Main.arena, 1);
			
			return;
			
		}
		
		if (event.getEntity() instanceof EnderCrystal && event.getDamager() instanceof Player) {
			
			if (!event.getEntity().getWorld().getName().equalsIgnoreCase("lobby")) return;
				
			event.setCancelled(true);
			
			if (!event.getDamager().hasPermission("global.build")) return;
			
			event.getEntity().remove();
			
		}
		
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
			
			for (String id : ArenaUtils.getArenas()) {
				
				if (ArenaUtils.getWorld(id).equalsIgnoreCase(event.getEntity().getWorld().getName())) {
					
					if (Arena.meleefun.get(id)) break;
					else return;
					
				}
				
			}
			
			event.setDamage(event.getDamage() * 0.5);
			
			new BukkitRunnable() {
				
				public void run() {
					
					((Player) event.getEntity()).setNoDamageTicks(0);
					
				}
				
			}.runTaskLater(Main.arena, 1);
			
		}
		
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		
		if (event.getTo().getWorld().getName().equalsIgnoreCase("lobby") && event.getTo().getY() <= 0) event.getPlayer().teleport(event.getTo().getWorld().getSpawnLocation());
		
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		
		event.getDrops().clear();
		event.getDrops().add(ItemsUtils.createItem(Material.GOLDEN_APPLE, "§b§lCommandsPVP §r§6Golden Apple", 1, 0));
		event.getDrops().add(new ItemStack(Material.ARROW, 8));
		
		if (new Random().nextDouble() < 0.35) event.getDrops().add(new ItemStack(Material.INK_SACK, 1, (short) 4));
		if (new Random().nextDouble() < 0.20) event.getDrops().add(new ItemStack(Material.DIAMOND, 1));
		
		event.setDeathMessage(null);
		
		Integer[] scores = Arena.scores.get(event.getEntity());
		scores[1] = Arena.scores.get(event.getEntity())[1] + 1;
		if (Arena.scores.get(event.getEntity())[4] > Arena.scores.get(event.getEntity())[2]) scores[2] = Arena.scores.get(event.getEntity())[4];
		scores[4] = 0;
		Arena.scores.put(event.getEntity(), scores);
		 
		PlayerUtils.updateScoreboard(event.getEntity());
		
		if (event.getEntity().getKiller() != null && event.getEntity() != event.getEntity().getKiller()) {
			
			scores = Arena.scores.get(event.getEntity().getKiller());
			scores[0] = Arena.scores.get(event.getEntity().getKiller())[0] + 1;
			scores[4] = Arena.scores.get(event.getEntity().getKiller())[4] + 1;
			Arena.scores.put(event.getEntity().getKiller(), scores);
			
			PlayerUtils.updateScoreboard(event.getEntity().getKiller());
			
			NumberFormat format = new DecimalFormat("##.#");
			event.getEntity().getKiller().sendMessage(Main.PREFIX + "You killed '§6" + event.getEntity().getName() + "§7' §8(§7" + format.format((event.getEntity().getKiller().getHealth() / 2) * 10) + "%§8)");
			event.getEntity().sendMessage(Main.PREFIX + "You have been killed by '§6" + event.getEntity().getKiller().getName() + "§7' §8(§7" + format.format((event.getEntity().getKiller().getHealth() / 2) * 10) + "%§8)");
			event.getEntity().getKiller().playSound(event.getEntity().getKiller().getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
			event.getEntity().getKiller().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, true));
			event.getEntity().getKiller().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1, false, true));
			
			double health = event.getEntity().getKiller().getHealth() + 4;
			
			event.getEntity().getKiller().setLevel(event.getEntity().getKiller().getLevel() + 1);
			if (health > 20) health = 20;
			event.getEntity().getKiller().setHealth(health);
			
		}
		
		new BukkitRunnable() {
			
			public void run() {
				
				event.getEntity().spigot().respawn();
				
				if (Arena.playerBlocks.containsKey(event.getEntity())) {
					
					for (Location block : Arena.playerBlocks.get(event.getEntity())) {
						
						block.getBlock().setType(Material.AIR);
						
						if (Arena.blocks.contains(block)) Arena.blocks.remove(block);
						
						if (Arena.water.contains(block)) {
							
							block.getBlock().setType(Material.STATIONARY_WATER);
							Arena.water.remove(block);
							
						}
						
					}
					
					Arena.playerBlocks.get(event.getEntity()).clear();
					
				}
				
			}
			
		}.runTaskLater(Main.arena, 5);
	
	}
	
	@EventHandler
	public void onRespaw(PlayerRespawnEvent event) {
		
		new BukkitRunnable() {
			
			public void run() {
				
				event.setRespawnLocation(Bukkit.getWorld("lobby").getSpawnLocation().add(0.5, 0, 0.5));
				event.getPlayer().setGameMode(GameMode.ADVENTURE);
				
				PlayerUtils.clearInventory(event.getPlayer());
				PlayerUtils.clearXp(event.getPlayer());
				PlayerUtils.feed(event.getPlayer());
				PlayerUtils.heal(event.getPlayer());
				PlayerUtils.clearEffects(event.getPlayer());
				event.getPlayer().setExp(0);
				event.getPlayer().setTotalExperience(0);
				ArenaUtils.placeItems(event.getPlayer());
				
				if (event.getPlayer().hasPermission("global.fly")) {
					
					event.getPlayer().setAllowFlight(true);
					return;
					
				}
				
			}
			
		}.runTaskLater(Main.arena, 1);
		
	}
	
	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		
		if (event.getItem().getType() != Material.GOLDEN_APPLE) return;
			
		new BukkitRunnable() {
			
			public void run() {
				
				event.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);
				
			}
			
        }.runTask(Main.arena);
		
	}
	
	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent event) {
		
		if (event.getPlayer().getWorld().getName().equalsIgnoreCase("lobby") && event.getPlayer().hasPermission("global.fly")) {
			
			event.getPlayer().setAllowFlight(true);
			return;
			
		}
		
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
		
		event.getPlayer().setAllowFlight(false);
		event.getPlayer().setFlying(false);
		return;
		
	}
	
	@EventHandler
	public void onArrowHit(ProjectileHitEvent event) {
		
		if (!(event.getEntity() instanceof Arrow)) return;
		
		((Arrow) event.getEntity()).remove();
		
	}

}
