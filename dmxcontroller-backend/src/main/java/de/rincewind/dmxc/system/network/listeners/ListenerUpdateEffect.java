package de.rincewind.dmxc.system.network.listeners;

import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateEffect;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.network.Client;

public class ListenerUpdateEffect implements PacketListener {

	@PacketHandler
	public void onUpdate(PacketPlayInUpdateEffect packet, Client client) {
		Main.environment().getEffect(packet.getEffect()).update(client, packet.getValue());
	}
}
