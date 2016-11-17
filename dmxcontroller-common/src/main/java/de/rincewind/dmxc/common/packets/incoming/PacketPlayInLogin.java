package de.rincewind.dmxc.common.packets.incoming;

import de.rincewind.dmxc.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketPlayInLogin extends PacketPlayIn {
	
	private String username;
	private String password;
	
	public PacketPlayInLogin() {
		
	}
	
	public PacketPlayInLogin(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	public ByteBuf encode() {
		ByteBuf buffer = Unpooled.buffer();
		ByteUtil.writeString(buffer, this.username);
		ByteUtil.writeString(buffer, this.password);
		return buffer.copy(0, buffer.writerIndex());
	}

	@Override
	public void decode(ByteBuf buffer) {
		this.username = ByteUtil.readString(buffer);
		this.password = ByteUtil.readString(buffer);
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
