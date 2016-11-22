package de.rincewind.dmxc.app.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import de.rincewind.dmxc.app.Main;
import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateEffect;

public class Effect extends Fadeable {
	
	private static List<Effect> effects;
	
	static {
		Effect.effects = new ArrayList<>();
	}
	
	public static void create(String name) {
		Effect.effects.add(new Effect(name));
		Console.println("Effect created: " + name);
	}
	
	public static void deleteAll() {
		Effect.effects.clear();
	}
	
	public static Effect get(String name) {
		for (Effect effect : Effect.effects) {
			if (effect.name.equals(name)) {
				return effect;
			}
		}
		
		return new GhostEffect(name);
	}
	
	public static List<Effect> getAll() {
		return Collections.unmodifiableList(Effect.effects);
	}
	
	protected static Effect get(JsonElement element) {
		return Effect.get(element.getAsString());
	}
	
	
	private String name;
	
	protected Effect(String name) {
		this.name = name;
	}
	
	@Override
	public void update(Short value) {
		if (Effect.get(this.name) != null) {
			Main.client().releasePacket(new PacketPlayInUpdateEffect(this.name, value));
		}
	}
	
	@Override
	public String getType() {
		return "effect";
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	public void delete() {
		Effect.effects.remove(this);
		Console.println("Effect deleted: " + this.name);
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		return new JsonPrimitive(this.name);
	}
	
	
	public static class GhostEffect extends Effect {

		protected GhostEffect(String name) {
			super(name);
		}
		
	}
	
}
