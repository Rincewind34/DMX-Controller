package de.rincewind.dmxc.system.network;

import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOut;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutAccess.LoginError;
import de.rincewind.dmxc.system.Account;

public class ServerClient extends Client {
	
	private static ServerClient instance;
	
	static {
		ServerClient.instance = new ServerClient();
	}
	
	public static ServerClient instance() {
		return ServerClient.instance;
	}
	
	
	private ServerClient() {
		super(null);
		
		this.interruptElapseThread();
	}
	
	@Override
	public void verify(Account account) {
		
	}
	
	@Override
	public void disallow(LoginError error) {
		
	}
	
	@Override
	public void sendPacket(PacketPlayOut packet) {
		
	}
	
	@Override
	public void disconnect() {
		
	}
	
	@Override
	public boolean isVerified() {
		return true;
	}
	
	@Override
	public boolean isConnected() {
		return true;
	}
	
	@Override
	public boolean isBefore(Client target) {
		return true;
	}

}
