package de.rincewind.dmxc.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class ChannelSelection extends Fadeable {
	
	private List<Channel> channels;
	
	public ChannelSelection(Channel... channels) {
		this(Arrays.asList(channels));
	}
	
	public ChannelSelection(List<Channel> channels) {
		this.channels = channels;
	}
	
	protected ChannelSelection(JsonElement element) {
		JsonArray array = element.getAsJsonArray();
		List<Channel> channels = new ArrayList<>();
		
		for (int i = 0; i < array.size(); i++) {
			channels.add(Channel.fromJson(array.get(i)));
		}
		
		this.channels = channels;
	}
	
	@Override
	public void update(Short value) {
		for (Channel channel : this.channels) {
			channel.update(value);
		}
	}
	
	@Override
	public String getType() {
		return "channelselection";
	}
	
	@Override
	public String toString() {
		return "Channels " + this.channels.toString();
	}
	
	public int size() {
		return this.channels.size();
	}
	
	public Channel toChannel() {
		return this.channels.get(0);
	}
	
	public List<Channel> getChannels() {
		return Collections.unmodifiableList(this.channels);
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		JsonArray array = new JsonArray();
		
		for (Channel channel : this.channels) {
			array.add(channel.serializeSimplified());
		}
		
		return array;
	}
	
}
