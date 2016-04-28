package com.thetonyk.arena.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

public class ItemsUtils {
	
	public static ItemStack createItem(Material material, String name, int number, int damage) {
		
		ItemStack item = new ItemStack(material, number, (short) damage);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		
		return item;
		
	}
	
	public static ItemStack createItem(Material material, String name, int number, int damage, ArrayList<String> lore) {
		
		ItemStack item = new ItemStack(material, number, (short) damage);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
		
	}
	
	public static ItemStack addGlow(ItemStack item) {
		
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        
        if (!nmsStack.hasTag()) {
        	
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
            
        }
        
        if (tag == null) tag = nmsStack.getTag();
        
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
        
    }
	
	public static ItemStack hideFlags(ItemStack item) {
		
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(meta);
		return item;
		
	}
	
	public static ItemStack getSkull(String name, String texture) {
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		
		if (texture.isEmpty()) return head;
		
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("texture", new String(texture)));
		Field profileField = null;
		
		try {
			
			profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
			
			exception.printStackTrace();
			
		}
		
		headMeta.setDisplayName(name);
		head.setItemMeta(headMeta);
		return head;
		
	}
	
	public static String serializeItemStack (ItemStack originalItem) {
		
		List<Map<String, Object>> serialized = new ArrayList<Map<String, Object>>();
		
		ItemStack item = new ItemStack(Material.AIR);
		
		if (originalItem != null) item = originalItem.clone();
		
		Map<String, Object> serializedMeta = (item.hasItemMeta()) ? item.getItemMeta().serialize() : null;
		item.setItemMeta(null);
		Map<String, Object> serializedItem = item.serialize();
		
		serialized.add(serializedItem);
		serialized.add(serializedMeta);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		return gson.toJson(serialized);
		
	}
	
	public static ItemStack unserializeItemStack (String serialized) {
		
		List<Map<String, Object>> serializedItemStack = new Gson().fromJson(serialized, new TypeToken<List<Map<String, Object>>>(){}.getType());
		
		// Gson#fromJson change integer to double for strange reasons and ConfigurationSerialization#deserializeObject can't recognize the double value of key "repair-cost".
		// I don't need the repair cost, so i simply remove it. We can change it to integer if we need it.
		
		for (int i = 0; i < serializedItemStack.size(); i++) {
			
			if (serializedItemStack.get(i) == null) continue;
			
			Boolean exist = false;
			
			for (String key : serializedItemStack.get(i).keySet()) {
				
				if (key.equalsIgnoreCase("repair-cost")) exist = true;
				else if (key.equalsIgnoreCase("enchants")) {
					
					@SuppressWarnings("unchecked")
					LinkedTreeMap<String, Object> enchants = (LinkedTreeMap<String, Object>) serializedItemStack.get(i).get(key);
					Map<String, Object> temp = new HashMap<String, Object>();
					
					for (String keyEnchants : enchants.keySet()) {
						
						Double d = new Double((double) enchants.get(keyEnchants));
						temp.put(keyEnchants, d.intValue());
						
					}
					
					enchants.clear();
					
					for (String keyTemp : temp.keySet()) {
						
						enchants.put(keyTemp, temp.get(keyTemp));
						
					}
					
				}
				
			}
			
			if (exist) serializedItemStack.get(i).remove("repair-cost");
			
		}
		
		ItemStack item = ItemStack.deserialize(serializedItemStack.get(0));
		
		if (serializedItemStack.get(1) != null) {
			
			ItemMeta meta = (ItemMeta) ConfigurationSerialization.deserializeObject(serializedItemStack.get(1), ConfigurationSerialization.getClassByAlias("ItemMeta"));
			item.setItemMeta(meta);
			
		}
		
		return item;
		
	}

}
