package de.rincewind.dmxc.system.network;

import de.rincewind.dmxc.common.handlers.HandlerInbound;
import de.rincewind.dmxc.common.handlers.HandlerInitialize;

public class HandlerServerInitialize extends HandlerInitialize {
	
	private ServerCore core;
	
	public HandlerServerInitialize(ServerCore core) {
		this.core = core;
	}
	
	@Override
	protected HandlerInbound newInboundHandler() {
		return new HandlerServerInbound(this.core);
	}
	
}
