package de.rincewind.dmxc.app.network.listeners;

import de.rincewind.dmxc.app.api.Submaster;
import de.rincewind.dmxc.common.packets.Action;
import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutSubmaster;

public class ListenerSubmaster implements PacketListener {

	@PacketHandler
	public void onSubmaster(PacketPlayOutSubmaster packet) {
		if (packet.getAction() == Action.ADD) {
			Submaster.create(packet.getSubmaster());
		} else if (packet.getAction() == Action.REMOVE) {
			Submaster.get(packet.getSubmaster()).delete();
		}
	}
}
