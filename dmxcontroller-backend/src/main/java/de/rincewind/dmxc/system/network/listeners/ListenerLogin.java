package de.rincewind.dmxc.system.network.listeners;

import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.packets.PacketHandler;
import de.rincewind.dmxc.common.packets.PacketListener;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInLogin;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutAccess.LoginError;
import de.rincewind.dmxc.system.Account;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.network.Client;

public class ListenerLogin implements PacketListener {

	@PacketHandler
	public void onLogin(PacketPlayInLogin packet, Client client) {
		Console.println("New incoming connection registered: ");
		Console.println("- Username: " + packet.getUsername());
		Console.println("- Password: " + ((packet.getPassword() != null && !packet.getPassword().isEmpty()) ? "YES" : "NO"));
		
		Account account = Main.management().getAccount(packet.getUsername());
		
		if (account == null) {
			Console.println("LOGIN-ERROR: Invalid username!");
			client.disallow(LoginError.INVALID_USERNAME);
			return;
		}
		
		if (account.isInUse()) {
			Console.println("LOGIN-ERROR: Username already in use!");
			client.disallow(LoginError.USERNAME);
			return;
		}
		
		if (!account.check(packet.getPassword())) {
			Console.println("LOGIN-ERROR: Invalid password!");
			client.disallow(LoginError.PASSWORD);
			return;
		}
		
		client.verify(account);
		Console.println("LOGIN-SUCCESS: Client verified!");
	}
}
