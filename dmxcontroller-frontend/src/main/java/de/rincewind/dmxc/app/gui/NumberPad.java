package de.rincewind.dmxc.app.gui;

import java.io.InputStream;

import com.google.gson.JsonElement;

import de.rincewind.dmxc.app.api.ChannelSelection;
import de.rincewind.dmxc.app.api.Fadeable;
import de.rincewind.dmxc.app.gui.util.FaderBase;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import de.rincewind.dmxc.app.gui.util.NumberPadInterpreter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class NumberPad extends TemplateComponent {

	private ToolController toolPane;
	private Fader.ConfigController configPane;
	
	public NumberPad() {
		this((Fadeable) null);
	}
	
	public NumberPad(Fadeable target) {
		this.setCaption("Number-Pad");
		this.toolPane = new ToolController();
		FileLoader.loadFXML(this.toolPane, "numberpad.fxml");
		this.toolPane.init();
		this.faderBase().setTarget(target);
		
		this.configPane = Fader.loadConfigController(this, this.faderBase());
		
		TemplateComponent.setBackgroundColor(this.toolPane, Fader.COLOR_CHANNEL);
	}
	
	protected NumberPad(JsonElement element) {
		this(Fadeable.deserialize(element.getAsJsonObject()));
	}
	
	@Override
	public String getType() {
		return "numberpad";
	}
	
	public ChannelSelection getSelectedChannels() {
		return this.toolPane.interpreter.getSelectedChannels();
	}
	
	public FaderBase faderBase() {
		return this.toolPane.faderBase;
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		return this.faderBase().getTarget().serialize();
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
		return FileLoader.getImageStream("numberpad");
	}
	

	private static class ToolController extends HBox {

		@FXML
		private Button button0;

		@FXML
		private Button button1;

		@FXML
		private Button button2;

		@FXML
		private Button button3;

		@FXML
		private Button button4;

		@FXML
		private Button button5;

		@FXML
		private Button button6;

		@FXML
		private Button button7;

		@FXML
		private Button button8;

		@FXML
		private Button button9;

		@FXML
		private Button buttonPlus;

		@FXML
		private Button buttonThru;

		@FXML
		private Button buttonClear;

		@FXML
		private Button buttonBack;

		@FXML
		private Label display;
		
		@FXML
		private Slider fader;
		
		@FXML
		private Button flashButton;
		
		@FXML
		private ToggleButton settingButton;
		
		private FaderBase faderBase;
		
		private NumberPadInterpreter interpreter;

		private void init() {
			this.faderBase = new FaderBase(this.fader, this.flashButton, this.settingButton);
			this.interpreter = new NumberPadInterpreter(this.button0, this.button1, this.button2, this.button3, this.button4, this.button5, this.button6,
					this.button7, this.button8, this.button9, this.buttonPlus, this.buttonThru, this.buttonClear, this.buttonBack, this.display);
			
			this.interpreter.setAction(() -> {
				this.faderBase.setTarget(this.interpreter.getSelection());
			});
		}
		
	}

}
