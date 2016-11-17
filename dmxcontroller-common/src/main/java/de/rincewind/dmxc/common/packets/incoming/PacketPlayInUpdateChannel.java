package de.rincewind.dmxc.common.packets.incoming;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayInUpdateChannel extends PacketPlayIn {
	
	private short dmxAddress;
	private Short value;
	
	public PacketPlayInUpdateChannel() {
		
	}
	
	public PacketPlayInUpdateChannel(short dmxAddress, Short value) {
		this.dmxAddress = dmxAddress;
		this.value = value;
	}
	
	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		buffer.writeShort(this.dmxAddress);
		
		if (this.value == null) {
			buffer.writeShort(-1);
		} else {
			buffer.writeShort(this.value);
		}
		
		return buffer.copy(0, buffer.writerIndex());
	}

	@Override
	public void decode(ByteBuf buffer) {
		this.dmxAddress = buffer.readShort();
		this.value = buffer.readShort();
		
		if (this.value == -1) {
			this.value = null;
		}
	}
	
	public short getDmxAddress() {
		return this.dmxAddress;
	}
	
	public Short getValue() {
		return this.value;
	}
	
}
