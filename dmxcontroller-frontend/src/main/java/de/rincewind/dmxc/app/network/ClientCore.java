package de.rincewind.dmxc.app.network;

import de.rincewind.dmxc.common.Core;
import de.rincewind.dmxc.common.exceptions.ChannelActiveException;
import de.rincewind.dmxc.common.exceptions.ChannelInactiveException;
import de.rincewind.dmxc.common.exceptions.ChannelResourcesException;
import de.rincewind.dmxc.common.handlers.Listenable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientCore extends Core implements Listenable {

	private int port;
	private String hostname;
	
	private EventLoopGroup workerGroup;
	
	private Channel channel;
	
	public ClientCore(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	@Override
	public synchronized void start() throws InterruptedException {
		if (this.isOnline()) {
			throw new ChannelActiveException();
		}
		
		if (this.workerGroup != null) {
			throw new ChannelResourcesException();
		}
		
		this.workerGroup = new NioEventLoopGroup();
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(this.workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.handler(new HandlerClientInitialize(this));
		
		ChannelFuture future = bootstrap.connect(this.hostname, this.port).sync();
		this.channel = future.channel();
	}

	@Override
	public synchronized void waitCore() throws InterruptedException {
		if (!this.isOnline()) {
			throw new ChannelInactiveException();
		}
		
		this.channel.closeFuture().sync();
	}

	@Override
	public void stop() {
		if (!this.isOnline()) {
			throw new ChannelInactiveException();
		}
		
		try {
			this.channel.close().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void shutdown() {
		if (this.workerGroup == null) {
			return;
		}
		
		if (this.isOnline()) {
			throw new ChannelActiveException();
		}
		
		this.workerGroup.shutdownGracefully();
		this.workerGroup = null;
	}

	@Override
	public boolean isOnline() {
		if (this.channel == null) {
			return false;
		}
		
		return this.channel.isOpen();
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
}