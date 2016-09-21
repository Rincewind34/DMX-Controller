package de.rincewind.dmxc.common.handlers;

import java.util.List;

import de.rincewind.dmxc.common.packets.Packet;
import de.rincewind.dmxc.common.packets.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class HandlerDecode extends ByteToMessageDecoder {

	/*
	 * Indicates, if the packet head (id and length) is already read
	 */
	private boolean headerRead = false;

	/*
	 * The id of the packet this handler is currently reading
	 */
	private byte id;

	/*
	 * The length of the packet content this handler is currently reading
	 */
	private int length;

	/*
	 * Is called, when new bytes arrive in the channel pipeline
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
		try {
			if (!this.headerRead) {
				if (buffer.readableBytes() < Packet.MINIMUM_LENGTH) {
					// The buffer does not contain the full packet head => wait
					// until the buffer contains the head
					return;
				}

				this.id = buffer.readByte();
				this.length = buffer.readInt(); // length
				this.headerRead = true;
			}
			
			// Wait, until the buffer contains the whole content for the packet to read
			
			if (buffer.readableBytes() < this.length) {
				return;
			}
			
			byte[] data = new byte[this.length];
			buffer.readBytes(data);
			this.headerRead = false;
			ByteBuf content = Unpooled.wrappedBuffer(data);

			Packet packet = PacketRegistry.newPacket(this.id);
			packet.decode(content);

			out.add(packet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
