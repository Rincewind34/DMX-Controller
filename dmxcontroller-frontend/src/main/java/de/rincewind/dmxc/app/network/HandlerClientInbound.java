package de.rincewind.dmxc.app.network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.rincewind.dmxc.app.Main;
import de.rincewind.dmxc.app.api.Effect;
import de.rincewind.dmxc.app.api.Show;
import de.rincewind.dmxc.app.api.Submaster;
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
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Submaster.deleteAll();
		Effect.deleteAll();
		Show.instance().reset();
		Main.hideMainWindow();
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
