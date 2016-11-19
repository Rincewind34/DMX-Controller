package de.rincewind.dmxc.app.gui;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.rincewind.dmxc.app.api.Fadeable;
import de.rincewind.dmxc.app.api.Submaster;
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

public class SubmasterFader extends TemplateComponent {
	
	private ToolController toolPane;
	private ConfigController configPane;
	
	public SubmasterFader() {
		this.setCaption("Submasters");
		this.toolPane = new ToolController();
		FileLoader.loadFXML(this.toolPane, "submaster-fader.fxml");
		this.toolPane.init();
		
		this.configPane = new ConfigController();
		FileLoader.loadFXML(this.configPane, "configs/submasterfader-config.fxml");
		this.configPane.init(this);
		
		TemplateComponent.setBackgroundColor(this.toolPane, Fader.COLOR_SUBMASTER);
	}
	
	protected SubmasterFader(JsonElement element) {
		this();
		
		JsonArray array = element.getAsJsonArray();
		
		for (int i = 0; i < 4; i++) {
			JsonObject object = array.get(i).getAsJsonObject();
			
			if (object.has("submaster")) {
				this.setSubmaster(i, (Submaster) Fadeable.deserialize(object.get("submaster").getAsJsonObject()));
			}
		}
	}
	
	@Override
	public String getType() {
		return "submasterfader";
	}
	
	@Override
	public void update() {
		this.faderBase().updateSlider();
	}
	
	public void setSubmaster(int index, Submaster submaster) {
		this.toolPane.setSubmaster(index, submaster);
	}
	
	public Submaster getSubmaster(int index) {
		return this.toolPane.submasters.get(index);
	}
	
	public FaderBase faderBase() {
		return this.toolPane.base;
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		JsonArray array = new JsonArray();
		
		for (int i = 0; i < 4; i++) {
			JsonObject object = new JsonObject();
			
			if (this.getSubmaster(i) != null) {
				object.add("submaster", this.getSubmaster(i).serialize());
			}
			
			array.add(object);
		}
		
		return array;
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
	
	
	private static class ToolController extends HBox {
		
		private FaderBase base;
		
		@FXML
		private Slider fader;
		
		@FXML
		private Button buttonFlash;
		
		@FXML
		private ToggleButton buttonPushZero;
		
		@FXML
		private ToggleButton buttonSub1;
		
		@FXML
		private ToggleButton buttonSub2;
		
		@FXML
		private ToggleButton buttonSub3;
		
		@FXML
		private ToggleButton buttonSub4;
		
		private int current;
		
		private Map<Integer, Submaster> submasters;
		private Map<Integer, Short> values;
		
		private void init() {
			this.values = new HashMap<>();
			this.submasters = new HashMap<>();
			this.current = -1;
			
			this.base = new FaderBase(this.fader, this.buttonFlash, this.buttonPushZero);
			
			this.setupButton(0, this.buttonSub1);
			this.setupButton(1, this.buttonSub2);
			this.setupButton(2, this.buttonSub3);
			this.setupButton(3, this.buttonSub4);
			
			this.updateDisable();
		}
		
		private void setSubmaster(int index, Submaster submaster) {
			this.submasters.put(index, submaster);
			this.values.put(index, (short) 0);
			
			if (this.current == index) {
				this.activate();
			}
			
			this.getButton(index).setText(submaster.getName());
			this.updateDisable();
		}
		
		private void setupButton(int index, ToggleButton button) {
			button.armedProperty().addListener((observeable, oldValue, newValue) -> {
				if (this.current != -1) {
					this.values.put(this.current, this.base.getFaderValue());
				}
				
				if (newValue == true && !button.isSelected()) {
					if (this.submasters.containsKey(index)) {
						this.current = index;
						this.activate();
						this.updateDisable();
						this.base.fireChange();
					}
				} else if (newValue == false && !button.isSelected()) {
					this.current = -1;
					this.base.setTarget(null);
					this.base.setFaderValue((short) 0);
					this.updateDisable();
				}
			});
			
			button.setOnAction((event) -> {
				if (this.current != -1) {
					this.values.put(this.current, this.base.getFaderValue());
				}
				
				if (this.submasters.containsKey(index)) {
					this.current = index;
					this.activate();
					this.updateDisable();
					this.base.fireChange();
					this.setButton(button);
				}
			});
		}
		
		private void activate() {
			this.base.setTarget(this.submasters.get(this.current));
			
			if (this.values.containsKey(this.current)) {
				this.base.setFaderValue(this.values.get(this.current));
			}
		}
		
		private void updateDisable() {
			boolean value = this.current == -1;
			
			this.fader.setDisable(value);
			this.buttonFlash.setDisable(value);
			this.buttonPushZero.setDisable(value);
			
			for (int i = 0; i < 4; i++) {
				this.getButton(i).setDisable(!this.submasters.containsKey(i));
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
				return this.buttonSub1;
			} else if (index == 1) {
				return this.buttonSub2;
			} else if (index == 2) {
				return this.buttonSub3;
			} else if (index == 3) {
				return this.buttonSub4;
			} else {
				return null;
			}
		}
		
	}
	
	private static class ConfigController extends VBox {
		
		@FXML
		private TextField textCaption;
		
		@FXML
		private Button buttonSetSelection;
		
		@FXML
		private Label labelSelection;
		
		@FXML
		private ComboBox<String> boxSubmasters;
		
		private void init(SubmasterFader fader) {
			fader.bindCaptionField(this.textCaption);
			
			this.boxSubmasters.getItems().add("Submaster 1");
			this.boxSubmasters.getItems().add("Submaster 2");
			this.boxSubmasters.getItems().add("Submaster 3");
			this.boxSubmasters.getItems().add("Submaster 4");
			this.boxSubmasters.getSelectionModel().select(0);
			
			this.boxSubmasters.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
				this.updateDisplay(fader);
			});
			
			this.buttonSetSelection.setOnAction((event) -> {
				this.updateSelection(fader);
			});
			
			this.updateDisplay(fader);
		}
		
		private void updateSelection(SubmasterFader fader) {
			if (fader.getRoot().getCurrentSelection() instanceof Submaster) {
				fader.setSubmaster(this.getSelectedIndex(), (Submaster) fader.getRoot().getCurrentSelection());
				this.updateDisplay(fader);
			}
		}
		
		private void updateDisplay(SubmasterFader fader) {
			this.labelSelection.setText(fader.getSubmaster(this.getSelectedIndex()) == null ? "Nothing" : fader.getSubmaster(this.getSelectedIndex()).toString());
		}
		
		private int getSelectedIndex() {
			return this.boxSubmasters.getSelectionModel().getSelectedIndex();
		}
		
	}
	
}
