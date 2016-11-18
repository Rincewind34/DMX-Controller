package de.rincewind.dmxc.app.api;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import de.rincewind.dmxc.app.Main;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateChannel;

public class Channel extends Fadeable {
	
	private static Map<Short, Channel> channels;
	
	static {
		Channel.channels = new HashMap<>();
	}
	
	public static Channel fromAddress(short dmxAddress) {
		if (dmxAddress < 1 || dmxAddress > 512) {
			throw new RuntimeException("Invalid dmxaddress!");
		}
		
		if (!Channel.channels.containsKey(dmxAddress)) {
			Channel.channels.put(dmxAddress, new Channel(dmxAddress));
		}
		
		return Channel.channels.get(dmxAddress);
	}
	
	protected static Channel fromJson(JsonElement element) {
		return Channel.fromAddress(element.getAsShort());
	}
	
	
	private short dmxAddress;
	
	private Channel(short dmxAddress) {
		this.dmxAddress = dmxAddress;
	}
	
	@Override
	public void update(Short value) {
		Main.client().releasePacket(new PacketPlayInUpdateChannel(this.dmxAddress, value));
	}
	
	@Override
	public String getType() {
		return "channel";
	}
	
	@Override
	public String toString() {
		return "Channel " + this.dmxAddress;
	}
	
	public short getDMXAddress() {
		return this.dmxAddress;
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		return new JsonPrimitive(this.dmxAddress);
	}

}
