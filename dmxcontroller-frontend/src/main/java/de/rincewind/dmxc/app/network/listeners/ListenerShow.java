package de.rincewind.dmxc.app.network.listeners;

import de.rincewind.dmxc.app.api.Show;
import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutShow;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutShow.ShowAction;

public class ListenerShow implements PacketListener {

	@PacketHandler
	public void onSubmaster(PacketPlayOutShow packet) {
		if (packet.getAction() == ShowAction.ADD_SCENE) {
			if (packet.getIndex() == -1) {
				Show.instance().addScene(packet.getScene());
			} else {
				Show.instance().addScene(packet.getScene(), packet.getIndex());
			}
		} else if (packet.getAction() == ShowAction.REMOVE_SCENE) {
			Show.instance().removeScene(packet.getIndex());
		} else if (packet.getAction() == ShowAction.CURRENT_SCENE) {
			Show.instance().setScene(packet.getIndex(), packet.getState());
		} else if (packet.getAction() == ShowAction.ACTIVATE) {
			if (packet.isActivate()) {
				Show.instance().setActivated(true, packet.getIndex());
			} else {
				Show.instance().setActivated(false, -1);
			}
		}
	}
}
