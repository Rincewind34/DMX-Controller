package de.rincewind.dmxc.app.gui;

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

public class RGBWFader extends TemplateComponent {
	
	private ToolController toolPane;
	private ConfigController configPane;
	
	public RGBWFader() {
		this.toolPane = new ToolController();
		FileLoader.loadFXML(this.toolPane, "rgbw-fader.fxml");
		this.toolPane.init();
		
		this.configPane = new ConfigController();
		FileLoader.loadFXML(this.configPane, "configs/colorfader-config.fxml");
		this.configPane.init(this);
	}
	
	protected RGBWFader(JsonElement element) {
		this();
		
		JsonObject object = element.getAsJsonObject();
		JsonArray array = object.get("names").getAsJsonArray();
		
		for (int i = 0; i < 3; i++) {
			this.setColorName(i, array.get(i).getAsString());
		}
		
		this.redBase().setTarget(Fadeable.deserialize(object.get("redtarget").getAsJsonObject()));
		this.greenBase().setTarget(Fadeable.deserialize(object.get("greentarget").getAsJsonObject()));
		this.blueBase().setTarget(Fadeable.deserialize(object.get("bluetarget").getAsJsonObject()));
		this.whiteBase().setTarget(Fadeable.deserialize(object.get("whitetarget").getAsJsonObject()));
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
	
	public FaderBase whiteBase() {
		return this.toolPane.whiteBase;
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		JsonObject object = new JsonObject();
		JsonArray array = new JsonArray();
		
		for (int i = 0; i < 3; i++) {
			array.add(new JsonPrimitive(this.getColorName(i)));
		}
		
		object.add("redtarget", this.redBase().getTarget().serialize());
		object.add("greentarget", this.greenBase().getTarget().serialize());
		object.add("bluetarget", this.blueBase().getTarget().serialize());
		object.add("whitetarget", this.whiteBase().getTarget().serialize());
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
	
	
	private static class ToolController extends HBox {
		
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
		private FaderBase whiteBase;
		
		private double[] color1;
		private double[] color2;
		private double[] color3;
		
		private void init() {
			this.buttonColor1.setOnAction((event) -> {
				if (this.buttonSaveMode.isSelected()) {
					this.color1[0] = this.faderRed.getValue();
					this.color1[1] = this.faderGreen.getValue();
					this.color1[2] = this.faderBlue.getValue();
					this.color1[3] = this.faderWhite.getValue();
					this.buttonSaveMode.setSelected(false);
				} else {
					this.faderRed.setValue(this.color1[0]);
					this.faderGreen.setValue(this.color1[1]);
					this.faderBlue.setValue(this.color1[2]);
					this.faderWhite.setValue(this.color1[3]);
				}
			});
			
			this.buttonColor2.setOnAction((event) -> {
				if (this.buttonSaveMode.isSelected()) {
					this.color2[0] = this.faderRed.getValue();
					this.color2[1] = this.faderGreen.getValue();
					this.color2[2] = this.faderBlue.getValue();
					this.color2[3] = this.faderWhite.getValue();
					this.buttonSaveMode.setSelected(false);
				} else {
					this.faderRed.setValue(this.color2[0]);
					this.faderGreen.setValue(this.color2[1]);
					this.faderBlue.setValue(this.color2[2]);
					this.faderWhite.setValue(this.color2[3]);
				}
			});
			
			this.buttonColor3.setOnAction((event) -> {
				if (this.buttonSaveMode.isSelected()) {
					this.color3[0] = this.faderRed.getValue();
					this.color3[1] = this.faderGreen.getValue();
					this.color3[2] = this.faderBlue.getValue();
					this.color3[3] = this.faderWhite.getValue();
					this.buttonSaveMode.setSelected(false);
				} else {
					this.faderRed.setValue(this.color3[0]);
					this.faderGreen.setValue(this.color3[1]);
					this.faderBlue.setValue(this.color3[2]);
					this.faderWhite.setValue(this.color3[3]);
				}
			});
			
			this.color1 = new double[4];
			this.color2 = new double[4];
			this.color3 = new double[4];
			this.redBase = new FaderBase(this.faderRed, this.flashRed, this.buttonPushZero);
			this.greenBase = new FaderBase(this.faderGreen, this.flashGreen, this.buttonPushZero);
			this.blueBase = new FaderBase(this.faderBlue, this.flashBlue, this.buttonPushZero);
			this.whiteBase = new FaderBase(this.faderWhite, this.flashWhite, this.buttonPushZero);
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
		private ComboBox<String> boxColors;
		
		@FXML
		private ComboBox<String> boxColorNames;
		
		private void init(RGBWFader fader) {
			fader.bindCaptionField(this.textCaption);
			
			this.boxColors.getItems().add("Red");
			this.boxColors.getItems().add("Green");
			this.boxColors.getItems().add("Blue");
			this.boxColors.getItems().add("White");
			this.boxColors.getSelectionModel().select(0);
			
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
		}
		
		private void updateSelection(RGBWFader fader) {
			this.getFaderBase(fader).setTarget(fader.getRoot().getCurrentSelection());
			this.updateDisplay(this.getFaderBase(fader));
		}
		
		private void updateDisplay(FaderBase base) {
			this.labelSelection.setText(base.getTarget() == null ? "Nothing" : base.getTarget().toString());
		}
		
		private void updateColorField(RGBWFader fader) {
			this.textColorName.setText(fader.getColorName(this.getColorIndex()));
		}
		
		private int getColorIndex() {
			return this.boxColorNames.getSelectionModel().getSelectedIndex();
		}
		
		private FaderBase getFaderBase(RGBWFader fader) {
			if (this.boxColors.getSelectionModel().getSelectedItem().equals("Red")) {
				return fader.redBase();
			} else if (this.boxColors.getSelectionModel().getSelectedItem().equals("Green")) {
				return fader.greenBase();
			} else if (this.boxColors.getSelectionModel().getSelectedItem().equals("Blue")) {
				return fader.blueBase();
			} else if (this.boxColors.getSelectionModel().getSelectedItem().equals("White")) {
				return fader.whiteBase();
			} else {
				return null;
			}
		}
		
	}
	
}
