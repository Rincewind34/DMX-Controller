package de.rincewind.dmxc.app.gui;

import de.rincewind.dmxc.app.api.FaderBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

public class HardFader extends HBox implements TemplateComponent {
	
	@FXML
	private Slider fader;
	
	@FXML
	private ToggleButton buttonPushZero;
	
	@FXML
	private ToggleButton buttonSaveMode;
	
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
	
	private double save1;
	private double save2;
	private double save3;
	private double save4;
	private double save5;
	
	private FaderBase base;
	
	public HardFader() {
		TemplateComponent.loadFXML(this, "hard-fader.fxml", "basics.css", "hardfader.css");
		
		this.button1.setOnAction((event) -> {
			if (this.buttonSaveMode.isSelected()) {
				this.save1 = this.fader.getValue();
				this.buttonSaveMode.setSelected(false);
			} else {
				this.fader.setValue(this.save1);
			}
		});
		
		this.button2.setOnAction((event) -> {
			if (this.buttonSaveMode.isSelected()) {
				this.save2 = this.fader.getValue();
				this.buttonSaveMode.setSelected(false);
			} else {
				this.fader.setValue(this.save2);
			}
		});
		
		this.button3.setOnAction((event) -> {
			if (this.buttonSaveMode.isSelected()) {
				this.save3 = this.fader.getValue();
				this.buttonSaveMode.setSelected(false);
			} else {
				this.fader.setValue(this.save3);
			}
		});
		
		this.button4.setOnAction((event) -> {
			if (this.buttonSaveMode.isSelected()) {
				this.save4 = this.fader.getValue();
				this.buttonSaveMode.setSelected(false);
			} else {
				this.fader.setValue(this.save4);
			}
		});
		
		this.button5.setOnAction((event) -> {
			if (this.buttonSaveMode.isSelected()) {
				this.save5 = this.fader.getValue();
				this.buttonSaveMode.setSelected(false);
			} else {
				this.fader.setValue(this.save5);
			}
		});
		
		this.base = new FaderBase(this.fader, null, this.buttonPushZero);
	}
	
	public FaderBase base() {
		return this.base;
	}
	
}
