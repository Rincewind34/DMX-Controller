package de.rincewind.dmxc.app.gui;

import de.rincewind.dmxc.app.api.ChannelSelection;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import de.rincewind.dmxc.app.gui.util.NumberPadInterpreter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class NumberPad extends TemplateComponent {

	private ToolController toolController;

	public NumberPad() {
		this.toolController = new ToolController();

		FileLoader.loadFXML(this.toolController, "numberpad.fxml", "basics.css", "numberpad.css");
		this.toolController.init();
	}

	public ChannelSelection getSelectedChannels() {
		return this.toolController.interpreter.getSelectedChannels();
	}
	
	@Override
	protected Pane getToolPane() {
		return this.toolController;
	}
	
	@Override
	protected Pane getConfigPane() {
		return new Pane();
	}

	private static class ToolController extends VBox {

		@FXML
		private Button button0;

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
		private Button button6;

		@FXML
		private Button button7;

		@FXML
		private Button button8;

		@FXML
		private Button button9;

		@FXML
		private Button buttonPlus;

		@FXML
		private Button buttonThru;

		@FXML
		private Button buttonClear;

		@FXML
		private Button buttonBack;

		@FXML
		private Label display;

		private NumberPadInterpreter interpreter;

		private void init() {
			this.interpreter = new NumberPadInterpreter(this.button0, this.button1, this.button2, this.button3, this.button4, this.button5, this.button6,
					this.button7, this.button8, this.button9, this.buttonPlus, this.buttonThru, this.buttonClear, this.buttonBack, this.display);
		}
		
	}

}
