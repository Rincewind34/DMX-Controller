package de.rincewind.dmxc.app.network;

import de.rincewind.dmxc.app.Main;
import de.rincewind.dmxc.app.gui.dialog.Credential;
import de.rincewind.dmxc.app.network.listeners.ListenerAccess;
import de.rincewind.dmxc.app.network.listeners.ListenerEffect;
import de.rincewind.dmxc.app.network.listeners.ListenerShow;
import de.rincewind.dmxc.app.network.listeners.ListenerSubmaster;
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

	public void releasePacket(PacketPlayIn packet) {
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
		} catch (Throwable e) {
			e.printStackTrace();

			if (this.nettyCore.isOnline()) {
				this.nettyCore.stop();
				this.nettyCore.shutdown();
			}

			this.nettyCore = null;
			Console.println("Failed to connect");
			return;
		}

		this.setupThread();
		this.thread.start();
		Console.println("Sending login data");
		this.releasePacket(new PacketPlayInLogin(this.username, this.password));
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

	public int getPort() {
		return this.port;
	}

	public String getHostname() {
		return this.hostname;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public Credential asCredential() {
		return new Credential(this.hostname, this.port, this.username, this.password);
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
		this.nettyCore.addListener(new ListenerSubmaster());
		this.nettyCore.addListener(new ListenerEffect());
		this.nettyCore.addListener(new ListenerShow());
	}

}
