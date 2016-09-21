package de.rincewind.dmxc.app.gui;

import java.util.ArrayList;
import java.util.List;

import de.rincewind.dmxc.app.DMXHandler;
import de.rincewind.dmxc.app.api.Channel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class NumberPad extends VBox implements TemplateComponent {
	
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
	
	public NumberPad() {
		TemplateComponent.loadFXML(this, "numberpad.fxml", "basics.css", "numberpad.css");
		
		this.setupButton(this.button0, "0");
		this.setupButton(this.button1, "1");
		this.setupButton(this.button2, "2");
		this.setupButton(this.button3, "3");
		this.setupButton(this.button4, "4");
		this.setupButton(this.button5, "5");
		this.setupButton(this.button6, "6");
		this.setupButton(this.button7, "7");
		this.setupButton(this.button8, "8");
		this.setupButton(this.button9, "9");
		this.setupButton(this.buttonPlus, "+");
		this.setupButton(this.buttonThru, "-");
		
		this.buttonBack.setOnAction((event) -> {
			this.display.setText(this.display.getText().substring(0, this.display.getText().length() - 1));
			this.updateButtons();
		});
		
		this.buttonClear.setOnAction((event) -> {
			this.display.setText("");
			this.updateButtons();
		});
		
		this.updateButtons();
	}
	
	public Channel[] getSelectedChannels() {
		List<Channel> selection = new ArrayList<>();
		
		if (this.display.getText() == null || this.display.getText().equals("")) {
			return selection.toArray(new Channel[0]);
		}
		
		String[] input = this.display.getText().split("\\+");
		
		for (String element : input) {
			if (element.contains("-")) {
				String[] addresses = element.split("\\-");
				
				short startAddress;
				short endAddress;
				
				try {
					startAddress = Short.parseShort(addresses[0]);
					endAddress = Short.parseShort(addresses[addresses.length - 1]);
				} catch (NumberFormatException exception) {
					continue;
				}
				
				if (startAddress < 0 || endAddress < 0) {
					continue;
				}
				
				while (startAddress <= endAddress) {
					selection.add(DMXHandler.get().getChannel(startAddress));
					startAddress = (short) (startAddress + 1);
				}
			} else {
				short dmxAddress;
				
				try {
					dmxAddress = Short.parseShort(element);
				} catch (NumberFormatException exception) {
					continue;
				}
				
				if (dmxAddress < 0) {
					continue;
				}
				
				selection.add(DMXHandler.get().getChannel(dmxAddress));
			}
		}
		
		return selection.toArray(new Channel[selection.size()]);
	}
	
	private void updateButtons() {
		boolean disableClear = this.display.getText() == null || this.display.getText().equals("");
		boolean disableSwitch = disableClear || this.display.getText().endsWith("+") || this.display.getText().endsWith("-");
		
		this.buttonBack.setDisable(disableClear);
		this.buttonClear.setDisable(disableClear);
		
		this.buttonPlus.setDisable(disableSwitch);
		this.buttonThru.setDisable(disableSwitch);
		
		getSelectedChannels();
	}
	
	private void setupButton(Button button, String text) {
		button.setOnAction((event) -> {
			this.display.setText(this.display.getText() == null ? text : this.display.getText() + text);
			this.updateButtons();
		});
	}
	
}
