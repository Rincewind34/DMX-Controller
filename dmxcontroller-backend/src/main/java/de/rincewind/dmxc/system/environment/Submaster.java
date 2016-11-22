package de.rincewind.dmxc.system.environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.rincewind.dmxc.system.Main;

public class Submaster extends DMXData {
	
	private String name;
	
	private Map<Short, Short> targetValues;
	
	protected Submaster(String name) {
		this.name = name;
		this.targetValues = new HashMap<>();
	}
	
	protected Submaster(JsonObject root) {
		this(root.get("name").getAsString());
		
		JsonArray array = root.get("targets").getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
			JsonObject object = array.get(i).getAsJsonObject();
			short dmxAddress = object.get("dmxAddress").getAsShort();
			short targetValue = object.get("targetValue").getAsShort();
			this.targetValues.put(dmxAddress, targetValue);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (short dmxAddress : this.targetValues.keySet()) {
			builder.append(dmxAddress);
			builder.append(": ");
			builder.append(this.targetValues.get(dmxAddress));
			builder.append(" | ");
		}
		
		if (this.targetValues.size() > 0) {
			builder.setLength(builder.length() - 3);
		} else {
			builder.append("nothing");
		}
		
		return builder.toString();
	}
	
	public void delete() {
		Main.environment().deleteSubmaster(this);
	}
	
	public void setTargetValue(short dmxAddress, short value) {
		this.targetValues.put(dmxAddress, value);
	}
	
	public void removeTargetValue(short dmxAddress) {
		this.targetValues.remove(dmxAddress);
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
		
		root.addProperty("name", this.name);
		root.add("targets", array);
		return root;
	}
	
	public Map<Short, Short> getTargetValues() {
		return Collections.unmodifiableMap(this.targetValues);
	}
	
	public Map<Short, Short> getCurrentValues() {
		if (this.getCurrentValue() == null) {
			return Collections.unmodifiableMap(new HashMap<>());
		} else {
			return Collections.unmodifiableMap(DMXData.multiply(this.getCurrentPercent(), this.getTargetValues()));
		}
	}
	
}
