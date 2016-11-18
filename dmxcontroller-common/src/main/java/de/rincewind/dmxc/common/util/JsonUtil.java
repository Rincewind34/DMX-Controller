package de.rincewind.dmxc.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class JsonUtil {
	
	private static Gson gson;
	
	static {
		JsonUtil.gson = new GsonBuilder().setPrettyPrinting().create();
	}
	
	public static <T> T fromJson(File file, Class<T> jsonClass) {
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			T object = JsonUtil.gson.fromJson(reader, jsonClass);
			
			fileReader.close();
			reader.close();
			return object;
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
		return null;
	}
	
	public static void toJson(File file, JsonElement json) {
		FileWriter writer = null;
		
		try {
			writer = new FileWriter(file);
			writer.write(JsonUtil.gson.toJson(json));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
	
}
