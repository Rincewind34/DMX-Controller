package de.rincewind.dmxc.common.util;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

public class ByteUtil {

	public static void writeString(ByteBuf buffer, String value) {
		byte[] data = value.getBytes(Charset.forName("UTF-8"));
		buffer.writeShort(data.length);
		buffer.writeBytes(data);
	}
	
	public static String readString(ByteBuf buffer) {
		return ByteUtil.readFixedString(buffer, buffer.readShort());
	}
	
	public static void writeFixedString(ByteBuf buffer, String value, int length) {
		byte[] data = value.getBytes(Charset.forName("UTF-8"));
		
		for (int i = 0; i < length; i++) {
			if (i < data.length) {
				buffer.writeByte(data[i]);
			} else {
				buffer.writeByte(0);
			}
		}
	}
	
	public static String readFixedString(ByteBuf buffer, int length) {
		byte[] data = new byte[length];
		buffer.readBytes(data);
		return new String(data, Charset.forName("UTF-8"));
	}
}
