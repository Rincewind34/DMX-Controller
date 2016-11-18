package de.rincewind.dmxc.app.gui.util;

import de.rincewind.dmxc.app.api.Fadeable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

public class FaderBase {
	
	private Fadeable target;
	
	private Slider fader;
	private Button flashButton;
	private ToggleButton pushZeroButton;
	
	public FaderBase(Slider fader, Button flashButton, ToggleButton pushZeroButton) {
		this.fader = fader;
		this.flashButton = flashButton;
		this.pushZeroButton = pushZeroButton;
		
		if (this.flashButton != null) {
			this.flashButton.armedProperty().addListener((observeable, oldValue, newValue) -> {
				this.fireChange();
			});
		}
		
		if (this.pushZeroButton != null) {
			this.pushZeroButton.armedProperty().addListener((observeable, oldValue, newValue) -> {
				if (this.getFaderValue() != 0) {
					return;
				}
				
				if (newValue == true && !this.pushZeroButton.isSelected()) {
					this.fireChange();
				} else if (newValue == false && !this.pushZeroButton.isSelected()) {
					this.fireChange();
				}
			});
		}
			
		this.fader.valueProperty().addListener((observeable, oldValue, newValue) -> {
			this.fireChange();
		});
	}
	
	public void setTarget(Fadeable target) {
		this.target = target;
	}
	
	public void setPushZero(boolean value) {
		if (this.pushZeroButton == null) {
			return;
		}
		
		this.pushZeroButton.setSelected(value);
	}
	
	public void setFaderValue(short value) {
		this.fader.setValue((value / 255.0D) * 100.0D);
	}
	
	public void updateSlider() {
		this.fader.setShowTickMarks(false);
		this.fader.setShowTickMarks(true);
	}
	
	public void fireChange() {
		if (this.target != null) {
			this.target.update(this.getValue());
		}
	}
	
	public boolean isFlashed() {
		return this.flashButton == null ? false : this.flashButton.isArmed();
	}
	
	public boolean pushZero() {
		return this.pushZeroButton == null ? false : (this.pushZeroButton.isArmed() || this.pushZeroButton.isSelected());
	}
	
	public boolean isFullLoad() {
		return this.isFlashed() || this.getFaderValue() == 255;
	}
	
	public short getFaderValue() {
		return (short) ((this.fader.getValue() / 100.0D) * 255);
	}
	
	public Short getValue() {
		if (this.isFlashed()) {
			return 255;
		}
		
		if (this.getFaderValue() == 0) {
			return this.pushZero() ? (short) 0 : null;
		} else {
			return this.getFaderValue();
		}
	}
	
	public Fadeable getTarget() {
		return this.target;
	}
	
}
