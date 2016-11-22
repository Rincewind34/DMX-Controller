package de.rincewind.dmxc.system.environment;

import java.util.Map;

import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutShow;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutShow.SceneState;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.network.Client;

public class Show extends SceneBased {
	
	private boolean pushedState;
	
	private boolean running;
	private long time;
	
	private Scene previous;
	
	public Show() {
		super.setCurrentIndex(0);
		this.previous = null;
	}
	
	@Override
	public boolean isRunning() {
		return this.running;
	}
	
	@Override
	public void update(Client client, Short value) {
		if (this.isRunning()) {
			super.update(client, value);
		}
	}
	
	@Override
	public void addScene(Scene scene) {
		super.addScene(scene);
		
		for (Client client : Main.server().getClients()) {
			client.sendPacket(new PacketPlayOutShow(scene.getName()));
		}
	}
	
	@Override
	public void addScene(int index, Scene scene) {
		super.addScene(index, scene);
		
		for (Client client : Main.server().getClients()) {
			client.sendPacket(new PacketPlayOutShow(scene.getName(), index));
		}
	}
	
	@Override
	public void removeScene(int index) {
		super.removeScene(index);
		
		for (Client client : Main.server().getClients()) {
			client.sendPacket(new PacketPlayOutShow(index));
		}
	}
	
	@Override
	public void setCurrentIndex(int index) {
		if (!this.running) {
			throw new RuntimeException("The show is not running!");
		}
		
		if (index >= this.getScenes().size() || index < 0) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		this.pushedState = false;
		this.previous = this.getScene(this.getCurrentIndex());
		super.setCurrentIndex(index);
		this.setTime();
		
		for (Client client : Main.server().getClients()) {
			client.sendPacket(new PacketPlayOutShow(index, SceneState.FADING));
		}
	}
	
	@Override
	public Map<Short, Short> getCurrentValues() {
		if (!this.getScene(this.getCurrentIndex()).isFading(this.adjustTime()) && !this.pushedState) {
			this.pushedState = true;
			
			for (Client client : Main.server().getClients()) {
				client.sendPacket(new PacketPlayOutShow(this.getCurrentIndex(), SceneState.STAY));
			}
		}
		
		return super.getCurrentValues();
	}
	
	public void setRunning(boolean value) {
		if (this.running == value) {
			return;
		}
		
		this.running = value;
		
		if (this.running) {
			this.setTime();
		} else {
			this.pushedState = false;
		}
		
		for (Client client : Main.server().getClients()) {
			client.sendPacket(new PacketPlayOutShow(this.running, this.running ? this.getCurrentIndex() : -1));
		}
	}
	
	public void nextScene() {
		this.setCurrentIndex(this.getCurrentIndex() + 1);
	}
	
	public void previousScene() {
		this.setCurrentIndex(this.getCurrentIndex() - 1);
	}
	
	@Override
	protected int adjustTime() {
		return (int) (System.currentTimeMillis() - this.time);
	}

	@Override
	protected Scene getPreviousScene() {
		return this.previous;
	}
	
	private void setTime() {
		this.time = System.currentTimeMillis();
	}

}
