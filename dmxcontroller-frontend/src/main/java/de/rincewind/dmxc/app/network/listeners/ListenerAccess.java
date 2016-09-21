package de.rincewind.dmxc.app.network.listeners;

import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.Debug;
import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutAccess;

public class ListenerAccess implements PacketListener {

	@PacketHandler
	public void onAccess(PacketPlayOutAccess packet) {
		if (Debug.DEBUG) {
			Console.println("Access information incoming!");
			Console.println("Access Granted: " + packet.accessGranted());
			Console.println("Error: " + (!packet.accessGranted() ? packet.getError().getErrorMessage() : "None"));
			Console.println("Access-Token: " + (packet.isAccessTokenPresent() ? packet.getAccessToken() : "None"));
		}
	}
}
