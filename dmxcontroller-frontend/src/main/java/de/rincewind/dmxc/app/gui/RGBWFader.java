package de.rincewind.dmxc.app.gui;

import com.google.gson.JsonElement;

import de.rincewind.dmxc.app.gui.util.FaderBase;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

public class RGBWFader extends ColorFader {
	
	public RGBWFader() {
		super(4);
		
		this.setCaption("RGBW-Fader");
	}
	
	protected RGBWFader(JsonElement element) {
		this();
		
		this.deserialize(element);
	}
	
	@Override
	public String getType() {
		return "rgbwfader";
	}
	
	@Override
	public TemplateComponent newOne() {
		return new RGBWFader();
	}
	
	public FaderBase redBase() {
		return this.getFaderBase(0);
	}
	
	public FaderBase greenBase() {
		return this.getFaderBase(1);
	}
	
	public FaderBase blueBase() {
		return this.getFaderBase(2);
	}
	
	public FaderBase whiteBase() {
		return this.getFaderBase(3);
	}
	
	@Override
	protected String getTooltip() {
		return "RGBW-Fader\n\nThis fader group provides four faders at\nonce (red, green, blue and white).";
	}
	
	@Override
	protected ColorFader.ToolController newToolPane() {
		ToolController toolPane = new ToolController(this);
		FileLoader.loadFXML(toolPane, "rgbw-fader.fxml");
		toolPane.init();
		
		this.initFader(0, new FaderBase(toolPane.faderRed, toolPane.flashRed, toolPane.buttonPushZero));
		this.initFader(1, new FaderBase(toolPane.faderGreen, toolPane.flashGreen, toolPane.buttonPushZero));
		this.initFader(2, new FaderBase(toolPane.faderBlue, toolPane.flashBlue, toolPane.buttonPushZero));
		this.initFader(3, new FaderBase(toolPane.faderWhite, toolPane.flashWhite, toolPane.buttonPushZero));
		
		return toolPane;
	}
	
	@Override
	protected ColorFader.ConfigController newConfigPane() {
		ConfigController configPane = new ConfigController(this);
		FileLoader.loadFXML(configPane, "configs/colorfader-config.fxml");
		configPane.init();
		return configPane;
	}
	
	
	private static class ToolController extends ColorFader.ToolController {
		
		@FXML
		private Button flashRed;
		
		@FXML
		private Button flashGreen;
		
		@FXML
		private Button flashBlue;
		
		@FXML
		private Button flashWhite;
		
		@FXML
		private Slider faderRed;
		
		@FXML
		private Slider faderGreen;
		
		@FXML
		private Slider faderBlue;
		
		@FXML
		private Slider faderWhite;
		
		public ToolController(ColorFader root) {
			super(root);
		}
		
	}
	
	private static class ConfigController extends ColorFader.ConfigController {
		
		public ConfigController(ColorFader root) {
			super(root);
		}

		@Override
		protected void setupColorBox(ComboBox<String> boxColors) {
			boxColors.getItems().add("Red");
			boxColors.getItems().add("Green");
			boxColors.getItems().add("Blue");
			boxColors.getItems().add("White");
		}
		
	}
	
}
