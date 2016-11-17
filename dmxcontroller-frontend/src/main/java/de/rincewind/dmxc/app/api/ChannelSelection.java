package de.rincewind.dmxc.app.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChannelSelection implements Fadeable {
	
	private List<Channel> channels;
	
	public ChannelSelection(Channel... channels) {
		this(Arrays.asList(channels));
	}
	
	public ChannelSelection(List<Channel> channels) {
		this.channels = channels;
	}
	
	@Override
	public void update(Short value) {
		for (Channel channel : this.channels) {
			channel.update(value);
		}
	}
	
	public List<Channel> getChannels() {
		return Collections.unmodifiableList(channels);
	}
	
}
