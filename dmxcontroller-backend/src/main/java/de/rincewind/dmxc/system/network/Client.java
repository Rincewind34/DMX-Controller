package de.rincewind.dmxc.system.network;

import de.rincewind.dmxc.common.packets.Action;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOut;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutAccess;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutAccess.LoginError;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutEffect;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutShow;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutSubmaster;
import de.rincewind.dmxc.system.Account;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.environment.Effect;
import de.rincewind.dmxc.system.environment.Scene;
import de.rincewind.dmxc.system.environment.Submaster;
import io.netty.channel.Channel;

public class Client {
	
	private Thread elapseThread;
	
	private Account verification;
	private Channel channel;

	public Client(Channel channel) {
		this.channel = channel;
		
		this.elapseThread = new Thread(() -> {
			try {
				Thread.sleep(1000L * 10);
			} catch (InterruptedException e) {
				// No printing
				return;
			}
			
			if (!Thread.interrupted() && this.isConnected() && !this.isVerified()) {
				this.disallow(LoginError.AUTH_TIME_ELAPSED);
			}
		});
		
		this.elapseThread.start();
	}

	public void verify(Account account) {
		if (this.isVerified()) {
			throw new RuntimeException("The client is already verfied!");
		}
		
		if (account.isInUse()) {
			throw new RuntimeException("The account is already in use!");
		}

		this.verification = account;
		this.interruptElapseThread();
		this.sendPacket(new PacketPlayOutAccess());
		
		for (Submaster submaster : Main.environment().getSubmasters()) {
			this.sendPacket(new PacketPlayOutSubmaster(submaster.getName(), Action.ADD));
		}
		
		for (Effect effect : Main.environment().getEffects()) {
			this.sendPacket(new PacketPlayOutEffect(effect.getName(), Action.ADD));
		}
		
		for (Scene scene : Main.environment().getShow().getScenes()) {
			this.sendPacket(new PacketPlayOutShow(scene.getName()));
		}
	}
	
	public void disallow(LoginError error) {
		if (this.isVerified()) {
			throw new RuntimeException("The client is already verfied!");
		}
		
		this.sendPacket(new PacketPlayOutAccess(error), false);
		this.disconnect();
	}

	public void sendPacket(PacketPlayOut packet) {
		this.sendPacket(packet, true);
	}

	public void disconnect() {
		Main.server().diconnectClient(this);
	}

	public boolean isVerified() {
		return this.verification != null;
	}
	
	public boolean isConnected() {
		return Main.server().getClient(this.channel) != null;
	}
	
	public boolean isBefore(Client target) {
		return Main.server().getClients().indexOf(this) > Main.server().getClients().indexOf(target);
	}
	
	public Account getVerification() {
		return this.verification;
	}

	public Channel getChannel() {
		return this.channel;
	}
	
	protected void interruptElapseThread() {
		this.elapseThread.interrupt();
	}
	
	private void sendPacket(PacketPlayOut packet, boolean check) {
		if (check && !this.isVerified()) {
			throw new RuntimeException("The client is not verified!");
		}

		this.channel.writeAndFlush(packet);
	}

}
