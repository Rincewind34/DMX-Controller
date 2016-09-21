package de.rincewind.dmxc.common.packets.outgoing;

import de.rincewind.dmxc.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayOutAccess extends PacketPlayOut {
	
	private boolean access;
	private String accessToken;
	private LoginError error;
	
	public PacketPlayOutAccess() {
		this.access = true;
		this.accessToken = null;
		this.error = null;
	}
	
	public PacketPlayOutAccess(String accessToken) {
		this.access = true;
		this.accessToken = accessToken;
		this.error = null;
	}
	
	public PacketPlayOutAccess(LoginError error) {
		this.access = false;
		this.error = error;
		this.accessToken = null;
	}
	
	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		
		if (this.access) {
			buffer.writeByte(0x01);
			
			if (this.isAccessTokenPresent()) {
				ByteUtil.writeFixedString(buffer, this.accessToken, 32);
			}
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
			this.accessToken = ByteUtil.readFixedString(buffer, 32);
		}
	}
	
	public LoginError getError() {
		return this.error;
	}
	
	public String getAccessToken() {
		return this.accessToken;
	}
	
	public boolean accessGranted() {
		return this.access;
	}
	
	public boolean isAccessTokenPresent() {
		return this.accessToken != null;
	}
	
	public static enum LoginError {
		
		USERNAME(0, "The username already logged in!"),
		AUTH_TIME_ELAPSED(1, "The authentication-time is elapsed!"),
		PASSWORD(2, "The password was incorrect!");
		
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
