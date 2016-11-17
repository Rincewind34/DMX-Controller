package de.rincewind.dmxc.app.gui.dialog;

import javafx.scene.control.Dialog;

public class DialogWait extends Dialog<Void> {

	public DialogWait() {
		this.setTitle("DMXController - Verbindungsaufbau");
		this.setHeaderText("Bitte warten...");
	}
}
