package com.thetonyk.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Arena {
	
	public static List<Location> blocks = new ArrayList<Location>();
	public static Map<Player, List<Location>> playerBlocks = new HashMap<Player, List<Location>>();
	public static List<Location> water = new ArrayList<Location>();
	public static Map<Player, Integer[]> scores = new HashMap<Player, Integer[]>();
	public static List<Player> nodamages = new ArrayList<Player>();
	public static Map<String, Boolean> meleefun = new HashMap<String, Boolean>();

}
