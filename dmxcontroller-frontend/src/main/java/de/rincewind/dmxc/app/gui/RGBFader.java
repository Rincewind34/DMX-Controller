package de.rincewind.dmxc.app.gui;

import de.rincewind.dmxc.app.gui.util.FaderBase;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class RGBFader extends TemplateComponent {
	
	private ToolController toolController;
	
	public RGBFader() {
		this.toolController = new ToolController();
		FileLoader.loadFXML(this.toolController, "rgb-fader.fxml", "basics.css", "colorfader.css");
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
	
}
