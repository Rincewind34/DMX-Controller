package de.rincewind.dmxc.app.gui;

import de.rincewind.dmxc.app.api.FaderBase;
import de.rincewind.dmxc.app.gui.util.Color;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

public class Fader extends VBox implements TemplateComponent {
	
	public static final Color COLOR_CHANNEL = new Color(0xFA, 0xEF, 0xA8);
	public static final Color COLOR_SUBMASTER = new Color(0xD4, 0xEC, 0xFF);
	public static final Color COLOR_EFFECT = new Color(0xE4, 0xFF, 0xE3);
	
	
	private FaderBase base;
	
	@FXML
	private Slider fader;
	
	@FXML
	private Button flashButton;
	
	@FXML
	private ToggleButton settingButton;
	
	public Fader() {
		TemplateComponent.loadFXML(this, "fader.fxml", "basics.css", "fader.css");
		
		this.base = new FaderBase(this.fader, this.flashButton, this.settingButton);
	}
	
	public void setBackgroundColor(Color color) {
		this.setStyle("-fx-background-color: " + TemplateComponent.BORDER_COLOR.toCSS() + ", " + color.toCSS() + ";");
	}
	
	public FaderBase base() {
		return this.base;
	}
	
}
