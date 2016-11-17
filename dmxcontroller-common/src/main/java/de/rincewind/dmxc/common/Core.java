package de.rincewind.dmxc.common;

import java.util.ArrayList;
import java.util.List;

import de.rincewind.dmxc.common.handlers.Listenable;
import de.rincewind.dmxc.common.packets.PacketListener;

public abstract class Core implements Listenable {
	
	private List<PacketListener> listeners;
	
	public Core() {
		this.listeners = new ArrayList<>();
	}
	
	public abstract void start() throws InterruptedException;
	
	public abstract void waitCore() throws InterruptedException;
	
	public abstract void stop();
	
	public abstract void shutdown();
	
	
	public abstract boolean isOnline();
	
	@Override
	public List<PacketListener> getPacketListeners() {
		return this.listeners;
	}
	
	public void addListener(PacketListener listener) {
		this.listeners.add(listener);
	}
	
}
