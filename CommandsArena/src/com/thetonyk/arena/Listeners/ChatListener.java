package com.thetonyk.arena.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.arena.Main;
import com.thetonyk.arena.Utils.PlayerUtils;

public class ChatListener implements Listener {

	List<Player> cooldown = new ArrayList<Player>();
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		
		Player player = event.getPlayer();
		
		if (cooldown.contains(player)) {
			
			event.setCancelled(true);
			return;
			
		}
		
		Player[] receivers = event.getRecipients().toArray(new Player[0]);
		
		for (int i = 0; i < receivers.length; i++) {
			
			if (PlayerUtils.getChatVisibility(receivers[i]) == 0) event.getRecipients().remove(receivers[i]);
			
			if (PlayerUtils.getChatVisibility(receivers[i]) == 1 && event.getMessage().contains(receivers[i].getName()) && PlayerUtils.getMentionsState(receivers[i]) == 1) {
				
				if (event.getRecipients().contains(receivers[i])) event.getRecipients().remove(receivers[i]);
				
				StringBuilder message = new StringBuilder();
				
				for (int j = 0; j < event.getMessage().split(" ").length; j++) {
					
					if (event.getMessage().split(" ")[j].equalsIgnoreCase(receivers[i].getName())) message.append("§a§l" + receivers[i].getName() + "§f ");
					else message.append(event.getMessage().split(" ")[j] + " ");
					
				}
				
				receivers[i].sendMessage(PlayerUtils.getRank(player.getName()).getPrefix() + "§7" + player.getName() + " §8⫸ §f" + message);
				receivers[i].playSound(receivers[i].getLocation(), Sound.ORB_PICKUP, 1, 1);
				
			}
			
		}
		
		event.setFormat(PlayerUtils.getRank(player.getName()).getPrefix() + "§7" + player.getName() + " §8⫸ §f%2$s");
		cooldown.add(player);
		
		new BukkitRunnable() {
			
			public void run() {
				
				cooldown.remove(player);
				
			}
			
		}.runTaskLater(Main.arena, 40);
		
	}
			
}
