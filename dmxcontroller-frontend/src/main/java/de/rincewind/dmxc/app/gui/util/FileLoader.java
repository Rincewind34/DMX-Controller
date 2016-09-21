package de.rincewind.dmxc.app.gui.util;

import java.net.URL;

import de.rincewind.dmxc.common.Core;

public class FileLoader {
	
	public static final String PATH_FXML = "de/rincewind/dmxc/javafx/fxml/";
	public static final String PATH_CSS = "de/rincewind/dmxc/javafx/css/";
	
	
	public static URL getFXMLFile(String name) {
		return Core.class.getClassLoader().getResource(FileLoader.PATH_FXML + name);
	}
	
	public static URL getCSSFile(String name) {
		return Core.class.getClassLoader().getResource(FileLoader.PATH_CSS + name);
	}
	
}
