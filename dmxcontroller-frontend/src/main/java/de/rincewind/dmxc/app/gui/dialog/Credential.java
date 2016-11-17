package de.rincewind.dmxc.app.gui.dialog;

public class Credential {

	private int port;
	
	private String hostname;
	private String username;
	private String password;
	
	public Credential() {
		
	}
	
	public Credential(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	public Credential(String hostname, int port, String username) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
	}
	
	public Credential(String hostname, int port, String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public String getHostname() {
		return this.hostname;
	}

	public String getPassword() {
		return this.password;
	}

	public String getUsername() {
		return this.username;
	}

}
