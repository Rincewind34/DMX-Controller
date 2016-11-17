package de.rincewind.dmxc.app.gui;

import de.rincewind.dmxc.app.api.Channel;
import de.rincewind.dmxc.app.api.ChannelSelection;
import de.rincewind.dmxc.app.api.Fadeable;
import de.rincewind.dmxc.app.api.Submaster;
import de.rincewind.dmxc.app.gui.util.Color;
import de.rincewind.dmxc.app.gui.util.FaderBase;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Fader extends TemplateComponent {
	
	public static final Color COLOR_CHANNEL = new Color(0xFA, 0xEF, 0xA8);
	public static final Color COLOR_SUBMASTER = new Color(0xD4, 0xEC, 0xFF);
	public static final Color COLOR_EFFECT = new Color(0xE4, 0xFF, 0xE3);
	
	private ToolController toolPane;
	private ConfigController configPane;
	
	public Fader() {
		this(null);
	}
	
	public Fader(Fadeable target) {
		this.toolPane = new ToolController();
		FileLoader.loadFXML(this.toolPane, "fader.fxml", "basics.css", "fader.css");
		this.toolPane.init();
		this.toolPane.base.setTarget(target);
		
		this.configPane = new ConfigController();
		FileLoader.loadFXML(this.configPane, "configs/fader-config.fxml");
		this.configPane.init(this);
	}
	
	@Override
	public void update() {
		if (this.faderBase().getTarget() instanceof Channel || this.faderBase().getTarget() instanceof ChannelSelection) {
			this.setBackgroundColor(Fader.COLOR_CHANNEL);
		} else if (this.faderBase().getTarget() instanceof Submaster) {
			this.setBackgroundColor(Fader.COLOR_SUBMASTER);
		}
		
		this.faderBase().updateSlider();
	}
	
	public FaderBase faderBase() {
		return this.toolPane.base;
	}
	
	@Override
	protected Pane getToolPane() {
		return this.toolPane;
	}
	
	@Override
	protected Pane getConfigPane() {
		return this.configPane;
	}
	
	private void setBackgroundColor(Color color) {
		this.toolPane.setStyle("-fx-background-color: " + TemplateComponent.BORDER_COLOR.toCSS() + ", " + color.toCSS() + ";");
	}
	
	
	private static class ToolController extends VBox {
		
		private FaderBase base;
		
		@FXML
		private Slider fader;
		
		@FXML
		private Button flashButton;
		
		@FXML
		private ToggleButton settingButton;
		
		private void init() {
			this.base = new FaderBase(this.fader, this.flashButton, this.settingButton);
		}
		
	}
	
	private static class ConfigController extends VBox {
		
		@FXML
		private TextField textCaption;
		
		@FXML
		private Button buttonSetSelection;
		
		private void init(Fader fader) {
			fader.bindCaptionField(this.textCaption);
			
			this.buttonSetSelection.setOnAction((event) -> {
				fader.faderBase().setTarget(fader.getRoot().getCurrentSelection());
			});
		}
		
	}
	
}
