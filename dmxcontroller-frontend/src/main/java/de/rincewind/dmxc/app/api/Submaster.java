package de.rincewind.dmxc.app.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.rincewind.dmxc.app.Main;
import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateSubmaster;

public class Submaster implements Fadeable {
	
	private static List<Submaster> submasters;
	
	static {
		Submaster.submasters = new ArrayList<>();
	}
	
	public static void create(String name) {
		Submaster.submasters.add(new Submaster(name));
		Console.println("Submaster created: " + name);
	}
	
	public static void deleteAll() {
		Submaster.submasters.clear();
	}
	
	public static Submaster get(String name) {
		for (Submaster submaster : Submaster.submasters) {
			if (submaster.name.equals(name)) {
				return submaster;
			}
		}
		
		return null;
	}
	
	public static List<Submaster> getAll() {
		return Collections.unmodifiableList(Submaster.submasters);
	}
	
	
	private String name;
	
	private Submaster(String name) {
		this.name = name;
	}
	
	@Override
	public void update(Short value) {
		if (!Submaster.submasters.contains(this)) {
			return;
		}
		
		Main.client().releasePacket(new PacketPlayInUpdateSubmaster(this.name, value));
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	public void delete() {
		Submaster.submasters.remove(this);
		Console.println("Submaster deleted: " + this.name);
	}
	
	public String getName() {
		return this.name;
	}
	
}
