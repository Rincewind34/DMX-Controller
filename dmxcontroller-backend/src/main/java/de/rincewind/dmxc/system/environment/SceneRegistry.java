package de.rincewind.dmxc.system.environment;

import java.util.ArrayList;
import java.util.List;

public class SceneRegistry {
	
	public static List<Scene> scenes = new ArrayList<>();
	
	static {
		Scene black = new Scene("black");
		
		for (short i = 1; i <= 512; i++) {
			black.setTargetValue(i, (short) 0);
		}
		
		SceneRegistry.scenes.add(black);
	}
	
	public static void add(Scene scene) {
		SceneRegistry.scenes.add(scene);
	}
	
	public static Scene getScene(String name) {
		for (Scene scene : SceneRegistry.scenes) {
			if (scene.getName().equals(name)) {
				return scene;
			}
		}
		
		return null;
	}
	
}
