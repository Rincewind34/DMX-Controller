package de.rincewind.dmxc.common.packets.outgoing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayOutAccess extends PacketPlayOut {
	
	private boolean access;
	private LoginError error;
	
	public PacketPlayOutAccess() {
		this.access = true;
		this.error = null;
	}
	
	public PacketPlayOutAccess(LoginError error) {
		this.access = false;
		this.error = error;
	}
	
	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		
		if (this.access) {
			buffer.writeByte(0x01);
		} else {
			buffer.writeByte(0x00);
			buffer.writeByte(this.error.getId());
		}
		
		return buffer.copy(0, buffer.writerIndex());
	}

	@Override
	public void decode(ByteBuf buffer) {
		if (buffer.readableBytes() == 1) {
			buffer.skipBytes(1);
			this.access = true;
		} else if (buffer.readableBytes() == 2) {
			buffer.skipBytes(1);
			this.access = false;
			this.error = LoginError.getById(buffer.readByte());
		} else if (buffer.readableBytes() == 33) {
			buffer.skipBytes(1);
			this.access = true;
		}
	}
	
	public LoginError getError() {
		return this.error;
	}
	
	public boolean accessGranted() {
		return this.access;
	}
	
	
	public static enum LoginError {
		
		INVALID_USERNAME(0, "Der Nutzername existiert nicht!"),
		USERNAME(1, "Der Nutzername wird bereits benutzt!"),
		AUTH_TIME_ELAPSED(2, "Die Verbindung hat die Zeit überschritten!"),
		PASSWORD(3, "Das Passwort ist falsch!");
		
		private byte id;
		private String errorMessage;
		
		private LoginError(int id, String errorMessage) {
			this.id = (byte) id;
			this.errorMessage = errorMessage;
		}
		
		public byte getId() {
			return this.id;
		}
		
		public String getErrorMessage() {
			return this.errorMessage;
		}
		
		public static LoginError getById(int id) {
			for (LoginError value : values()) {
				if (value.id == id) {
					return value;
				}
			}
			return null;
		}
	}

}
