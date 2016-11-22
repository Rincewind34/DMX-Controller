package de.rincewind.dmxc.common.packets.outgoing;

import de.rincewind.dmxc.common.packets.Action;
import de.rincewind.dmxc.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayOutSubmaster extends PacketPlayOut {

	private Action action;

	private String submaster;

	public PacketPlayOutSubmaster() {

	}

	public PacketPlayOutSubmaster(String submaster, Action action) {
		this.submaster = submaster;
		this.action = action;
	}

	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		buffer.writeByte(this.action.getId());
		ByteUtil.writeString(buffer, this.submaster);
		return buffer;
	}

	@Override
	public void decode(ByteBuf buffer) {
		this.action = Action.fromId(buffer.readByte());
		this.submaster = ByteUtil.readString(buffer);
	}

	public void setSubmaster(String submaster) {
		this.submaster = submaster;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return this.action;
	}

	public String getSubmaster() {
		return this.submaster;
	}
	
}
