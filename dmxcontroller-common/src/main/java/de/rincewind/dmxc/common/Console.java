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

	public static final String ANSI_RESET = "\u001B[0m";

	public static final String ANSI_HIGH_INTENSITY = "\u001B[1m";
	public static final String ANSI_LOW_INTENSITY = "\u001B[2m";

	public static final String ANSI_ITALIC = "\u001B[3m";
	public static final String ANSI_UNDERLINE = "\u001B[4m";
	public static final String ANSI_BLINK = "\u001B[5m";
	public static final String ANSI_RAPID_BLINK = "\u001B[6m";
	public static final String ANSI_REVERSE_VIDEO = "\u001B[7m";
	public static final String ANSI_INVISIBLE_TEXT = "\u001B[8m";

	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_MAGENTA = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BACKGROUND_BLACK = "\u001B[40m";
	public static final String ANSI_BACKGROUND_RED = "\u001B[41m";
	public static final String ANSI_BACKGROUND_GREEN = "\u001B[42m";
	public static final String ANSI_BACKGROUND_YELLOW = "\u001B[43m";
	public static final String ANSI_BACKGROUND_BLUE = "\u001B[44m";
	public static final String ANSI_BACKGROUND_MAGENTA = "\u001B[45m";
	public static final String ANSI_BACKGROUND_CYAN = "\u001B[46m";
	public static final String ANSI_BACKGROUND_WHITE = "\u001B[47m";

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

	public static void startConsole() { // (1)
		if (Console.reader != null) {
			throw new RuntimeException("The console is already started!");
		}

		try {
			Console.reader = new ConsoleReader(System.in, System.out);
			String commandLine;

			while (Console.reader != null) {
				try {
					commandLine = Console.reader.readLine("" /* (2) */, null); // (3)
				} catch (UnsupportedOperationException ex) {
					break;
				}

				if (commandLine == null) {
					break;
				}

				if (commandLine.trim().isEmpty()) {
					continue;
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

	public static void closeConsole() throws Exception {
		if (Console.reader == null) {
			throw new RuntimeException("The console is not started!");
		}

		Console.reader.close();
		Console.reader = null;
	}

	@Override
	public void sendMessage(String message) {
		Console.println(message);
	}

}