package de.rincewind.dmxc.common.packets.outgoing;

import de.rincewind.dmxc.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayOutShow extends PacketPlayOut {

	private ShowAction action;
	
	private boolean activate;
	private SceneState state;
	private int index;
	private String scene;
	
	protected PacketPlayOutShow() {
		
	}
	
	public PacketPlayOutShow(String scene) {
		this(scene, -1);
	}

	public PacketPlayOutShow(String scene, int index) {
		this.scene = scene;
		this.index = index;
		this.action = ShowAction.ADD_SCENE;
	}

	public PacketPlayOutShow(int index) {
		this.index = index;
		this.action = ShowAction.REMOVE_SCENE;
	}

	public PacketPlayOutShow(int index, SceneState state) {
		this.index = index;
		this.state = state;
		this.action = ShowAction.CURRENT_SCENE;
	}
	
	public PacketPlayOutShow(boolean activate, int index) {
		this.activate = activate;
		this.index = index;
		this.action = ShowAction.ACTIVATE;
	}

	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		buffer.writeByte(this.action.id);

		if (this.action == ShowAction.ADD_SCENE) {
			ByteUtil.writeString(buffer, this.scene);
			buffer.writeInt(this.index);
		} else if (this.action == ShowAction.CURRENT_SCENE) {
			buffer.writeInt(this.index);
			buffer.writeByte(this.state.id);
		} else if (this.action == ShowAction.ACTIVATE) {
			buffer.writeBoolean(this.activate);
			
			if (this.activate) {
				buffer.writeInt(this.index);
			}
		}

		return buffer;
	}

	@Override
	public void decode(ByteBuf buffer) {
		this.action = ShowAction.fromId(buffer.readByte());

		if (this.action == ShowAction.ADD_SCENE) {
			this.scene = ByteUtil.readString(buffer);
			this.index = buffer.readInt();
		} else if (this.action == ShowAction.CURRENT_SCENE) {
			this.index = buffer.readInt();
			this.state = SceneState.fromId(buffer.readByte());
		} else if (this.action == ShowAction.ACTIVATE) {
			this.activate = buffer.readBoolean();
			
			if (this.activate) {
				this.index = buffer.readInt();
			}
		}
	}
	
	public boolean isActivate() {
		return this.activate;
	}
	
	public int getIndex() {
		return this.index;
	}

	public ShowAction getAction() {
		return this.action;
	}

	public String getScene() {
		return this.scene;
	}

	public SceneState getState() {
		return this.state;
	}

	public static enum ShowAction {

		ADD_SCENE((byte) 0), REMOVE_SCENE((byte) 1), CURRENT_SCENE((byte) 2), ACTIVATE((byte) 3);

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

	public static enum SceneState {

		FADING((byte) 0), STAY((byte) 1);

		private byte id;

		private SceneState(byte id) {
			this.id = id;
		}

		public byte getId() {
			return this.id;
		}

		public static SceneState fromId(byte id) {
			for (SceneState state : SceneState.values()) {
				if (state.id == id) {
					return state;
				}
			}

			return null;
		}

	}

}
