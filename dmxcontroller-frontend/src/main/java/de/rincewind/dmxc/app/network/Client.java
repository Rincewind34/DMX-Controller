package de.rincewind.dmxc.app.network;

import de.rincewind.dmxc.app.Main;
import de.rincewind.dmxc.app.network.listeners.ListenerAccess;
import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayIn;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInLogin;

public class Client {
	
	public static Client get() {
		return Main.client();
	}
	
	private int port;
	
	private String hostname;
	private String username;
	private String password;
	
	private ClientCore nettyCore;
	private Thread thread;
	
	public Client() {
		this.setupThread();
		
		this.hostname = "localhost";
		this.port = 24578;
		this.username = "admin";
		this.password = "";
	}
	
	public void setPort(int port) {
		if (this.isConnected()) {
			throw new RuntimeException("The client is already connected!");
		}
		
		this.port = port;
	}
	
	public void setHostname(String hostname) {
		if (this.isConnected()) {
			throw new RuntimeException("The client is already connected!");
		}
		
		this.hostname = hostname;
	}
	
	public void setUsername(String username) {
		if (this.isConnected()) {
			throw new RuntimeException("The client is already connected!");
		}
		
		this.username = username;
	}
	
	public void setPassword(String password) {
		if (this.isConnected()) {
			throw new RuntimeException("The client is already connected!");
		}
		
		this.password = password;
	}
	
	public void sendPacket(PacketPlayIn packet) {
		if (!this.isConnected()) {
			throw new RuntimeException("The client is not connected!");
		}
		
		this.nettyCore.getChannel().writeAndFlush(packet);
	}
	
	public void connect() {
		if (this.isConnected()) {
			throw new RuntimeException("The client is already connected!");
		}
		
		Console.println("Connecting to server (" + this.hostname + ":" + this.port + ")");
		this.nettyCore = new ClientCore(this.hostname, this.port);
		this.addListeners();
		
		try {
			this.nettyCore.start();
			Console.println("Connected");
		} catch (InterruptedException e) {
			e.printStackTrace();
			
			this.nettyCore.stop();
			this.nettyCore.shutdown();
			this.nettyCore = null;
			Console.println("Failed to connect");
		}
		
		this.thread.start();
		Console.println("Sending login data");
		this.sendPacket(new PacketPlayInLogin(this.username, this.password));
	}
	
	public void disconnect() {
		if (!this.isConnected()) {
			throw new RuntimeException("The client is not connected!");
		}
		
		Console.println("Disconnecting from server");
		this.nettyCore.stop();
		this.thread.interrupt();
	}
	
	public boolean isConnected() {
		return this.nettyCore != null;
	}
	
	public ClientCore nettyCore() {
		return this.nettyCore;
	}
	
	private void setupThread() {
		this.thread = new Thread(() -> {
			try {
				this.nettyCore.waitCore();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				this.nettyCore.shutdown();
				this.nettyCore = null;
				Console.println("Network resources closed");
			}
		});
	}
	
	private void addListeners() {
		this.nettyCore.addListener(new ListenerAccess());
	}
	
}
