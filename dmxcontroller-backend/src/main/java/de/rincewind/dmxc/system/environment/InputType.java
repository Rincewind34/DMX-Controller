package de.rincewind.dmxc.system.environment;

import de.rincewind.dmxc.common.Console;

public enum InputType {
	
	NONE(""),
	CHANNEL(Console.ANSI_YELLOW),
	SUBMASTER(Console.ANSI_BLUE),
	EFFECT(Console.ANSI_GREEN),
	SHOW(Console.ANSI_MAGENTA);
	
	private String color;
	
	private InputType(String color) {
		this.color = color;
	}
	
	public String getConsoleColor() {
		return this.color;
	}
	
}
