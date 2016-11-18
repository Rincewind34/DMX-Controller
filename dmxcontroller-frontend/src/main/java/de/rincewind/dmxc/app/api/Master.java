package de.rincewind.dmxc.app.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import de.rincewind.dmxc.app.Main;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateMaster;

public class Master extends Fadeable {
	
	private static Master instance;
	
	static {
		Master.instance = new Master();
	}
	
	public static Master instance() {
		return Master.instance;
	}
	
	private Master() {
		
	}
	
	@Override
	public void update(Short value) {
		Main.client().releasePacket(new PacketPlayInUpdateMaster(value));
	}
	
	@Override
	public String getType() {
		return "master";
	}
	
	@Override
	public String toString() {
		return "Master";
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		return JsonNull.INSTANCE;
	}
	
}
