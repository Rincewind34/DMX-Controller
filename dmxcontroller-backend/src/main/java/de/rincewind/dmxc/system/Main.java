package de.rincewind.dmxc.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.system.commands.CommandAccounts;
import de.rincewind.dmxc.system.commands.CommandDMXValues;
import de.rincewind.dmxc.system.commands.CommandStop;
import de.rincewind.dmxc.system.commands.CommandSubmaster;
import de.rincewind.dmxc.system.environment.DMXEnvironment;
import de.rincewind.dmxc.system.environment.MergingMethod;
import de.rincewind.dmxc.system.network.Server;

public class Main {
	
	private static Gson gson;
	
	static {
		Main.gson = new GsonBuilder().setPrettyPrinting().create();
	}
	
	public static <T> T fromJson(File file, Class<T> jsonClass) {
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			T object = Main.gson.fromJson(reader, jsonClass);
			
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
			writer.write(Main.gson.toJson(json));
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
	
	
	private static Server server;
	private static AccountManagement management;
	private static DMXEnvironment environment;
	
	private static File accountsFile;
	private static File submastersFile;
	
	public static void main(String[] args) {
		System.out.println("Preparing...");
		
		Main.submastersFile = new File("submasters.json");
		Main.setupFile(Main.submastersFile);
		
		Main.environment = new DMXEnvironment(MergingMethod.HIGHST_VALUE);
		Main.environment.loadSubmasters(Main.submastersFile);
		
		Main.management = new AccountManagement();
		Main.accountsFile = new File("accounts.json");
		Main.setupFile(Main.accountsFile);
		
		
		if (Main.accountsFile.exists()) {
			Main.management.loadAccounts(Main.accountsFile);
			Main.management.addAccount(new Account("admin", "dmxcontroller"));
			Main.management.saveAccounts(Main.accountsFile);
		}
		
		new Thread(() -> {
			Console.initConsole();
			Console.registerCommand("stop", new CommandStop());
			Console.registerCommand("accounts", new CommandAccounts());
			Console.registerCommand("dmxvalues", new CommandDMXValues());
			Console.registerCommand("submaster", new CommandSubmaster());
			Console.startConsole();
		}).start();
		
		Main.server = new Server();
		Main.server.setPort(2345);
		Main.server.connect();
	}
	
	public static Server server() {
		return Main.server;
	}
	
	public static AccountManagement management() {
		return Main.management;
	}
	
	public static DMXEnvironment environment() {
		return Main.environment;
	}
	
	public static void shutdown() {
		if (Main.accountsFile.exists()) {
			Main.management.saveAccounts(Main.accountsFile);
		}
		
		if (Main.submastersFile.exists()) {
			Main.environment.saveSubmasters(Main.submastersFile);
		}
		
		if (Main.server.isConnected()) {
			Main.server.disconnect();
		}
		
		try {
			Console.closeConsole();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void setupFile(File file) {
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
