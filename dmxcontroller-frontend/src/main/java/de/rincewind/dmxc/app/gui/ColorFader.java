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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public abstract class ColorFader extends TemplateComponent {
	
	private int colorAmount;
	
	private FaderBase[] faders;
	
	private ToolController toolPane;
	private ConfigController configPane;
	
	protected ColorFader(int colorAmount) {
		this.colorAmount = colorAmount;
		this.faders = new FaderBase[this.colorAmount];
		
		this.toolPane = this.newToolPane();
		this.configPane = this.newConfigPane();
	}
	
	@Override
	public void update() {
		this.configPane.updateColorNames();
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
	
	public void setColorValues(int index, short[] values) {
		if (index == 0) {
			this.toolPane.color1 = values;
		} else if (index == 1) {
			this.toolPane.color2 = values;
		} else if (index == 2) {
			this.toolPane.color3 = values;
		}
	}
	
	public int getColorAmount() {
		return this.colorAmount;
	}
	
	public short[] getColorValues(int index) {
		if (index == 0) {
			return this.toolPane.color1;
		} else if (index == 1) {
			return this.toolPane.color2;
		} else if (index == 2) {
			return this.toolPane.color3;
		} else {
			return null;
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
	
	public FaderBase getFaderBase(int index) {
		return this.faders[index];
	}
	
	protected abstract ToolController newToolPane();
	
	protected abstract ConfigController newConfigPane();
	
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
	
	@Override
	protected JsonElement serializeSimplified() {
		JsonObject root = new JsonObject();
		JsonArray arrayColors = new JsonArray();
		JsonArray arrayTargets = new JsonArray();
		
		for (int i = 0; i < 3; i++) {
			JsonObject object = new JsonObject();
			JsonArray arrayColorValues = new JsonArray();
			
			for (int j = 0; j < this.colorAmount; j++) {
				arrayColorValues.add(new JsonPrimitive(this.getColorValues(i)[j]));
			}
			
			object.addProperty("name", this.getColorName(i));
			object.add("values", arrayColorValues);
			arrayColors.add(object);
		}
		
		for (int i = 0; i < this.colorAmount; i++) {
			FaderBase base = this.getFaderBase(i);
			
			if (base.getTarget() != null) {
				arrayTargets.add(base.getTarget().serialize());
			} else {
				arrayTargets.add(new JsonObject());
			}
		}
		
		root.add("colors", arrayColors);
		root.add("targets", arrayTargets);
		return root;
	}
	
	protected void initFader(int index, FaderBase fader) {
		this.faders[index] = fader;
	}
	
	protected void deserialize(JsonElement element) {
		JsonObject root = element.getAsJsonObject();
		JsonArray arrayColors = root.get("colors").getAsJsonArray();
		JsonArray arrayTargets = root.get("targets").getAsJsonArray();
		
		for (int i = 0; i < 3; i++) {
			JsonObject object = arrayColors.get(i).getAsJsonObject();
			JsonArray arrayColorValues = new JsonArray();
			
			short[] values = new short[this.colorAmount];
			
			for (int j = 0; j < this.colorAmount; j++) {
				values[j] = arrayColorValues.get(j).getAsShort();
			}
			
			this.setColorName(i, object.get("name").getAsString());
			this.setColorValues(i, values);
		}
		
		for (int i = 0; i < this.colorAmount; i++) {
			this.getFaderBase(i).setTarget(Fadeable.deserialize(arrayTargets.get(i).getAsJsonObject()));
		}
	}
	
	protected static class ToolController extends HBox {
		
		@FXML
		private Button buttonColor1;
		
		@FXML
		private Button buttonColor2;
		
		@FXML
		private Button buttonColor3;
		
		@FXML
		private ToggleButton buttonSaveMode;
		
		@FXML
		protected ToggleButton buttonPushZero;
		
		private short[] color1;
		private short[] color2;
		private short[] color3;
		
		protected ColorFader root;
		
		public ToolController(ColorFader root) {
			this.root = root;
			this.color1 = this.setupArray(new short[this.root.colorAmount]);
			this.color2 = this.setupArray(new short[this.root.colorAmount]);
			this.color3 = this.setupArray(new short[this.root.colorAmount]);
		}
		
		protected void init() {
			this.buttonColor1.setOnAction((event) -> {
				this.useButton(this.color1);
			});
			
			this.buttonColor2.setOnAction((event) -> {
				this.useButton(this.color2);
			});
			
			this.buttonColor3.setOnAction((event) -> {
				this.useButton(this.color3);
			});
		}
		
		private short[] setupArray(short[] array) {
			for (int i = 0; i < array.length; i++) {
				array[i] = -1;
			}
			
			return array;
		}
		
		private void useButton(short[] targetColor) {
			if (this.buttonSaveMode.isSelected()) {
				for (int i = 0; i < targetColor.length; i++) {
					targetColor[i] = this.root.getFaderBase(i).getFaderValue();
				}
				
				this.buttonSaveMode.setSelected(false);
			} else {
				if (targetColor[0] == -1) {
					return;
				}
				
				for (int i = 0; i < targetColor.length; i++) {
					this.root.getFaderBase(i).setFaderValue(targetColor[i]);
				}
			}
		}
		
	}
	
	protected abstract static class ConfigController extends VBox {
		
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
		
		protected ColorFader root;
		
		public ConfigController(ColorFader root) {
			this.root = root;
		}
		
		protected abstract void setupColorBox(ComboBox<String> boxColors);
		
		protected void init() {
			this.root.bindCaptionField(this.textCaption);
			
			this.setupColorBox(this.boxColors);
			this.boxColors.getSelectionModel().select(0);
			
			this.boxColorNames.getItems().add("Color 1");
			this.boxColorNames.getItems().add("Color 2");
			this.boxColorNames.getItems().add("Color 3");
			this.boxColorNames.getSelectionModel().select(0);
			
			this.boxColors.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
				this.updateDisplay();
			});
			
			this.boxColorNames.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
				this.updateColorField();
			});
			
			this.buttonSetSelection.setOnAction((event) -> {
				this.updateSelection();
			});
			
			this.textColorName.textProperty().addListener((observeable, oldValue, newValue) -> {
				this.root.setColorName(this.getColorIndex(), newValue);
			});
			
			this.updateDisplay();
			this.updateColorField();
		}
		
		private void updateColorNames() {
			int current = this.boxColorNames.getSelectionModel().getSelectedIndex();
			
			this.boxColorNames.getItems().clear();
			
			for (int i = 0; i < 3; i++) {
				this.boxColorNames.getItems().add(this.root.getColorName(i));
			}
			
			this.boxColorNames.getSelectionModel().select(current);
		}
		
		private void updateSelection() {
			this.getFaderBase().setTarget(this.root.getRoot().getCurrentSelection());
			this.updateDisplay();
		}
		
		private void updateDisplay() {
			this.labelSelection.setText(this.getFaderBase().getTarget() == null ? "Nothing" : this.getFaderBase().getTarget().toString());
		}
		
		private void updateColorField() {
			this.textColorName.setText(this.root.getColorName(this.getColorIndex()));
		}
		
		private int getColorIndex() {
			return this.boxColorNames.getSelectionModel().getSelectedIndex();
		}
		
		private FaderBase getFaderBase() {
			return this.root.getFaderBase(this.boxColors.getSelectionModel().getSelectedIndex());
		}
		
	}
	
}
