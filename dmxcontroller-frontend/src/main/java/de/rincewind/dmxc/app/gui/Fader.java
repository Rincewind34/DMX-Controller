package de.rincewind.dmxc.app.gui;

import java.io.InputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.rincewind.dmxc.app.api.Channel;
import de.rincewind.dmxc.app.api.ChannelSelection;
import de.rincewind.dmxc.app.api.Fadeable;
import de.rincewind.dmxc.app.api.Submaster;
import de.rincewind.dmxc.app.gui.util.Color;
import de.rincewind.dmxc.app.gui.util.FaderBase;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Fader extends TemplateComponent {
	
	public static final Color COLOR_CHANNEL = new Color(0xFA, 0xEF, 0xA8);
	public static final Color COLOR_SUBMASTER = new Color(0xD4, 0xEC, 0xFF);
	public static final Color COLOR_EFFECT = new Color(0xE4, 0xFF, 0xE3);
	
	public static ConfigController loadConfigController(TemplateComponent component, FaderBase faderBase) {
		ConfigController configPane = new ConfigController();
		FileLoader.loadFXML(configPane, "configs/fader-config.fxml");
		configPane.init(component, faderBase);
		return configPane;
	}
	
	
	private ToolController toolPane;
	private ConfigController configPane;
	
	public Fader() {
		this((Fadeable) null);
	}
	
	public Fader(Fadeable target) {
		this.setCaption("Fader");
		this.toolPane = new ToolController();
		FileLoader.loadFXML(this.toolPane, "fader.fxml");
		this.toolPane.init();
		this.faderBase().setTarget(target);
		
		this.configPane = Fader.loadConfigController(this, this.faderBase());
	}
	
	protected Fader(JsonElement element) {
		this(Fadeable.deserialize(element.getAsJsonObject()));
	}
	
	@Override
	public String getType() {
		return "fader";
	}
	
	@Override
	public void update() {
		if (this.faderBase().getTarget() instanceof Channel || this.faderBase().getTarget() instanceof ChannelSelection) {
			TemplateComponent.setBackgroundColor(this.toolPane, Fader.COLOR_CHANNEL);
		} else if (this.faderBase().getTarget() instanceof Submaster) {
			TemplateComponent.setBackgroundColor(this.toolPane, Fader.COLOR_SUBMASTER);
		}
		
		this.faderBase().updateSlider();
	}
	
	public FaderBase faderBase() {
		return this.toolPane.base;
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		if (this.faderBase().getTarget() != null) {
			return this.faderBase().getTarget().serialize();
		} else {
			return new JsonObject();
		}
	}
	
	@Override
	protected Pane getToolPane() {
		return this.toolPane;
	}
	
	@Override
	protected Pane getConfigPane() {
		return this.configPane;
	}
	
	@Override
	protected InputStream getDragDropImageStream() {
		return FileLoader.getImageStream("faders");
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
	
	public static class ConfigController extends VBox {
		
		@FXML
		private TextField textCaption;
		
		@FXML
		private Button buttonSetSelection;
		
		@FXML
		private Label labelSelection;
		
		private void init(TemplateComponent component, FaderBase fader) {
			component.bindCaptionField(this.textCaption);
			
			this.buttonSetSelection.setOnAction((event) -> {
				fader.setTarget(component.getRoot().getCurrentSelection());
				this.updateDisplay(fader);
			});
			
			this.updateDisplay(fader);
		}
		
		private void updateDisplay(FaderBase fader) {
			this.labelSelection.setText(fader.getTarget() == null ? "Nothing" : fader.getTarget().toString());
		}
		
	}
	
}
