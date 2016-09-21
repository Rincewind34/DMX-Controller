package de.rincewind.dmxc.common.packets;

import io.netty.buffer.ByteBuf;

public interface Packet {
	
	public static final int MINIMUM_LENGTH = 5;
	
	
	public abstract ByteBuf encode();
	
	public abstract void decode(ByteBuf buffer);
	
}
