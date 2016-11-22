package de.rincewind.dmxc.app.gui;

import java.io.InputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

public abstract class MultiFader<T extends Fadeable> extends TemplateComponent {
	
	private String targetType;
	private String displayName;
	
	private Class<T> cls;
	
	private ToolController<T> toolPane;
	private ConfigController<T> configPane;
	
	public MultiFader(Class<T> cls, String targetType, String displayName) {
		this.cls = cls;
		this.targetType = targetType;
		this.displayName = displayName;
		
		this.toolPane = new ToolController<>();
		FileLoader.loadFXML(this.toolPane, "multi-fader.fxml");
		this.toolPane.init(this);
		
		this.configPane = new ConfigController<>();
		FileLoader.loadFXML(this.configPane, "configs/multifader-config.fxml");
		this.configPane.init(this);
	}
	
	@Override
	public void update() {
		this.faderBase().updateSlider();
	}
	
	public void setFadeable(int index, T fadeable) {
		this.toolPane.setFadeable(index, fadeable);
	}
	
	public String getTargetType() {
		return this.targetType;
	}
	
	public Class<T> getTypeClass() {
		return this.cls;
	}
	
	public T getFadeable(int index) {
		return this.toolPane.fadeables[index];
	}
	
	public FaderBase faderBase() {
		return this.toolPane.base;
	}
	
	protected abstract T[] newArray(int size);
	
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
		return FileLoader.getImageStream("spotlights");
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		JsonArray array = new JsonArray();
		
		for (int i = 0; i < 4; i++) {
			JsonObject object = new JsonObject();
			
			if (this.getFadeable(i) != null) {
				object.add("fadeable", this.getFadeable(i).serialize());
			}
			
			array.add(object);
		}
		
		return array;
	}
	
	protected void deserialize(JsonElement element) {
		JsonArray array = element.getAsJsonArray();
		
		for (int i = 0; i < 4; i++) {
			JsonObject object = array.get(i).getAsJsonObject();
			
			if (object.has("fadeable")) {
				this.setFadeable(i, this.cls.cast(Fadeable.deserialize(object.get("fadeable").getAsJsonObject())));
			}
		}
	}
	
	
	private static class ToolController<T extends Fadeable> extends HBox {
		
		@FXML
		private Slider fader;
		
		@FXML
		private Button buttonFlash;
		
		@FXML
		private ToggleButton buttonPushZero;
		
		@FXML
		private ToggleButton buttonFader1;
		
		@FXML
		private ToggleButton buttonFader2;
		
		@FXML
		private ToggleButton buttonFader3;
		
		@FXML
		private ToggleButton buttonFader4;
		
		private int current;
		
		private T[] fadeables;
		private Short[] values;
		
		private FaderBase base;
		
		private MultiFader<T> root;
		
		private void init(MultiFader<T> root) {
			this.root = root;
			this.values = new Short[4];
			this.fadeables = this.root.newArray(4);
			this.current = -1;
			
			this.base = new FaderBase(this.fader, this.buttonFlash, this.buttonPushZero);
			
			this.setupButton(0, this.buttonFader1);
			this.setupButton(1, this.buttonFader2);
			this.setupButton(2, this.buttonFader3);
			this.setupButton(3, this.buttonFader4);
			
			this.updateDisable();
		}
		
		private void setFadeable(int index, T fadeable) {
			this.fadeables[index] = fadeable;
			this.values[index] = 0;
			
			if (this.current == index) {
				this.activate();
			}
			
			this.getButton(index).setText(fadeable.toString());
			this.updateDisable();
		}
		
		private void setupButton(int index, ToggleButton button) {
			button.armedProperty().addListener((observeable, oldValue, newValue) -> {
				if (this.current != -1) {
					this.values[this.current] = this.base.getFaderValue();
				}
				
				if (newValue == true && !button.isSelected()) {
					if (this.fadeables[this.current] != null) {
						this.current = index;
						this.activate();
						this.updateDisable();
						this.base.fireChange();
						this.setButton(button);
					}
				} else if (newValue == false && !button.isSelected()) {
					this.current = -1;
					this.base.setTarget(null);
					this.base.setFaderValue((short) 0);
					this.updateDisable();
				}
			});
			
//			button.setOnAction((event) -> {
//				if (this.current != -1) {
//					this.values[this.current] = this.base.getFaderValue();
//				}
//				
//				if (this.submasters.containsKey(index)) {
//					this.current = index;
//					this.activate();
//					this.updateDisable();
//					this.base.fireChange();
//					this.setButton(button);
//				}
//			});
		}
		
		private void activate() {
			this.base.setTarget(this.fadeables[this.current]);
			
			if (this.values[this.current] != null) {
				this.base.setFaderValue(this.values[this.current].shortValue());
			}
		}
		
		private void updateDisable() {
			this.base.setDisabled(this.current == -1);
			
			for (int i = 0; i < 4; i++) {
				this.getButton(i).setDisable(this.fadeables[i] == null);
			}
		}
		
		private void setButton(ToggleButton button) {
			for (int i = 0; i < 4; i++) {
				if (this.getButton(i) != button) {
					this.getButton(i).setSelected(false);
				}
			}
		}
		
		private ToggleButton getButton(int index) {
			if (index == 0) {
				return this.buttonFader1;
			} else if (index == 1) {
				return this.buttonFader2;
			} else if (index == 2) {
				return this.buttonFader3;
			} else if (index == 3) {
				return this.buttonFader4;
			} else {
				return null;
			}
		}
		
	}
	
	private static class ConfigController<T extends Fadeable> extends VBox {
		
		@FXML
		private TextField textCaption;
		
		@FXML
		private Button buttonSetSelection;
		
		@FXML
		private Label labelSelection;
		
		@FXML
		private ComboBox<String> boxFaders;
		
		private MultiFader<T> root;
		
		private void init(MultiFader<T> root) {
			this.root = root;
			
			this.root.bindCaptionField(this.textCaption);
			
			this.boxFaders.getItems().add(this.root.displayName + " 1");
			this.boxFaders.getItems().add(this.root.displayName + " 2");
			this.boxFaders.getItems().add(this.root.displayName + " 3");
			this.boxFaders.getItems().add(this.root.displayName + " 4");
			this.boxFaders.getSelectionModel().select(0);
			
			this.boxFaders.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
				this.updateDisplay();
			});
			
			this.buttonSetSelection.setOnAction((event) -> {
				this.updateSelection();
			});
			
			this.updateDisplay();
		}
		
		private void updateSelection() {
			if (this.root.getRoot().getCurrentSelection().getType().equals(this.root.targetType)) {
				this.root.setFadeable(this.getSelectedIndex(), this.root.cls.cast(this.root.getRoot().getCurrentSelection()));
				this.updateDisplay();
			}
		}
		
		private void updateDisplay() {
			this.labelSelection.setText(this.root.getFadeable(this.getSelectedIndex()) == null ? "Nothing" : this.root.getFadeable(this.getSelectedIndex()).toString());
		}
		
		private int getSelectedIndex() {
			return this.boxFaders.getSelectionModel().getSelectedIndex();
		}
		
	}
	
}
