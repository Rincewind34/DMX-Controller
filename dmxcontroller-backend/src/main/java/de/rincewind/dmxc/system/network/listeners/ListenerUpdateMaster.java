package de.rincewind.dmxc.system.network.listeners;

import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateMaster;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.network.Client;

public class ListenerUpdateMaster implements PacketListener {

	@PacketHandler
	public void onUpdate(PacketPlayInUpdateMaster packet, Client client) {
		Main.environment().updateMaster(client, packet.getValue());
	}
}
