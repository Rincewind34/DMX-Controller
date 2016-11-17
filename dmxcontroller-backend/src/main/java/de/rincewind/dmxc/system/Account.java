package de.rincewind.dmxc.system;

public class Account {

	public String username;
	public String password;

	public Account(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public boolean isInUse() {
		return Main.management().getClient(this) != null;
	}
	
	public boolean hasPassword() {
		return this.password != null && !this.password.isEmpty();
	}
	
	public boolean check(String password) {
		if (!this.hasPassword()) {
			return true;
		}
		
		if (password == null) {
			return false;
		}
		
		return this.password.equals(password);
	}
	
	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

}
