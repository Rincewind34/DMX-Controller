package de.rincewind.dmxc.system.network.listeners;

import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateChannel;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.network.Client;

public class ListenerUpdateChannel implements PacketListener {

	@PacketHandler
	public void onUpdate(PacketPlayInUpdateChannel packet, Client client) {
		Main.environment().update(client, packet.getDmxAddress(), packet.getValue());
	}
}
