package de.rincewind.dmxc.system.network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.rincewind.dmxc.common.Debug;
import de.rincewind.dmxc.common.handlers.HandlerInbound;
import de.rincewind.dmxc.common.packets.Packet;
import de.rincewind.dmxc.common.packets.PacketListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class HandlerServerInbound extends HandlerInbound {
	
	public HandlerServerInbound(ServerCore core) {
		super(core);
	}
	
	@Override
	protected void invoke(PacketListener listener, Method method, Packet packet, Channel channel) {
		try {
			method.invoke(listener, packet, Server.get().getClient(channel));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Channel active " + ctx.channel().toString());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Channel inactive " + ctx.channel().toString());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (Debug.DEBUG) {
			cause.printStackTrace();
		} else {
			System.err.println("Internal exception: " + cause.getMessage());
		}
	}
	
}
