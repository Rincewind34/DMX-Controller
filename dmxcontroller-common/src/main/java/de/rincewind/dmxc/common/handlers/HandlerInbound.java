package de.rincewind.dmxc.common.handlers;

import java.lang.reflect.Method;

import de.rincewind.dmxc.common.packets.Packet;
import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class HandlerInbound extends SimpleChannelInboundHandler<Packet> {
	
	protected final Listenable handler;
	
	public HandlerInbound(Listenable handler) {
		this.handler = handler;
	}
	
	protected abstract void invoke(PacketListener listener, Method method, Packet packet, Channel channel);
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Packet packet) throws Exception {
		for (PacketListener listener : this.handler.getPacketListeners()) {
			for (Method method : listener.getClass().getMethods()) {
				if (method.isAnnotationPresent(PacketHandler.class)) {
					if (method.getParameterTypes().length > 0) {
						if (method.getParameterTypes()[0] == packet.getClass()) {
							this.invoke(listener, method, packet, ctx.channel());
						} else {
							continue;
						}
					} else {
						this.throwException();
					}
				}
			}
		}
	}
	
	protected void throwException() {
		try {
			throw new RuntimeException("Unknown method-structure!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
