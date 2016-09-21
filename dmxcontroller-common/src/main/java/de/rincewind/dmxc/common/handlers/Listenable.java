package de.rincewind.dmxc.common.handlers;

import java.util.List;

import de.rincewind.dmxc.common.packets.PacketListener;

public interface Listenable {
	
	public abstract List<PacketListener> getPacketListeners();
	
}
