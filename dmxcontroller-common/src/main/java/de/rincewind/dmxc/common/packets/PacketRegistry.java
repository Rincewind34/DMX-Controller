package de.rincewind.dmxc.common.packets;

import java.util.HashMap;
import java.util.Map;

import de.rincewind.dmxc.common.packets.incoming.PacketPlayInLogin;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateChannel;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateEffect;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateMaster;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateSubmaster;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutAccess;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutEffect;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutSubmaster;

public class PacketRegistry {
	
	private static Map<Integer, Class<? extends Packet>> packets;
	
	static {
		PacketRegistry.packets = new HashMap<>();
		PacketRegistry.packets.put(1, PacketPlayOutAccess.class);
		PacketRegistry.packets.put(2, PacketPlayOutSubmaster.class);
		PacketRegistry.packets.put(3, PacketPlayOutEffect.class);
		PacketRegistry.packets.put(4, PacketPlayInLogin.class);
		PacketRegistry.packets.put(5, PacketPlayInUpdateMaster.class);
		PacketRegistry.packets.put(6, PacketPlayInUpdateChannel.class);
		PacketRegistry.packets.put(7, PacketPlayInUpdateSubmaster.class);
		PacketRegistry.packets.put(8, PacketPlayInUpdateEffect.class);
	}
	
	public static <T extends Packet> T newPacket(Class<T> packetClass) {
		try {
			return packetClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new PacketException("Could not create packet!");
		}
	}
	
	public static Packet newPacket(int id) {
		return PacketRegistry.newPacket(PacketRegistry.packets.get(id));
	}
	
	public static int getId(Packet packet) {
		return PacketRegistry.getId(packet.getClass());
	}
	
	public static int getId(Class<? extends Packet> packetClass) {
		for (int id : PacketRegistry.packets.keySet()) {
			if (PacketRegistry.packets.get(id).getName().equals(packetClass.getName())) {
				return id;
			}
		}
		
		throw new PacketException("The packet is not registered!");
	}
	
}
