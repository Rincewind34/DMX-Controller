package de.rincewind.dmxc.common.packets.incoming;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayInUpdateMaster extends PacketPlayIn {
	
	private Short value;
	
	public PacketPlayInUpdateMaster() {
		
	}
	
	public PacketPlayInUpdateMaster(Short value) {
		this.value = value;
	}
	
	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		
		if (this.value == null) {
			buffer.writeShort(-1);
		} else {
			buffer.writeShort(this.value);
		}
		
		return buffer.copy(0, buffer.writerIndex());
	}

	@Override
	public void decode(ByteBuf buffer) {
		this.value = buffer.readShort();
		
		if (this.value == -1) {
			this.value = null;
		}
	}
	
	public Short getValue() {
		return this.value;
	}
	
}
