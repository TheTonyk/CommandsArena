package com.thetonyk.arena.Utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.thetonyk.arena.Main;
import com.thetonyk.arena.Utils.PlayerUtils.Rank;

public class PermissionsUtils {
	
	private static Map<String, PermissionAttachment> permissions = new HashMap<String, PermissionAttachment>();
	
	public static void setPermissions(Player player) {
		
		if (!permissions.containsKey(player.getName())) permissions.put(player.getName(), player.addAttachment(Main.arena));
		
		PermissionAttachment permission = permissions.get(player.getName());
		Rank rank = PlayerUtils.getRank(player.getName());
		
		if (rank == Rank.ADMIN) {
			
			player.setOp(true);
			return;
			
		}
		
		permission.setPermission("arena.lag", true);
		permission.setPermission("arena.leave", true);
		permission.setPermission("arena.hotbar", true);
		
		if (rank == Rank.PLAYER || rank == Rank.WINNER) return;
		
		permission.setPermission("global.fly", true);
		
		if (rank == Rank.FAMOUS || rank == Rank.FRIEND) return;
		
		permission.setPermission("global.bypasswhitelist", true);
		
		if (rank == Rank.BUILDER) return;
		
		permission.setPermission("global.build", true);
		permission.setPermission("global.gamemode", true);
		permission.setPermission("parkour.jump", true);
		
		if (rank == Rank.ACTIVE_BUILDER) return;
		
		permission.setPermission("global.gamemode", false);
		permission.setPermission("global.build", false);
		permission.setPermission("parkour.jump", false);
		
		if (rank == Rank.HOST) return;

		permission.setPermission("arena.check", true);
		
		if (rank == Rank.MOD) return;
		
		permission.setPermission("arena.warning", true);
		
	}
	
	public static void clearPermissions(Player player) {
		
		if (permissions.containsKey(player.getName())) {
			
			try {
				
				player.removeAttachment(permissions.get(player.getName()));
				
			} catch (Exception exception) {
				
				Main.arena.getLogger().severe("[PermissionsUtils] Error to clear permissions of player " + player.getName() + ".");
				
			}
			
			permissions.remove(player.getName());
			
		}
		
	}
	
	public static void updateBungeePermissions(Player player) {
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		out.writeUTF("updatePermissions");
		out.writeUTF(player.getUniqueId().toString());
		
		player.sendPluginMessage(Main.arena, "CommandsBungee", out.toByteArray());
		
	}

}
