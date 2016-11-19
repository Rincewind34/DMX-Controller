package de.rincewind.dmxc.app.gui;

import java.io.InputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.rincewind.dmxc.app.api.Fadeable;
import de.rincewind.dmxc.app.gui.util.FaderBase;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class RGBFader extends TemplateComponent {
	
	private ToolController toolPane;
	
	private ConfigController configPane;
	
	public RGBFader() {
		this.setCaption("RGB-Fader");
		this.toolPane = new ToolController();
		FileLoader.loadFXML(this.toolPane, "rgb-fader.fxml");
		this.toolPane.init();
		
		this.configPane = new ConfigController();
		FileLoader.loadFXML(this.configPane, "configs/colorfader-config.fxml");
		this.configPane.init(this);
	}
	
	protected RGBFader(JsonElement element) {
		this();
		
		JsonObject object = element.getAsJsonObject();
		JsonArray array = object.get("names").getAsJsonArray();
		
		for (int i = 0; i < 3; i++) {
			this.setColorName(i, array.get(i).getAsString());
		}
		
		this.redBase().setTarget(Fadeable.deserialize(object.get("redtarget").getAsJsonObject()));
		this.greenBase().setTarget(Fadeable.deserialize(object.get("greentarget").getAsJsonObject()));
		this.blueBase().setTarget(Fadeable.deserialize(object.get("bluetarget").getAsJsonObject()));
	}
	
	@Override
	public String getType() {
		return "rgbfader";
	}
	
	public void setColorName(int index, String name) {
		if (index == 0) {
			this.toolPane.buttonColor1.setText(name);
		} else if (index == 1) {
			this.toolPane.buttonColor2.setText(name);
		} else if (index == 2) {
			this.toolPane.buttonColor3.setText(name);
		}
	}
	
	public String getColorName(int index) {
		if (index == 0) {
			return this.toolPane.buttonColor1.getText();
		} else if (index == 1) {
			return this.toolPane.buttonColor2.getText();
		} else if (index == 2) {
			return this.toolPane.buttonColor3.getText();
		} else {
			return null;
		}
	}
	
	public FaderBase redBase() {
		return this.toolPane.redBase;
	}
	
	public FaderBase greenBase() {
		return this.toolPane.greenBase;
	}
	
	public FaderBase blueBase() {
		return this.toolPane.blueBase;
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		JsonObject object = new JsonObject();
		JsonArray array = new JsonArray();
		
		for (int i = 0; i < 3; i++) {
			array.add(new JsonPrimitive(this.getColorName(i)));
		}
		
		if (this.redBase().getTarget() != null) {
			object.add("redtarget", this.redBase().getTarget().serialize());
		} else {
			object.add("redtarget", new JsonObject());
		}
		
		if (this.greenBase().getTarget() != null) {
			object.add("greentarget", this.greenBase().getTarget().serialize());
		} else {
			object.add("greentarget", new JsonObject());
		}
		
		if (this.blueBase().getTarget() != null) {
			object.add("bluetarget", this.blueBase().getTarget().serialize());
		} else {
			object.add("bluetarget", new JsonObject());
		}
		
		object.add("names", array);
		return object;
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
		return FileLoader.getImageStream("complexfaders");
	}
	
	
	private static class ToolController extends HBox {
		
		@FXML
		private Button flashRed;
		
		@FXML
		private Button flashGreen;
		
		@FXML
		private Button flashBlue;
		
		@FXML
		private Slider faderRed;
		
		@FXML
		private Slider faderGreen;
		
		@FXML
		private Slider faderBlue;
		
		@FXML
		private ToggleButton buttonPushZero;
		
		@FXML
		private ToggleButton buttonSaveMode;
		
		@FXML
		private Button buttonColor1;
		
		@FXML
		private Button buttonColor2;
		
		@FXML
		private Button buttonColor3;
		
		private FaderBase redBase;
		private FaderBase greenBase;
		private FaderBase blueBase;
		
		private double[] color1;
		private double[] color2;
		private double[] color3;
		
		private void init() {
			this.color1 = new double[] { -1, -1, -1 };
			this.color2 = new double[] { -1, -1, -1 };
			this.color3 = new double[] { -1, -1, -1 };
			
			this.buttonColor1.setOnAction((event) -> {
				this.useButton(this.color1);
			});
			
			this.buttonColor2.setOnAction((event) -> {
				this.useButton(this.color2);
			});
			
			this.buttonColor3.setOnAction((event) -> {
				this.useButton(this.color3);
			});
			
			this.redBase = new FaderBase(this.faderRed, this.flashRed, this.buttonPushZero);
			this.greenBase = new FaderBase(this.faderGreen, this.flashGreen, this.buttonPushZero);
			this.blueBase = new FaderBase(this.faderBlue, this.flashBlue, this.buttonPushZero);
		}
		
		private void useButton(double[] targetColor) {
			if (this.buttonSaveMode.isSelected()) {
				targetColor[0] = this.faderRed.getValue();
				targetColor[1] = this.faderGreen.getValue();
				targetColor[2] = this.faderBlue.getValue();
				this.buttonSaveMode.setSelected(false);
			} else {
				if (targetColor[0] == -1) {
					return;
				}
				
				this.faderRed.setValue(targetColor[0]);
				this.faderGreen.setValue(targetColor[1]);
				this.faderBlue.setValue(targetColor[2]);
			}
		}
		
	}
	
	private static class ConfigController extends VBox {
		
		@FXML
		private TextField textCaption;
		
		@FXML
		private TextField textColorName;
		
		@FXML
		private Button buttonSetSelection;
		
		@FXML
		private Label labelSelection;
		
		@FXML
		private ComboBox<String> boxColorNames;
		
		@FXML
		private ComboBox<String> boxColors;
		
		private void init(RGBFader fader) {
			fader.bindCaptionField(this.textCaption);
			
			this.boxColors.getItems().add("Red");
			this.boxColors.getItems().add("Green");
			this.boxColors.getItems().add("Blue");
			this.boxColors.getSelectionModel().select(0);
			
			this.boxColorNames.getItems().add("Color 1");
			this.boxColorNames.getItems().add("Color 2");
			this.boxColorNames.getItems().add("Color 3");
			this.boxColorNames.getSelectionModel().select(0);
			
			this.boxColors.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
				this.updateDisplay(this.getFaderBase(fader));
			});
			
			this.boxColorNames.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
				this.updateColorField(fader);
			});
			
			this.buttonSetSelection.setOnAction((event) -> {
				this.updateSelection(fader);
			});
			
			this.textColorName.textProperty().addListener((observeable, oldValue, newValue) -> {
				fader.setColorName(this.getColorIndex(), newValue);
			});
			
			this.updateDisplay(this.getFaderBase(fader));
			this.updateColorField(fader);
		}
		
		private void updateSelection(RGBFader fader) {
			this.getFaderBase(fader).setTarget(fader.getRoot().getCurrentSelection());
			this.updateDisplay(this.getFaderBase(fader));
		}
		
		private void updateDisplay(FaderBase base) {
			this.labelSelection.setText(base.getTarget() == null ? "Nothing" : base.getTarget().toString());
		}
		
		private void updateColorField(RGBFader fader) {
			this.textColorName.setText(fader.getColorName(this.getColorIndex()));
		}
		
		private int getColorIndex() {
			return this.boxColorNames.getSelectionModel().getSelectedIndex();
		}
		
		private FaderBase getFaderBase(RGBFader fader) {
			if (this.boxColors.getSelectionModel().getSelectedItem().equals("Red")) {
				return fader.redBase();
			} else if (this.boxColors.getSelectionModel().getSelectedItem().equals("Green")) {
				return fader.greenBase();
			} else if (this.boxColors.getSelectionModel().getSelectedItem().equals("Blue")) {
				return fader.blueBase();
			} else {
				return null;
			}
		}
		
	}
	
}
