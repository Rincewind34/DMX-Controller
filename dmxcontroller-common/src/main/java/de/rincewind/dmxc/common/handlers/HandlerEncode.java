package de.rincewind.dmxc.common.handlers;

import de.rincewind.dmxc.common.packets.Packet;
import de.rincewind.dmxc.common.packets.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class HandlerEncode extends MessageToByteEncoder<Packet> {
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf buffer) throws Exception {
		try {
			byte[] data = packet.encode().array();
			int id = PacketRegistry.getId(packet);
			
			buffer.writeByte(id);
			buffer.writeInt(data.length);
			buffer.writeBytes(data);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
