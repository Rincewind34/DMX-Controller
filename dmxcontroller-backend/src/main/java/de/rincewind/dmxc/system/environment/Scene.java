package de.rincewind.dmxc.system.environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Scene {
	
	public static Map<Short, Short> calculateFade(Scene fadeIn, Scene fadeOut, int current) {
		if (fadeIn.fadeIn <= current) {
			return new HashMap<>();
		}
		
		Map<Short, Short> result = new HashMap<>();
		
		double percent = current / (double) fadeIn.fadeIn;
		
		for (Entry<Short, Short> entry : fadeIn.targetValues.entrySet()) {
			if (fadeOut != null && fadeOut.targetValues.containsKey(entry.getKey())) {
				result.put(entry.getKey(), Scene.calculateValue(fadeOut.targetValues.get(entry.getKey()), entry.getValue(), fadeIn.fadeIn, current));
			} else {
				if (entry.getValue() == null) {
					result.put(entry.getKey(), null);
				} else {
					result.put(entry.getKey(), (short) Math.round(entry.getValue().shortValue() * percent));
				}
			}
		}
		
		percent = 1.0 - percent;
		
		if (fadeOut != null) {
			for (Entry<Short, Short> entry : fadeOut.targetValues.entrySet()) {
				if (fadeIn.targetValues.containsKey(entry.getKey())) {
					continue;
				} else {
					if (entry.getValue() == null) {
						result.put(entry.getKey(), null);
					} else {
						result.put(entry.getKey(), (short) Math.round(entry.getValue().shortValue() * percent));
					}
				}
			}
		}
		
		return result;
	}
	
	private static short calculateValue(Short from, Short to, int length, int current) {
		short valueFrom = from == null ? 0 : from.shortValue();
		short valueTo = to == null ? 0 : to.shortValue();
		
		double percent = current / (double) length;
		
		if (valueFrom == valueTo) {
			return valueFrom;
		} else if (valueFrom > valueTo) {
			percent = 1.0 - percent;
			short diff = (short) (valueFrom - valueTo);
			
			return (short) Math.round(valueTo + (percent * diff));
		} else {
			short diff = (short) (valueTo - valueFrom);
			return (short) Math.round(diff + (percent * valueFrom));
		}
	}
	
	
	private int fadeIn;
	private int stay;

	private String name;

	private Map<Short, Short> targetValues;

	public Scene(String name) {
		this.name = name;
		this.targetValues = new HashMap<>();
		
		this.fadeIn = 500;
		this.stay = 2000;
		
		SceneRegistry.add(this);
	}
	
	public Scene(JsonObject root) {
		this(root.get("name").getAsString());
		
		JsonArray array = root.get("targetValues").getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
			JsonObject object = array.get(i).getAsJsonObject();
			short dmxAddress = object.get("dmxAddress").getAsShort();
			short targetValue = object.get("targetValue").getAsShort();
			this.targetValues.put(dmxAddress, targetValue);
		}
		
		this.fadeIn = root.get("fadeIn").getAsInt();
		this.stay = root.get("stay").getAsInt();
	}

	public void setFadeIn(int fadeIn) {
		this.fadeIn = fadeIn;
	}

	public void setStay(int stay) {
		this.stay = stay;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTargetValue(short dmxAddress, short value) {
		this.targetValues.put(dmxAddress, value);
	}

	public void removeTargetValue(short dmxAddress) {
		this.targetValues.remove(dmxAddress);
	}
	
	public boolean isFading(int current) {
		return this.fadeIn > current;
	}

	public int getFadeIn() {
		return this.fadeIn;
	}

	public int getStay() {
		return this.stay;
	}

	public String getName() {
		return this.name;
	}
	
	public JsonObject serialize() {
		JsonObject root = new JsonObject();
		JsonArray array = new JsonArray();
		
		for (Entry<Short, Short> entry : this.targetValues.entrySet()) {
			JsonObject object = new JsonObject();
			object.addProperty("dmxAddress", entry.getKey());
			object.addProperty("targetValue", entry.getValue());
			array.add(object);
		}
		
		root.add("targetValues", array);
		root.addProperty("name", this.name);
		root.addProperty("fadeIn", this.fadeIn);
		root.addProperty("stay", this.stay);
		return root;
	}
	
	public Map<Short, Short> getTargetValues() {
		return Collections.unmodifiableMap(this.targetValues);
	}
	
}
