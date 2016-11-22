package de.rincewind.dmxc.system;

import java.io.File;

import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.util.FileUtil;
import de.rincewind.dmxc.system.commands.CommandAccounts;
import de.rincewind.dmxc.system.commands.CommandDMXValues;
import de.rincewind.dmxc.system.commands.CommandEffect;
import de.rincewind.dmxc.system.commands.CommandShow;
import de.rincewind.dmxc.system.commands.CommandStop;
import de.rincewind.dmxc.system.commands.CommandSubmaster;
import de.rincewind.dmxc.system.environment.DMXEnvironment;
import de.rincewind.dmxc.system.environment.MergingMethod;
import de.rincewind.dmxc.system.environment.SceneRegistry;
import de.rincewind.dmxc.system.network.Server;

public class Main {
	
	private static Server server;
	private static AccountManagement management;
	private static DMXEnvironment environment;
	
	private static File accountsFile;
	private static File submastersFile;
	private static File effectsFile;
	
	public static void main(String[] args) {
		System.out.println("Preparing...");
		
		Main.submastersFile = new File("submasters.json");
		FileUtil.setupFile(Main.submastersFile);
		
		Main.effectsFile = new File("effects.json");
		FileUtil.setupFile(Main.effectsFile);
		
		Main.environment = new DMXEnvironment(MergingMethod.HIGHST_VALUE);
		Main.environment.loadSubmasters(Main.submastersFile);
		Main.environment.loadEffects(Main.effectsFile);
		Main.environment.create();
		
		Main.management = new AccountManagement();
		Main.accountsFile = new File("accounts.json");
		FileUtil.setupFile(Main.accountsFile);
		
		
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
			Console.registerCommand("effect", new CommandEffect());
			Console.registerCommand("show", new CommandShow());
			Console.startConsole();
		}).start();
		
		Main.server = new Server();
		Main.server.setPort(2345);
		Main.server.connect();
		
		Main.environment.getShow().addScene(SceneRegistry.getScene("red"));
		Main.environment.getShow().addScene(SceneRegistry.getScene("black"));
		Main.environment.getShow().addScene(SceneRegistry.getScene("green"));
		Main.environment.getShow().addDimmer((short) 1);
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
		
		if (Main.effectsFile.exists()) {
			Main.environment.saveEffects(Main.effectsFile);
		}
		
		if (Main.server.isConnected()) {
			Main.server.disconnect();
		}
		
		Main.environment.destroy();
		
		try {
			Console.closeConsole();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}