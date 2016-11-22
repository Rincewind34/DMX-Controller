package de.rincewind.dmxc.common.packets.incoming;

import de.rincewind.dmxc.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayInUpdateEffect extends PacketPlayIn {
	
	private String effect;
	private Short value;
	
	public PacketPlayInUpdateEffect() {
		
	}
	
	public PacketPlayInUpdateEffect(String effect, Short value) {
		this.effect = effect;
		this.value = value;
	}
	
	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		ByteUtil.writeString(buffer, this.effect);
		
		if (this.value == null) {
			buffer.writeShort(-1);
		} else {
			buffer.writeShort(this.value);
		}
		
		return buffer.copy(0, buffer.writerIndex());
	}

	@Override
	public void decode(ByteBuf buffer) {
		this.effect = ByteUtil.readString(buffer);
		this.value = buffer.readShort();
		
		if (this.value == -1) {
			this.value = null;
		}
	}
	
	public String getEffect() {
		return this.effect;
	}
	
	public Short getValue() {
		return this.value;
	}
	
}
