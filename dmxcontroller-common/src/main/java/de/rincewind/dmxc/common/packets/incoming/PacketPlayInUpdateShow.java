package de.rincewind.dmxc.common.packets.incoming;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayInUpdateShow extends PacketPlayIn {

	private ShowAction action;

	private boolean activate;
	private int index;
	private Short value;
	
	protected PacketPlayInUpdateShow() {
		
	}
	
	public PacketPlayInUpdateShow(Short value) {
		this.value = value;
		this.action = ShowAction.UPDATE_FADER;
	}

	public PacketPlayInUpdateShow(boolean activate) {
		this.activate = activate;
		this.action = ShowAction.ACTIVATE;
	}

	public PacketPlayInUpdateShow(int index) {
		this.index = index;
		this.action = ShowAction.SET_SCENE;
	}

	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		buffer.writeByte(this.action.id);
		
		if (this.action == ShowAction.UPDATE_FADER) {
			if (this.value == null) {
				buffer.writeShort(-1);
			} else {
				buffer.writeShort(this.value);
			}
		} else if (this.action == ShowAction.ACTIVATE) {
			buffer.writeBoolean(this.activate);
		} else {
			buffer.writeInt(this.index);
		}

		return buffer;
	}

	@Override
	public void decode(ByteBuf buffer) {
		this.action = ShowAction.fromId(buffer.readByte());
		
		if (this.action == ShowAction.UPDATE_FADER) {
			this.value = buffer.readShort();
	
			if (this.value == -1) {
				this.value = null;
			}
		} else if (this.action == ShowAction.ACTIVATE) {
			this.activate = buffer.readBoolean();
		} else {
			this.index = buffer.readInt();
		}
	}

	public ShowAction getAction() {
		return this.action;
	}

	public boolean isActivate() {
		return this.activate;
	}

	public int getIndex() {
		return this.index;
	}

	public Short getValue() {
		return this.value;
	}

	public static enum ShowAction {

		UPDATE_FADER((byte) 0), ACTIVATE((byte) 1), SET_SCENE((byte) 2);

		private byte id;

		private ShowAction(byte id) {
			this.id = id;
		}

		public byte getId() {
			return this.id;
		}

		public static ShowAction fromId(byte id) {
			for (ShowAction action : ShowAction.values()) {
				if (action.id == id) {
					return action;
				}
			}

			return null;
		}

	}

}
