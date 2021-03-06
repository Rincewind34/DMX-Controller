package de.rincewind.dmxc.common.packets.incoming;

import de.rincewind.dmxc.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayInUpdateSubmaster extends PacketPlayIn {
	
	private String submaster;
	private Short value;
	
	public PacketPlayInUpdateSubmaster() {
		
	}
	
	public PacketPlayInUpdateSubmaster(String submaster, Short value) {
		this.submaster = submaster;
		this.value = value;
	}
	
	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		ByteUtil.writeString(buffer, this.submaster);
		
		if (this.value == null) {
			buffer.writeShort(-1);
		} else {
			buffer.writeShort(this.value);
		}
		
		return buffer.copy(0, buffer.writerIndex());
	}

	@Override
	public void decode(ByteBuf buffer) {
		this.submaster = ByteUtil.readString(buffer);
		this.value = buffer.readShort();
		
		if (this.value == -1) {
			this.value = null;
		}
	}
	
	public String getSubmaster() {
		return this.submaster;
	}
	
	public Short getValue() {
		return this.value;
	}
	
}
