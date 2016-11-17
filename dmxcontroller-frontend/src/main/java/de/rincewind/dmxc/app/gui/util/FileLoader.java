package de.rincewind.dmxc.app.gui.util;

import java.io.IOException;
import java.net.URL;

import de.rincewind.dmxc.common.Core;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class FileLoader {
	
	public static final String PATH_FXML = "de/rincewind/dmxc/javafx/fxml/";
	public static final String PATH_CSS = "de/rincewind/dmxc/javafx/css/";
	
	public static void loadFXML(Pane pane, String file, String... styles) {
		FXMLLoader loader = new FXMLLoader(FileLoader.getFXMLFile(file));
		loader.setController(pane);
		loader.setRoot(pane);
		
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String style : styles) {
			pane.getStylesheets().add(FileLoader.PATH_CSS + style);
		}
	}
	
	public static URL getFXMLFile(String name) {
		return Core.class.getClassLoader().getResource(FileLoader.PATH_FXML + name);
	}
	
	public static URL getCSSFile(String name) {
		return Core.class.getClassLoader().getResource(FileLoader.PATH_CSS + name);
	}
	
}
