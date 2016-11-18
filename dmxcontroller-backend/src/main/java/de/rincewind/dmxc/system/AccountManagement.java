package de.rincewind.dmxc.system;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.rincewind.dmxc.common.util.JsonUtil;
import de.rincewind.dmxc.system.network.Client;

public class AccountManagement {
	
	private List<Account> accounts;
	
	public AccountManagement() {
		this.accounts = new ArrayList<>();
	}
	
	public void loadAccounts(File file) {
		JsonArray array = JsonUtil.fromJson(file, JsonArray.class);
		
		if (array == null) {
			return;
		}
		
		for (int i = 0; i < array.size(); i++) {
			JsonObject object = array.get(i).getAsJsonObject();
			this.addAccount(new Account(object.get("username").getAsString(), object.get("password").getAsString()));
		}
	}
	
	public void saveAccounts(File file) {
		JsonArray array = new JsonArray();
		
		for (Account account : this.accounts) {
			JsonObject object = new JsonObject();
			object.addProperty("username", account.getUsername());
			object.addProperty("password", account.hasPassword() ? account.getPassword() : "");
			array.add(object);
		}
		
		JsonUtil.toJson(file, array);
	}
	
	public void addAccount(Account account) {
		if (this.getAccount(account.getUsername()) != null) {
			return;
		}
		
		this.accounts.add(account);
	}
	
	public void removeAccount(Account account) {
		this.accounts.remove(account);
	}
	
	public Account getAccount(String username) {
		for (Account account : this.accounts) {
			if (account.getUsername().equals(username)) {
				return account;
			}
		}
		
		return null;
	}
	
	public Client getClient(Account account) {
		for (Client client : Main.server().getClients()) {
			if (client.isVerified() && client.getVerification() == account) {
				return client;
			}
		}
		
		return null;
	}
	
	public List<Account> getAccounts() {
		return Collections.unmodifiableList(this.accounts);
	}
	
}
