package de.rincewind.dmxc.app.gui;

import java.io.InputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import de.rincewind.dmxc.app.api.Fadeable;
import de.rincewind.dmxc.app.api.Master;
import de.rincewind.dmxc.app.gui.util.Color;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MasterFader extends TemplateComponent {
	
	private ToolController toolPane;
	
	public MasterFader() {
		this.setCaption("Master");
		this.toolPane = new ToolController();
		FileLoader.loadFXML(this.toolPane, "masterfader.fxml");
		this.toolPane.init();
		
		TemplateComponent.setBackgroundColor(this.toolPane, new Color(0x46, 0x00, 0x00));
	}
	
	@Override
	public String getType() {
		return "masterfader";
	}
	
	@Override
	public void update() {
		this.toolPane.updateSlider();
	}
	
	public boolean isActive() {
		return this.toolPane.buttonActive.isSelected();
	}
	
	public boolean isBlackout() {
		return this.toolPane.buttonBlack.isSelected() || this.getFaderValue() == 0;
	}
	
	public short getFaderValue() {
		return (short) ((this.toolPane.fader.getValue() / 100.0D) * 255);
	}
	
	public Short getValue() {
		return this.toolPane.getValue();
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		return JsonNull.INSTANCE;
	}
	
	@Override
	protected Pane getToolPane() {
		return this.toolPane;
	}
	
	@Override
	protected Pane getConfigPane() {
		return null;
	}
	
	@Override
	protected InputStream getDragDropImageStream() {
		return FileLoader.getImageStream("faders");
	}
	
	
	private static class ToolController extends VBox {
		
		private Fadeable target;
		
		@FXML
		private Slider fader;
		
		@FXML
		private ToggleButton buttonBlack;
		
		@FXML
		private ToggleButton buttonActive;
		
		private void init() {
			this.target = Master.instance();
			this.fader.setValue(100.0D);
			
			this.buttonBlack.armedProperty().addListener((observeable, oldValue, newValue) -> {
				if (this.buttonActive.isSelected()) {
					if (newValue == true && !this.buttonBlack.isSelected()) {
						this.fireChange();
					} else if (newValue == false && !this.buttonBlack.isSelected()) {
						this.fireChange();
					}
				}
			});
			
			this.buttonActive.armedProperty().addListener((observeable, oldValue, newValue) -> {
				if (newValue == true && !this.buttonActive.isSelected()) {
					this.fireChange();
				} else if (newValue == false && !this.buttonActive.isSelected()) {
					this.fireChange();
				}
			});
			
			this.fader.valueProperty().addListener((observeable, oldValue, newValue) -> {
				if (this.buttonActive.isSelected() && !this.buttonBlack.isSelected()) {
					this.fireChange();
				}
			});
		}
		
		private void updateSlider() {
			this.fader.setShowTickMarks(false);
			this.fader.setShowTickMarks(true);
		}
		
		private void fireChange() {
			this.target.update(this.getValue());
		}
		
		private Short getValue() {
			if (!this.buttonActive.isSelected() && !this.buttonActive.isArmed()) {
				return null;
			}
			
			if (this.buttonBlack.isSelected() || this.buttonBlack.isArmed()) {
				return 0;
			}
			
			return (short) ((this.fader.getValue() / 100.0D) * 255);
		}
		
	}
	
}
