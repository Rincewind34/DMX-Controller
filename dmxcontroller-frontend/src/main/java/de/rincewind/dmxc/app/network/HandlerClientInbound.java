package de.rincewind.dmxc.app.network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.Debug;
import de.rincewind.dmxc.common.handlers.HandlerInbound;
import de.rincewind.dmxc.common.packets.Packet;
import de.rincewind.dmxc.common.packets.PacketListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class HandlerClientInbound extends HandlerInbound {

	public HandlerClientInbound(ClientCore core) {
		super(core);
	}
	
	@Override
	protected void invoke(PacketListener listener, Method method, Packet packet, Channel channel) {
		try {
			method.invoke(listener, packet);
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
		Console.println("Channel active " + ctx.channel().toString());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Console.println("Channel inactive " + ctx.channel().toString());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (Debug.DEBUG) {
			cause.printStackTrace();
		} else {
			Console.println("Internal exception: " + cause.getMessage());
		}
	}

}
