package de.rincewind.dmxc.system.network;

import de.rincewind.dmxc.common.Core;
import de.rincewind.dmxc.common.exceptions.ChannelActiveException;
import de.rincewind.dmxc.common.exceptions.ChannelInactiveException;
import de.rincewind.dmxc.common.exceptions.ChannelResourcesException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerCore extends Core {

	private int port;

	private Channel channel;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	public ServerCore(int port) {
		this.port = port;
	}

	@Override
	public void start() throws InterruptedException {
		if (this.isOnline()) {
			throw new ChannelActiveException();
		}

		if (this.workerGroup != null || this.bossGroup != null) {
			throw new ChannelResourcesException();
		}

		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup();

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(this.bossGroup, this.workerGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childHandler(new HandlerServerInitialize(this));
		bootstrap.option(ChannelOption.SO_BACKLOG, 20);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);

		ChannelFuture future = bootstrap.bind(this.port).sync();
		this.channel = future.channel();
	}

	@Override
	public void waitCore() throws InterruptedException {
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
		
		this.channel.close();
	}

	@Override
	public void shutdown() {
		if (this.workerGroup == null) {
			return;
		}
		
		if (this.isOnline()) {
			throw new ChannelActiveException();
		}
		
		this.workerGroup.shutdownGracefully();
		this.workerGroup = null;
		
		this.bossGroup.shutdownGracefully();
		this.bossGroup = null;
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
