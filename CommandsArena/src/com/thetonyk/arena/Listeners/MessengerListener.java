package com.thetonyk.arena.Listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class MessengerListener implements PluginMessageListener {
	
	public static Map<String, Integer> playerCount = new HashMap<String, Integer>();

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals("BungeeCord")) return;
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		
		if (subchannel.equals("PlayerCount")) {
			
			String server = in.readUTF();
			int count = in.readInt();
			
			playerCount.put(server, count);
			
		}
		
	}
	
}
