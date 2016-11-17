package de.rincewind.dmxc.app.gui;

import de.rincewind.dmxc.app.gui.util.FileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class SceneTool extends TemplateComponent {
	
	private ToolController toolController;
	
	public SceneTool() {
		this.toolController = new ToolController();
		FileLoader.loadFXML(this.toolController, "scene-tool.fxml", "basics.css", "scenetool.css");
		this.toolController.init();
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
		private ToggleButton buttonRecord;
		
		@FXML
		private Button buttonClear;
		
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
		private Slider faderIn;
		
		@FXML
		private Slider faderOut;
		
		private String[] scenes;
		
		private void init() {
			this.scenes = new String[5];
			
			this.buttonClear.setOnAction((event) -> {
				for (int i = 0; i < this.scenes.length; i++) {
					this.scenes[i] = null;
				}
			});
			
			this.button1.setOnAction((event) -> {
				if (this.buttonRecord.isSelected()) {
					this.scenes[0] = this.newScene();
					this.buttonRecord.setSelected(false);
				} else {
					this.fadeScene(this.scenes[0], this.faderOut.getValue(), this.faderIn.getValue());
				}
			});
			
			this.button2.setOnAction((event) -> {
				if (this.buttonRecord.isSelected()) {
					this.scenes[1] = this.newScene();
					this.buttonRecord.setSelected(false);
				} else {
					this.fadeScene(this.scenes[1], this.faderOut.getValue(), this.faderIn.getValue());
				}
			});
			
			this.button3.setOnAction((event) -> {
				if (this.buttonRecord.isSelected()) {
					this.scenes[2] = this.newScene();
					this.buttonRecord.setSelected(false);
				} else {
					this.fadeScene(this.scenes[2], this.faderOut.getValue(), this.faderIn.getValue());
				}
			});
			
			this.button4.setOnAction((event) -> {
				if (this.buttonRecord.isSelected()) {
					this.scenes[3] = this.newScene();
					this.buttonRecord.setSelected(false);
				} else {
					this.fadeScene(this.scenes[3], this.faderOut.getValue(), this.faderIn.getValue());
				}
			});
			
			this.button5.setOnAction((event) -> {
				if (this.buttonRecord.isSelected()) {
					this.scenes[4] = this.newScene();
					this.buttonRecord.setSelected(false);
				} else {
					this.fadeScene(this.scenes[4], this.faderOut.getValue(), this.faderIn.getValue());
				}
			});
		}
		
		private void fadeScene(String scene, double fadeOut, double fadeIn) {
			
		}
		
		private String newScene() {
			return null;
		}
		
	}
	
}
