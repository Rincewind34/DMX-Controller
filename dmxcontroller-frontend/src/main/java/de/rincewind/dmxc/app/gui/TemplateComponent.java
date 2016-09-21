package de.rincewind.dmxc.app.gui;

import java.io.IOException;

import de.rincewind.dmxc.app.gui.util.Color;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public interface TemplateComponent {
	
	public static final Color BORDER_COLOR = new Color(0x55, 0x55, 0x55);
	
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
	
}
