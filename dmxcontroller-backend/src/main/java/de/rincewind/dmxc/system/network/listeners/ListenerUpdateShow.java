package de.rincewind.dmxc.system.network.listeners;

import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateShow;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateShow.ShowAction;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.network.Client;

public class ListenerUpdateShow implements PacketListener {

	@PacketHandler
	public void onUpdate(PacketPlayInUpdateShow packet, Client client) {
		if (packet.getAction() == ShowAction.UPDATE_FADER) {
			Main.environment().getShow().update(client, packet.getValue());
		} else if (packet.getAction() == ShowAction.ACTIVATE) {
			Main.environment().getShow().setRunning(packet.isActivate());
		} else {
			Main.environment().getShow().setCurrentIndex(packet.getIndex());
		}
	}
}
