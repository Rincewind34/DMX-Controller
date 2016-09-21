package de.rincewind.dmxc.system.network;

import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOut;
import io.netty.channel.Channel;

public class Client {
	
	private Channel channel;
	
	public Client(Channel channel) {
		this.channel = channel;
	}
	
	public void sendPacket(PacketPlayOut packet) {
		this.channel.writeAndFlush(packet);
	}
	
	public void disconnect() {
		Server.get().diconnectClient(this);
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
}
