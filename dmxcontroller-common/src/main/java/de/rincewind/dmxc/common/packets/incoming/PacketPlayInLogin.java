package de.rincewind.dmxc.common.packets.incoming;

import de.rincewind.dmxc.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayInLogin extends PacketPlayIn {
	
	private String username;
	private String passwordHash;
	
	public PacketPlayInLogin() {
		
	}
	
	public PacketPlayInLogin(String username, String passwordHash) {
		this.username = username;
		this.passwordHash = passwordHash;
	}
	
	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		ByteUtil.writeString(buffer, this.username);
		ByteUtil.writeFixedString(buffer, this.passwordHash, 32);
		return buffer.copy(0, buffer.writerIndex());
	}

	@Override
	public void decode(ByteBuf buffer) {
		this.username = ByteUtil.readString(buffer);
		this.passwordHash = ByteUtil.readFixedString(buffer, 32);
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPasswordHash() {
		return this.passwordHash;
	}
	
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
}
