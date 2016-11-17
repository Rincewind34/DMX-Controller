package de.rincewind.dmxc.app.network.listeners;

import de.rincewind.dmxc.app.Main;
import de.rincewind.dmxc.app.gui.dialog.Credential;
import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutAccess;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutAccess.LoginError;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ListenerAccess implements PacketListener {

	@PacketHandler
	public void onAccess(PacketPlayOutAccess packet) {
		if (packet.accessGranted()) {
			Console.println("Login data accepted");
			Console.println("Rendering main view");
			Main.showMainWindow();
		} else {
			Platform.runLater(() -> {
				Credential credential;
				
				if (packet.getError() == LoginError.PASSWORD) {
					credential = new Credential(Main.client().getHostname(), Main.client().getPort(), Main.client().getUsername());
				} else {
					credential = new Credential(Main.client().getHostname(), Main.client().getPort());
				}
				
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(packet.getError().getErrorMessage());
				Main.connectResponse(alert, credential);
			});
		}
	}
}
