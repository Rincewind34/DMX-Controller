package de.rincewind.dmxc.app.gui.util;

public class Color {
	
	private int red;
	private int green;
	private int blue;
	
	public Color(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public String toCSS() {
		return "rgb(" + red + "," + green + "," + blue + ")";
	}
	
}
