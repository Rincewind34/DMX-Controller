package de.rincewind.dmxc.app.gui.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import de.rincewind.dmxc.app.Main;
import de.rincewind.dmxc.common.Core;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class FileLoader {
	
	public static final String PATH_FXML = "de/rincewind/dmxc/javafx/fxml/";
	public static final String PATH_CSS = "de/rincewind/dmxc/javafx/css/";
	public static final String PATH_IMAGES = "de/rincewind/dmxc/javafx/images/";
	
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
	
	public static InputStream getImageStream(String name) {
		return Main.client().getClass().getResourceAsStream("/" + FileLoader.PATH_IMAGES + name + ".png");
	}
	
	public static URL getFXMLFile(String name) {
		return Core.class.getClassLoader().getResource(FileLoader.PATH_FXML + name);
	}
	
	public static URL getCSSFile(String name) {
		return Core.class.getClassLoader().getResource(FileLoader.PATH_CSS + name);
	}
	
}
