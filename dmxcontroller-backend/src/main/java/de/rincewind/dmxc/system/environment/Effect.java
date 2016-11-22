package de.rincewind.dmxc.system.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.network.Client;

public class Effect extends DMXData {
	
	private boolean first;
	private int currentIndex;
	private long startTimestamp;
	
	private String name;
	
	private List<Scene> scenes;
	private List<Short> dimmers;
	
	protected Effect(String name) {
		this.name = name;
		this.currentIndex = -1;
		this.scenes = new ArrayList<>();
		this.dimmers = new ArrayList<>();
	}
	
	protected Effect(JsonObject root) {
		this(root.get("name").getAsString());
		
		JsonArray arrayScenes = root.get("scenes").getAsJsonArray();
		JsonArray arrayDimmers = root.get("dimmers").getAsJsonArray();
		
		for (int i = 0; i < arrayScenes.size(); i++) {
			this.scenes.add(new Scene(arrayScenes.get(i).getAsJsonObject()));
		}
		
		for (int i = 0; i < arrayDimmers.size(); i++) {
			this.dimmers.add(arrayDimmers.get(i).getAsShort());
		}
	}
	
	@Override
	public void update(Client client, Short value) {
		super.update(client, value);
		
		Short current = this.getCurrentValue();
		
		if (current == null && this.isRunning()) {
			this.currentIndex = -1;
		} else if (current != null && !this.isRunning()) {
			this.currentIndex = 0;
			this.startTimestamp = System.currentTimeMillis();
			this.first = true;
		}
	}
	
	@Override
	public String toString() {
		return "{running: " + this.isRunning() + "; length: " + this.getLength() + "; scenes: " + this.scenes.size() + "; dimmers: " + this.dimmers.size() + "}";
	}
	
	public void delete() {
		Main.environment().deleteEffect(this);
	}
	
	public void addScene(Scene scene) {
		if (this.isRunning()) {
			throw new RuntimeException("This effect is currently running!");
		}
		
		this.scenes.add(scene);
	}
	
	public void addScene(int index, Scene scene) {
		if (this.isRunning()) {
			throw new RuntimeException("This effect is currently running!");
		}
		
		this.scenes.add(index, scene);
	}
	
	public void removeScene(int index) {
		if (this.isRunning()) {
			throw new RuntimeException("This effect is currently running!");
		}
		
		this.scenes.remove(index);
		
	}
	
	public void removeScene(Scene scene) {
		if (this.isRunning()) {
			throw new RuntimeException("This effect is currently running!");
		}
		
		this.scenes.remove(scene);
	}
	
	public void addDimmer(short dmxAddress) {
		if (this.isRunning()) {
			throw new RuntimeException("This effect is currently running!");
		}
		
		this.dimmers.add(dmxAddress);
	}
	
	public void removeDimmer(short dmxAddress) {
		if (this.isRunning()) {
			throw new RuntimeException("This effect is currently running!");
		}
		
		this.dimmers.remove(dmxAddress);
	}
	
	public boolean isRunning() {
		return this.currentIndex != -1;
	}
	
	public int getLength() {
		int length = 0;
		
		for (Scene scene : this.scenes) {
			length = length + scene.getFadeIn() + scene.getStay();
		}
		
		return length;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Scene getScene(int index) {
		return this.scenes.get(index);
	}
	
	public JsonObject serialize() {
		JsonObject root = new JsonObject();
		JsonArray arrayScenes = new JsonArray();
		JsonArray arrayDimmers = new JsonArray();
		
		for (Scene scene : this.scenes) {
			arrayScenes.add(scene.serialize());
		}
		
		for (short dmxAddress : this.dimmers) {
			arrayDimmers.add(new JsonPrimitive(dmxAddress));
		}
		
		root.add("dimmers", arrayDimmers);
		root.add("scenes", arrayScenes);
		root.addProperty("name", this.name);
		return root;
	}
	
	public List<Scene> getScenes() {
		return Collections.unmodifiableList(this.scenes);
	}
	
	public Map<Short, Short> getCurrentValues() {
		if (!this.isRunning()) {
			return new HashMap<>();
		}
		
		this.updateCurrentIndex();
		Scene current = this.scenes.get(this.currentIndex);
		int adjusted = (int) this.adjustTime();
		
		Map<Short, Short> values = new HashMap<>();
		
		if (current.isFading(adjusted)) {
			values.putAll(Scene.calculateFade(current, (this.currentIndex != 0 || !this.first) ? this.scenes.get(this.prevIndex()) : null, adjusted));
		} else {
			values.putAll(current.getTargetValues());
		}
		
		Map<Short, Short> result = DMXData.multiply(this.getCurrentPercent(), values);
		
		for (short dimmer : this.dimmers) {
			result.put(dimmer, (short) 255);
		}
		
		return Collections.unmodifiableMap(result);
	}
	
	private void updateCurrentIndex() {
		long diff = this.getDifferenceTime();
		
		for (int index = 0; index < this.scenes.size(); index++) {
			Scene scene = this.scenes.get(index);
			
			if (diff > scene.getFadeIn() + scene.getStay()) {
				diff = diff - (scene.getFadeIn() + scene.getStay());
			} else {
				this.currentIndex = index;
				break;
			}
		}
	}
	
	private int prevIndex() {
		return this.adjustIndex(this.currentIndex - 1);
	}
	
	private int adjustIndex(int index) {
		if (index == this.scenes.size()) {
			return 0;
		} else if (index == -1) {
			return this.scenes.size() - 1;
		} else {
			return index;
		}
	}
	
	private long adjustTime() {
		long diff = this.getDifferenceTime();
		
		for (int index = 0; index < this.currentIndex; index++) {
			Scene scene = this.scenes.get(index);
			diff = diff - (scene.getFadeIn() + scene.getStay());
		}
		
		return diff;
	}
	
	private long getDifferenceTime() {
		long diff = System.currentTimeMillis() - this.startTimestamp;
		
		while (diff >= this.getLength()) {
			diff = diff - this.getLength();
			this.first = false;
		}
		
		return diff;
	}

}
