package de.rincewind.dmxc.common.exceptions;

public class ChannelActiveException extends RuntimeException {

	private static final long serialVersionUID = 3319814413814402100L;
	
	public ChannelActiveException() {
		super("The channel is active!");
	}
	
}
