package de.rincewind.dmxc.system;

import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.system.network.Server;

public class Main {
	
	private static Server server;
	
	public static void main(String[] args) {
		System.out.println("Preparing...");
		
		Main.server = new Server();
		
		new Thread(() -> {
			Console.initConsole();
			Console.startConsole();
		}).start();
	}
	
	public static Server server() {
		return Main.server;
	}
	
	public static void shutdown() {
		if (Main.server.isConnected()) {
			Main.server.disconnect();
		}
		
		Console.closeConsole();
	}
}
