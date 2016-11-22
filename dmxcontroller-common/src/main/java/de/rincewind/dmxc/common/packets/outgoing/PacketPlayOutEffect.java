package de.rincewind.dmxc.common.packets.outgoing;

import de.rincewind.dmxc.common.packets.Action;
import de.rincewind.dmxc.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayOutEffect extends PacketPlayOut {

	private Action action;

	private String effect;

	public PacketPlayOutEffect() {

	}

	public PacketPlayOutEffect(String effect, Action action) {
		this.effect = effect;
		this.action = action;
	}

	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		buffer.writeByte(this.action.getId());
		ByteUtil.writeString(buffer, this.effect);
		return buffer;
	}

	@Override
	public void decode(ByteBuf buffer) {
		this.action = Action.fromId(buffer.readByte());
		this.effect = ByteUtil.readString(buffer);
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return this.action;
	}

	public String getEffect() {
		return this.effect;
	}
	
}
