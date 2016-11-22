package de.rincewind.dmxc.app.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import de.rincewind.dmxc.app.Main;
import de.rincewind.dmxc.app.gui.ShowController;
import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateShow;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutShow.SceneState;

public class Show extends Fadeable {
	
	private static Show instance;
	
	static {
		Show.instance = new Show();
	}
	
	public static void sendActivated(boolean value) {
		Main.client().releasePacket(new PacketPlayInUpdateShow(value));
	}
	
	public static void sendScene(int index) {
		Main.client().releasePacket(new PacketPlayInUpdateShow(index));
	}
	
	public static void sendFaderValue(Short value) {
		Main.client().releasePacket(new PacketPlayInUpdateShow(value));
	}
	
	public static Show instance() {
		return Show.instance;
	}
	
	private boolean activated;
	private SceneState state;
	
	private int currentScene;
	
	private List<String> scenes;
	private List<ShowController> controllers;
	
	private Show() {
		this.scenes = new ArrayList<>();
		this.controllers = new ArrayList<>();
		this.reset();
	}
	
	@Override
	public void update(Short value) {
		Show.sendFaderValue(value);
	}
	
	@Override
	public String getType() {
		return "show";
	}
	
	@Override
	public String toString() {
		return "Show";
	}
	
	public void reset() {
		this.scenes.clear();
		this.controllers.clear();
		this.activated = false;
		this.state = null;
		this.currentScene = -1;
	}
	
	public void setScene(int index, SceneState state) {
		if (this.currentScene != index) {
			this.currentScene = index;
			
			Console.println("Show: Current index changed to " + index);
			
			this.iterate((controller) -> {
				controller.setCurrentScene(this.currentScene);
			});
		}
		
		if (this.state != state) {
			this.state = state;
			
			Console.println("Show: Scene state chaned to " + state.name());
			
			this.iterate((controller) -> {
				controller.setSceneState(this.state);
			});
		}
	}
	
	public void setActivated(boolean value, int currentScene) {
		if (this.activated != value) {
			Console.println("Show: Activation changed: " + value + (value ? " (index: " + currentScene + ")" : ""));
			
			this.activated = value;
			this.currentScene = currentScene;
			
			if (this.activated) {
				this.state = SceneState.FADING;
			}
			
			this.iterate((controller) -> {
				controller.setActivated(this.activated);
				
				if (this.activated) {
					controller.setCurrentScene(this.currentScene);
					controller.setSceneState(this.state);
				}
			});
		}
	}
	
	public void addScene(String name) {
		this.scenes.add(name);
		
		Console.println("Show: New scene " + name);
		
		this.iterate((controller) -> {
			controller.addScene(name);
		});
	}
	
	public void addScene(String name, int index) {
		this.scenes.add(index, name);
		
		Console.println("Show: New scene " + name + " (index: " + index + ")");
		
		this.iterate((controller) -> {
			controller.addScene(name, index);
		});
	}
	
	public void removeScene(int index) {
		this.scenes.remove(index);
		
		Console.println("Show: Removed scene " + index);
		
		this.iterate((controller) -> {
			controller.removeScene(index);
		});
	}
	
	public void registerController(ShowController controller) {
		this.controllers.add(controller);
		
		for (String scene : this.scenes) {
			controller.addScene(scene);
		}
		
		controller.setActivated(this.activated);
		
		if (this.activated) {
			controller.setCurrentScene(this.currentScene);
			controller.setSceneState(this.state);
		}
	}
	
	public void unregisterController(ShowController controller) {
		this.controllers.remove(controller);
	}
	
	public boolean isActivated() {
		return this.activated;
	}
	
	public int getCurrentScene() {
		return this.currentScene;
	}
	
	public SceneState getState() {
		return this.state;
	}
	
	public List<String> getScenes() {
		return Collections.unmodifiableList(this.scenes);
	}
	
	@Override
	protected JsonElement serializeSimplified() {
		return JsonNull.INSTANCE;
	}
	
	private void iterate(Consumer<ShowController> action) {
		for (ShowController controller : new ArrayList<>(this.controllers)) {
			action.accept(controller);
		}
	}
	
}
