package de.rincewind.dmxc.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.rincewind.dmxc.app.api.Channel;

public class DMXHandler {
	
	private static DMXHandler instance;
	
	static {
		DMXHandler.instance = new DMXHandler();
	}
	
	public static DMXHandler get() {
		return DMXHandler.instance;
	}
	
	private List<Channel> channels;
	
	public DMXHandler() {
		this.channels = new ArrayList<>();
	}
	
	public Channel getChannel(short dmxAddress) {
		for (Channel channel : this.channels) {
			if (channel.getDmxAddress() == dmxAddress) {
				return channel;
			}
		}
		
		Channel channel = new Channel(dmxAddress);
		this.channels.add(channel);
		return channel;
	}
	
	public List<Channel> getChannels() {
		return Collections.unmodifiableList(this.channels);
	}
	
}
