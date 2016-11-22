package de.rincewind.dmxc.common.packets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import de.rincewind.dmxc.common.packets.incoming.PacketPlayInLogin;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateChannel;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateEffect;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateMaster;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateShow;
import de.rincewind.dmxc.common.packets.incoming.PacketPlayInUpdateSubmaster;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutAccess;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutEffect;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutShow;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutSubmaster;

public class PacketRegistry {
	
	private static Map<Integer, Class<? extends Packet>> packets;
	
	static {
		PacketRegistry.packets = new HashMap<>();
		PacketRegistry.packets.put(1, PacketPlayOutAccess.class);
		PacketRegistry.packets.put(2, PacketPlayOutSubmaster.class);
		PacketRegistry.packets.put(3, PacketPlayOutEffect.class);
		PacketRegistry.packets.put(4, PacketPlayOutShow.class);
		PacketRegistry.packets.put(5, PacketPlayInLogin.class);
		PacketRegistry.packets.put(6, PacketPlayInUpdateMaster.class);
		PacketRegistry.packets.put(7, PacketPlayInUpdateChannel.class);
		PacketRegistry.packets.put(8, PacketPlayInUpdateSubmaster.class);
		PacketRegistry.packets.put(9, PacketPlayInUpdateEffect.class);
		PacketRegistry.packets.put(10, PacketPlayInUpdateShow.class);
	}
	
	public static <T extends Packet> T newPacket(Class<T> packetClass) {
		try {
			Constructor<T> constructor = packetClass.getDeclaredConstructor(new Class<?>[0]);
			constructor.setAccessible(true);
			return constructor.newInstance(new Object[0]);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
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
