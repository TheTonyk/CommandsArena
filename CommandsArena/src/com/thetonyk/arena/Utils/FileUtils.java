package com.thetonyk.arena.Utils;

import java.io.File;

import com.thetonyk.arena.Main;

public class FileUtils {
	
	public static boolean delete(File file) {
		
		if (file.exists()) {
			
			File[] files = file.listFiles();
			
			for (int i = 0; i < files.length; i++) {
				
				if (files[i].isDirectory()) delete(files[i]);
				else files[i].delete();
				
			}
			
		}
		
		if (file.isDirectory()) {

			      if (file.list().length > 0) Main.arena.getLogger().severe("[FileUtils] The folder " + file.getPath() + " is not empty!");
			      
		}
		
		return (file.delete());
		
	}

}
