package de.rincewind.dmxc.app.gui.util;

import java.util.ArrayList;
import java.util.List;

import de.rincewind.dmxc.app.api.Channel;
import de.rincewind.dmxc.app.api.ChannelSelection;
import de.rincewind.dmxc.app.api.Fadeable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class NumberPadInterpreter {
	
	private Button button0;
	
	private Button button1;
	
	private Button button2;
	
	private Button button3;
	
	private Button button4;
	
	private Button button5;
	
	private Button button6;
	
	private Button button7;
	
	private Button button8;
	
	private Button button9;
	
	private Button buttonPlus;
	
	private Button buttonThru;
	
	private Button buttonClear;
	
	private Button buttonBack;
	
	private Label display;
	
	private Runnable action;
	
	public NumberPadInterpreter(Button button0, Button button1, Button button2, Button button3, Button button4, Button button5, Button button6, Button button7,
			Button button8, Button button9, Button buttonPlus, Button buttonThru, Button buttonClear, Button buttonBack, Label display) {
		
		this.button0 = button0;
		this.button1 = button1;
		this.button2 = button2;
		this.button3 = button3;
		this.button4 = button4;
		this.button5 = button5;
		this.button6 = button6;
		this.button7 = button7;
		this.button8 = button8;
		this.button9 = button9;
		this.buttonPlus = buttonPlus;
		this.buttonThru = buttonThru;
		this.buttonClear = buttonClear;
		this.buttonBack = buttonBack;
		this.display = display;
		
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
			this.fireAction();
		});
		
		this.buttonClear.setOnAction((event) -> {
			this.display.setText("");
			this.updateButtons();
			this.fireAction();
		});
		
		this.updateButtons();
	}
	
	public void setAction(Runnable action) {
		this.action = action;
	}
	
	public Fadeable getSelection() {
		ChannelSelection selection = this.getSelectedChannels();
		
		if (selection.size() == 0) {
			return null;
		} else if (selection.size() == 1) {
			return selection.toChannel();
		} else {
			return selection;
		}
	}
	
	public ChannelSelection getSelectedChannels() {
		List<Channel> selection = new ArrayList<>();
		
		if (this.display.getText() == null || this.display.getText().equals("")) {
			return new ChannelSelection();
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
					selection.add(Channel.fromAddress(startAddress));
					startAddress = (short) (startAddress + 1);
				}
			} else {
				short dmxAddress;
				
				try {
					dmxAddress = Short.parseShort(element);
				} catch (NumberFormatException exception) {
					continue;
				}
				
				if (dmxAddress < 1 || dmxAddress > 512) {
					continue;
				}
				
				selection.add(Channel.fromAddress(dmxAddress));
			}
		}
		
		return new ChannelSelection(selection);
	}
	
	private void updateButtons() {
		boolean disableClear = this.display.getText() == null || this.display.getText().equals("");
		boolean disableSwitch = disableClear || this.display.getText().endsWith("+") || this.display.getText().endsWith("-");
		
		this.buttonBack.setDisable(disableClear);
		this.buttonClear.setDisable(disableClear);
		
		this.buttonPlus.setDisable(disableSwitch);
		this.buttonThru.setDisable(disableSwitch);
		
		int lastNumber = this.getLastNumber();
		
		if (lastNumber != -1) {
			if (lastNumber > 51) {
				this.setNumbersDisabled(true);
			} else if (lastNumber == 51) {
				this.setHalfNumbersDisabled(true);
			} else {
				this.setNumbersDisabled(false);
			}
		} else {
			this.setNumbersDisabled(false);
		}
		
		this.fireAction();
	}
	
	private void setupButton(Button button, String text) {
		button.setOnAction((event) -> {
			this.display.setText(this.display.getText() == null ? text : this.display.getText() + text);
			this.updateButtons();
		});
	}
	
	private void setNumbersDisabled(boolean value) {
		this.button0.setDisable(value);
		this.button1.setDisable(value);
		this.button2.setDisable(value);
		this.setHalfNumbersDisabled(value);
	}
	
	private void setHalfNumbersDisabled(boolean value) {
		this.button3.setDisable(value);
		this.button4.setDisable(value);
		this.button5.setDisable(value);
		this.button6.setDisable(value);
		this.button7.setDisable(value);
		this.button8.setDisable(value);
		this.button9.setDisable(value);
	}
	
	private void fireAction() {
		if (this.action != null) {
			this.action.run();
		}
	}
	
	private int getLastNumber() {
		String number = "";
		String copy = this.display.getText();
		
		while (!copy.isEmpty() && !copy.endsWith("+") && !copy.endsWith("-")) {
			number = copy.charAt(copy.length() - 1) + number;
			copy = copy.substring(0, copy.length() - 1);
		}
		
		if (number.equals("")) {
			return -1;
		}
		
		return Integer.parseInt(number);
	}
	
}
