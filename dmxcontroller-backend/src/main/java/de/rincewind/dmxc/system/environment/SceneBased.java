package de.rincewind.dmxc.system.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class SceneBased extends DMXData {
	
	private int currentIndex;
	
	private List<Scene> scenes;
	private List<Short> dimmers;
	
	protected SceneBased() {
		this.currentIndex = -1;
		this.scenes = new ArrayList<>();
		this.dimmers = new ArrayList<>();
	}
	
	protected SceneBased(JsonObject root) {
		this();
		
		JsonArray arrayScenes = root.get("scenes").getAsJsonArray();
		JsonArray arrayDimmers = root.get("dimmers").getAsJsonArray();
		
		for (int i = 0; i < arrayScenes.size(); i++) {
			this.scenes.add(new Scene(arrayScenes.get(i).getAsJsonObject()));
		}
		
		for (int i = 0; i < arrayDimmers.size(); i++) {
			this.dimmers.add(arrayDimmers.get(i).getAsShort());
		}
	}
	
	public abstract boolean isRunning();
	
	@Override
	public String toString() {
		return "{running: " + this.isRunning() + "; scenes: " + this.scenes.size() + "; dimmers: " + this.dimmers.size() + "}";
	}
	
	public void addScene(Scene scene) {
		if (this.isRunning()) {
			throw new RuntimeException("This component is currently running!");
		}
		
		this.scenes.add(scene);
	}
	
	public void addScene(int index, Scene scene) {
		if (this.isRunning()) {
			throw new RuntimeException("This component is currently running!");
		}
		
		this.scenes.add(index, scene);
	}
	
	public void removeScene(int index) {
		if (this.isRunning()) {
			throw new RuntimeException("This component is currently running!");
		}
		
		this.scenes.remove(index);
		
	}
	
	public void removeScene(Scene scene) {
		this.removeScene(this.scenes.indexOf(scene));
	}
	
	public void addDimmer(short dmxAddress) {
		if (this.isRunning()) {
			throw new RuntimeException("This component is currently running!");
		}
		
		this.dimmers.add(dmxAddress);
	}
	
	public void removeDimmer(short dmxAddress) {
		if (this.isRunning()) {
			throw new RuntimeException("This component is currently running!");
		}
		
		this.dimmers.remove(dmxAddress);
	}
	
	public int getCurrentIndex() {
		return this.currentIndex;
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
		return root;
	}
	
	public List<Scene> getScenes() {
		return Collections.unmodifiableList(this.scenes);
	}
	
	public Map<Short, Short> getCurrentValues() {
		if (!this.isRunning()) {
			return new HashMap<>();
		}
		
		Scene current = this.currentIndex >= this.scenes.size() ? null : this.scenes.get(this.currentIndex);
		int adjusted = this.adjustTime();
		
		Map<Short, Short> values = new HashMap<>();
		
		if (current == null) {
			
		} else if (current.isFading(adjusted)) {
			values.putAll(Scene.calculateFade(current, this.getPreviousScene(), adjusted));
		} else {
			values.putAll(current.getTargetValues());
		}
		
		Map<Short, Short> result = DMXData.multiply(this.getCurrentPercent(), values);
		
		for (short dimmer : this.dimmers) {
			result.put(dimmer, (short) 255);
		}
		
		return Collections.unmodifiableMap(result);
	}
	
	protected abstract int adjustTime();
	
	protected abstract Scene getPreviousScene();
	
	protected void setCurrentIndex(int index) {
		this.currentIndex = index;
	}
	
}
