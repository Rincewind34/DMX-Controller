package de.rincewind.dmxc.common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.rincewind.commandlib.Sender;
import jline.console.ConsoleReader;

public class Console implements Sender {
	
	private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	
	private static ConsoleReader reader;
	private static Map<String, CommandExecutor> executors;
	
	private static Console instance = new Console();
	
	public static Console instance() {
		return Console.instance;
	}
	
	public static void registerCommand(String command, CommandExecutor executor) {
		Console.executors.put(command, executor);
	}
	
	public static void println(String message) {
		System.out.println("[" + Console.format.format(new Date(System.currentTimeMillis())) + " INFO]: " + message);
	}
	
	public static void initConsole() {
		Console.executors = new HashMap<>();
	}
	
	public static void startConsole() {
		if (Console.reader != null) {
			throw new RuntimeException("The console is already started!");
		}
		
		try {
			Console.reader = new ConsoleReader(System.in, System.out);
			String commandLine;
			
			while (Console.reader != null) {
				try {
					commandLine = Console.reader.readLine("", null);
				} catch (UnsupportedOperationException ex) {
					break;
				}
				
				if (commandLine == null) {
					break;
				}
				
				if (Console.executors.containsKey(commandLine.split(" ")[0])) {
					try {
						Console.executors.get(commandLine.split(" ")[0]).execute(commandLine);
					} catch (Throwable cause) {
						Console.println("Error occured during command '" + commandLine + "'!");
						cause.printStackTrace();
					}
				} else {
					Console.println("This command is unknown!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeConsole() {
		if (Console.reader == null) {
			throw new RuntimeException("The console is not started!");
		}
		
		try {
			Console.reader.close();
			Console.reader = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendMessage(String message) {
		Console.println(message);
	}
	
}
