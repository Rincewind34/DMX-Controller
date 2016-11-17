package de.rincewind.dmxc.app.gui;

import de.rincewind.dmxc.app.gui.util.FaderBase;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class RGBWFader extends TemplateComponent {
	
	private ToolController toolController;
	
	public RGBWFader() {
		this.toolController = new ToolController();
		FileLoader.loadFXML(this.toolController, "rgbw-fader.fxml", "basics.css", "colorfader.css");
		this.toolController.init();
	}
	
	public FaderBase redBase() {
		return this.toolController.redBase;
	}
	
	public FaderBase greenBase() {
		return this.toolController.greenBase;
	}
	
	public FaderBase blueBase() {
		return this.toolController.blueBase;
	}
	
	public FaderBase whiteBase() {
		return this.toolController.whiteBase;
	}
	
	@Override
	protected Pane getToolPane() {
		return this.toolController;
	}
	
	@Override
	protected Pane getConfigPane() {
		return new Pane();
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
	
}
