package de.rincewind.dmxc.common.exceptions;

public class ChannelResourcesException extends RuntimeException {

	private static final long serialVersionUID = 8360059997110841811L;
	
	public ChannelResourcesException() {
		super("The channel resources are stil open!");
	}
	
}
