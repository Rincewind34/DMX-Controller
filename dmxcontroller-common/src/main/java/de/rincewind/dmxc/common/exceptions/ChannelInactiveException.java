package de.rincewind.dmxc.common.exceptions;

public class ChannelInactiveException extends RuntimeException {

	private static final long serialVersionUID = -4240353000986350645L;
	
	public ChannelInactiveException() {
		super("The channel is inactive!");
	}
	
}
