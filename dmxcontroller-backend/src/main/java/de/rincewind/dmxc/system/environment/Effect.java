package de.rincewind.dmxc.system.environment;

import java.util.Map;

import com.google.gson.JsonObject;

import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.network.Client;

public class Effect extends SceneBased {
	
	private boolean first;
	private long startTimestamp;
	
	private String name;
	
	protected Effect(String name) {
		this.name = name;
	}
	
	protected Effect(JsonObject root) {
		super(root);
		
		this.name = root.get("name").getAsString();
	}
	
	@Override
	public boolean isRunning() {
		return this.getCurrentIndex() != -1;
	}
	
	@Override
	public void update(Client client, Short value) {
		super.update(client, value);
		
		Short current = this.getCurrentValue();
		
		if (current == null && this.isRunning()) {
			this.setCurrentIndex(-1);
		} else if (current != null && !this.isRunning()) {
			this.setCurrentIndex(0);
			this.startTimestamp = System.currentTimeMillis();
			this.first = true;
		}
	}
	
	@Override
	public JsonObject serialize() {
		JsonObject root = super.serialize();
		root.addProperty("name", this.name);
		return root;
	}
	
	@Override
	public Map<Short, Short> getCurrentValues() {
		this.updateCurrentIndex();
		
		return super.getCurrentValues();
	}
	
	public void delete() {
		Main.environment().deleteEffect(this);
	}
	
	public int getLength() {
		int length = 0;
		
		for (Scene scene : this.getScenes()) {
			length = length + scene.getFadeIn() + scene.getStay();
		}
		
		return length;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	protected int adjustTime() {
		long diff = this.getDifferenceTime();
		
		for (int index = 0; index < this.getCurrentIndex(); index++) {
			Scene scene = this.getScene(index);
			diff = diff - (scene.getFadeIn() + scene.getStay());
		}
		
		return (int) diff;
	}
	
	@Override
	protected Scene getPreviousScene() {
		if (this.first && this.getCurrentIndex() == 0) {
			return null;
		} else {
			return this.getScene(this.adjustIndex(this.getCurrentIndex() - 1));
		}
	}
	
	private void updateCurrentIndex() {
		if (!this.isRunning()) {
			this.setCurrentIndex(-1);
			return;
		}
		
		long diff = this.getDifferenceTime();
		
		for (int index = 0; index < this.getScenes().size(); index++) {
			Scene scene = this.getScene(index);
			
			if (diff > scene.getFadeIn() + scene.getStay()) {
				diff = diff - (scene.getFadeIn() + scene.getStay());
			} else {
				this.setCurrentIndex(index);
				break;
			}
		}
	}
	
	private int adjustIndex(int index) {
		if (index == this.getScenes().size()) {
			return 0;
		} else if (index == -1) {
			return this.getScenes().size() - 1;
		} else {
			return index;
		}
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
