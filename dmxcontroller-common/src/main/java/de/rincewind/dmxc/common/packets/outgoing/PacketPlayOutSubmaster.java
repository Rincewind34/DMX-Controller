package de.rincewind.dmxc.common.packets.outgoing;

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
		buffer.writeByte(this.action.id);
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
	

	public static enum Action {

		ADD((byte) 0), REMOVE((byte) 1);
		
		private byte id;
		
		private Action(byte id) {
			this.id = id;
		}
		
		public byte getId() {
			return this.id;
		}
		
		public static Action fromId(byte id) {
			for (Action action : Action.values()) {
				if (action.id == id) {
					return action;
				}
			}
			
			return null;
		}
		
	}

}
