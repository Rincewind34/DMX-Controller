package de.rincewind.dmxc.common.util;

import java.io.File;
import java.io.IOException;

public class FileUtil {
	
	public static void setupFile(File file) {
		if (file.isDirectory()) {
			throw new RuntimeException("The file 'accounts.json' could not be created: Already a directory!");
		}
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
	
}
