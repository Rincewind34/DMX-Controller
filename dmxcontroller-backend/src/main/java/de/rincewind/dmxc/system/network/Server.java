package de.rincewind.dmxc.system.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayIn;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInLogin;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.inbound.ListenerLogin;
import io.netty.channel.Channel;

public class Server {
	
	public static Server get() {
		return Main.server();
	}
	
	private int port;
	
	private String username;
	private String password;
	
	private ServerCore nettyCore;
	private Thread thread;
	
	private List<Client> clients;
	
	public Server() {
		this.setupThread();
		
		this.port = 24578;
		this.username = "admin";
		this.password = "";
		this.clients = new ArrayList<>();
	}
	
	public void setPort(int port) {
		if (this.isConnected()) {
			throw new RuntimeException("The client is already connected!");
		}
		
		this.port = port;
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
		Console.println("Sending login data");
		this.sendPacket(new PacketPlayInLogin(this.username, this.password));
	}
	
	public void disconnect() {
		if (!this.isConnected()) {
			throw new RuntimeException("The client is not connected!");
		}
		
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
	
	public void diconnectClient(Client client) {
		client.getChannel().close();
		this.clients.remove(client);
	}
	
	public boolean isConnected() {
		return this.nettyCore != null;
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
	}
	
}
