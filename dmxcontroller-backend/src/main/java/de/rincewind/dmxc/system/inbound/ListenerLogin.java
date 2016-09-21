package de.rincewind.dmxc.system.inbound;

import de.rincewind.dmxc.common.Debug;
import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInLogin;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutAccess;
import de.rincewind.dmxc.system.network.Client;

public class ListenerLogin implements PacketListener {

	@PacketHandler
	public void onLogin(PacketPlayInLogin packet, Client client) {
		if (Debug.DEBUG) {
			System.out.println("A client tries to login");
			System.out.println("Username: " + packet.getUsername());
			System.out.println("Password: " + packet.getPasswordHash());
		}
		
		client.sendPacket(new PacketPlayOutAccess());
	}
}
