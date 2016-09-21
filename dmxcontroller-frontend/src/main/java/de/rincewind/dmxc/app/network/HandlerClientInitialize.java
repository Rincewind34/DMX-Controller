package de.rincewind.dmxc.app.network;

import de.rincewind.dmxc.common.handlers.HandlerInbound;
import de.rincewind.dmxc.common.handlers.HandlerInitialize;

public class HandlerClientInitialize extends HandlerInitialize {
	
	private ClientCore core;
	
	public HandlerClientInitialize(ClientCore core) {
		this.core = core;
	}
	
	@Override
	protected HandlerInbound newInboundHandler() {
		return new HandlerClientInbound(this.core);
	}

}
