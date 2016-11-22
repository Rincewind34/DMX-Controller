package de.rincewind.dmxc.app.network.listeners;

import de.rincewind.dmxc.app.api.Effect;
import de.rincewind.dmxc.common.packets.Action;
import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutEffect;

public class ListenerEffect implements PacketListener {

	@PacketHandler
	public void onSubmaster(PacketPlayOutEffect packet) {
		if (packet.getAction() == Action.ADD) {
			Effect.create(packet.getEffect());
		} else if (packet.getAction() == Action.REMOVE) {
			Effect.get(packet.getEffect()).delete();
		}
	}
	
}