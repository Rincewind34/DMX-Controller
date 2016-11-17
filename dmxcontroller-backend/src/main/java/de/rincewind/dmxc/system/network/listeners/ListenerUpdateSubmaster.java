package de.rincewind.dmxc.system.network.listeners;

import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateSubmaster;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.network.Client;

public class ListenerUpdateSubmaster implements PacketListener {

	@PacketHandler
	public void onUpdate(PacketPlayInUpdateSubmaster packet, Client client) {
		Main.environment().getSubmaster(packet.getSubmaster()).update(client, packet.getValue());
	}
}
