package de.rincewind.dmxc.system.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.network.listeners.ListenerLogin;
import de.rincewind.dmxc.system.network.listeners.ListenerUpdateChannel;
import de.rincewind.dmxc.system.network.listeners.ListenerUpdateEffect;
import de.rincewind.dmxc.system.network.listeners.ListenerUpdateMaster;
import de.rincewind.dmxc.system.network.listeners.ListenerUpdateShow;
import de.rincewind.dmxc.system.network.listeners.ListenerUpdateSubmaster;
import io.netty.channel.Channel;

public class Server {
	
	public static Server get() {
		return Main.server();
	}
	
	private int port;
	
	private ServerCore nettyCore;
	private Thread thread;
	
	private List<Client> clients;
	
	public Server() {
		this.setupThread();
		
		this.port = 24578;
		this.clients = new ArrayList<>();
	}
	
	public void setPort(int port) {
		if (this.isConnected()) {
			throw new RuntimeException("The client is already connected!");
		}
		
		this.port = port;
	}
	
	public void connect() {
		if (this.isConnected()) {
			throw new RuntimeException("The client is already connected!");
		}
		
		Console.println("Setting up server (Port: " + this.port + ")");
		this.nettyCore = new ServerCore(this.port);
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
	}
	
	public void disconnect() {
		if (!this.isConnected()) {
			throw new RuntimeException("The client is not connected!");
		}
		
		Console.println("Terminating client connections (currently " + this.clients.size() + ")");
		
		while (this.clients.size() != 0) {
			this.clients.get(0).disconnect();
		}
		
		Console.println("Disconnecting server");
		this.nettyCore.stop();
		this.thread.interrupt();
	}
	
	public void newClient(Channel channel) {
		if (this.getClient(channel) != null) {
			throw new RuntimeException("The channel is already registered!");
		}
		
		this.clients.add(new Client(channel));
	}
	
	public synchronized void diconnectClient(Client client) {
		if (!this.clients.contains(client)) {
			throw new RuntimeException("Unknown client!");
		}
		
		client.getChannel().close();
	}
	
	public void cleanUpClient(Client client) {
		Console.println("Disconnecting client " + (client.isVerified() ? client.getVerification().getUsername() : "unverified"));
		Main.environment().cleanup(client);
		client.interruptElapseThread();
		this.clients.remove(client);
	}
	
	public boolean isConnected() {
		return this.nettyCore != null;
	}
	
	public Client getFirst() {
		return this.clients.get(0);
	}
	
	public Client getClient(Channel channel) {
		for (Client client : this.clients) {
			if (client.getChannel().id().equals(channel.id())) {
				return client;
			}
		}
		
		return null;
	}
	
	public ServerCore nettyCore() {
		return this.nettyCore;
	}
	
	public List<Client> getClients() {
		return Collections.unmodifiableList(this.clients);
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
		this.nettyCore.addListener(new ListenerLogin());
		this.nettyCore.addListener(new ListenerUpdateMaster());
		this.nettyCore.addListener(new ListenerUpdateChannel());
		this.nettyCore.addListener(new ListenerUpdateSubmaster());
		this.nettyCore.addListener(new ListenerUpdateEffect());
		this.nettyCore.addListener(new ListenerUpdateShow());
	}
	
}
