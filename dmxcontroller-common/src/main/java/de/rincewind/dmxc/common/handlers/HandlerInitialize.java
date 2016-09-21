package de.rincewind.dmxc.common.handlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public abstract class HandlerInitialize extends ChannelInitializer<SocketChannel> {
	
	protected abstract HandlerInbound newInboundHandler();
	
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		channel.pipeline().addLast(new HandlerEncode());
		
		channel.pipeline().addLast(new HandlerDecode());
		channel.pipeline().addLast(this.newInboundHandler());
	}
}
