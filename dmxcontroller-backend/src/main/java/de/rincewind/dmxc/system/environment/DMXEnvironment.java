package de.rincewind.dmxc.system.environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.packets.Action;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutEffect;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutSubmaster;
import de.rincewind.dmxc.common.util.JsonUtil;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.UpdateTask;
import de.rincewind.dmxc.system.network.Client;

public class DMXEnvironment {

	private MergingMethod method;

	private List<Submaster> submasters;
	private List<Effect> effects;

	private Map<Short, DMXData> addresses;
	
	private Show show;
	
	private DMXData master;
	private DMXInterface dmxInterface;
	
	private Thread updateThread;
	
	public DMXEnvironment(MergingMethod method) {
		this.method = method;
		this.show = new Show();
		this.master = new DMXData();
		this.addresses = new HashMap<>();
		this.submasters = new ArrayList<>();
		this.effects = new ArrayList<>();
		
		for (short i = 1; i <= 512; i++) {
			this.addresses.put(i, new DMXData());
		}
		
		this.updateThread = new Thread(new UpdateTask());
	}
	
	public void create() {
		Console.println("Opening interface");
		this.dmxInterface = new DMXInterface();
		Console.println("Result: " + this.dmxInterface.openInterface());
		
		Console.println("Starting update thread");
		this.updateThread.start();
	}
	
	@SuppressWarnings("deprecation")
	public void destroy() {
		Console.println("Stoping update thread");
		this.updateThread.stop();
		
		Console.println("Closing interface");
		this.dmxInterface.closeInterface();
	}
	
	public void setMergingMethod(MergingMethod method) {
		this.method = method;
	}
	
	public void update(Client client, short dmxAddress, Short dmxValue) {
		if (dmxAddress < 1 || dmxAddress > 512) {
			return;
		}
		
		this.addresses.get(dmxAddress).update(client, dmxValue);
	}
	
	public void updateMaster(Client client, Short value) {
		this.master.update(client, value);
	}
	
	public void cleanup(Client client) {
		for (DMXData data : this.addresses.values()) {
			data.removeClient(client);
		}
		
		for (Submaster submaster : this.submasters) {
			submaster.removeClient(client);
		}
		
		for (Effect effect : this.effects) {
			effect.removeClient(client);
		}
		
		this.master.removeClient(client);
	}
	
	public void deleteSubmaster(Submaster submaster) {
		this.submasters.remove(submaster);
		
		for (Client client : Main.server().getClients()) {
			client.sendPacket(new PacketPlayOutSubmaster(submaster.getName(), Action.REMOVE));
		}
	}
	
	public void deleteEffect(Effect effect) {
		this.effects.remove(effect);
		
		for (Client client : Main.server().getClients()) {
			client.sendPacket(new PacketPlayOutEffect(effect.getName(), Action.REMOVE));
		}
	}
	
	public void loadSubmasters(File file) {
		JsonArray array = JsonUtil.fromJson(file, JsonArray.class);
		
		if (array == null) {
			return;
		}
		
		for (int i = 0; i < array.size(); i++) {
			JsonObject object = array.get(i).getAsJsonObject();
			Submaster submaster = new Submaster(object);
			
			if (this.getSubmaster(submaster.getName()) != null) {
				Console.println("Skiping submaster " + submaster.getName() + " in loading: already exists");
			}
			
			this.registerSubmaster(submaster);
		}
	}
	
	public void saveSubmasters(File file) {
		JsonArray array = new JsonArray();
		
		for (Submaster submaster : this.submasters) {
			array.add(submaster.serialize());
		}
		
		JsonUtil.toJson(file, array);
	}
	
	public void loadEffects(File file) {
		JsonArray array = JsonUtil.fromJson(file, JsonArray.class);
		
		if (array == null) {
			return;
		}
		
		for (int i = 0; i < array.size(); i++) {
			JsonObject object = array.get(i).getAsJsonObject();
			Effect effect = new Effect(object);
			
			if (this.getEffect(effect.getName()) != null) {
				Console.println("Skiping effect " + effect.getName() + " in loading: already exists");
			}
			
			this.registerEffect(effect);
		}
	}
	
	public void saveEffects(File file) {
		JsonArray array = new JsonArray();
		
		for (Effect effect : this.effects) {
			array.add(effect.serialize());
		}
		
		JsonUtil.toJson(file, array);
	}
	
	public void updateChangedValues() {
		for (short dmxAddress = 1; dmxAddress <= 512; dmxAddress++) {
			this.dmxInterface.sendData(dmxAddress, this.getCurrentValue(dmxAddress));
		}
	}
	
	public boolean isSet(short dmxAddress, InputType type) {
		if (dmxAddress < 1 || dmxAddress > 512) {
			return false;
		}
		
		return this.getFixedValue(dmxAddress, type) != null;
	}
	
	public InputType getCurrentType(short dmxAddress) {
		if (this.isSet(dmxAddress, InputType.CHANNEL)) {
			return InputType.CHANNEL;
		} else if (this.isSet(dmxAddress, InputType.SUBMASTER)) {
			return InputType.SUBMASTER;
		} else if (this.isSet(dmxAddress, InputType.EFFECT)) {
			return InputType.EFFECT;
		} else if (this.isSet(dmxAddress, InputType.SHOW)) {
			return InputType.SHOW;
		} else {
			return InputType.NONE;
		}
	}
	
	public short getCurrentValue(short dmxAddress) {
		return this.getCurrentValue(dmxAddress, this.getCurrentType(dmxAddress));
	}
	
	public short getCurrentValue(short dmxAddress, InputType type) {
		if (dmxAddress < 1 || dmxAddress > 512) {
			return -1;
		}
		
		double prercent = this.getMasterValue() / 255.0D;
		return (short) Math.round(this.convert(this.getFixedValue(dmxAddress, type)) * prercent);
	}
	
	public short getMasterValue() {
		Short value = this.master.getCurrentValue();
		
		if (value == null) {
			return 255;
		} else {
			return value.shortValue();
		}
	}
	
	public MergingMethod getMergingMethod() {
		return this.method;
	}
	
	public Submaster newSubmaster(String name) {
		if (this.getSubmaster(name) != null) {
			throw new RuntimeException("The submaster already exists!");
		}
		
		Submaster submaster = new Submaster(name);
		this.registerSubmaster(submaster);
		return submaster;
	}
	
	public Submaster getSubmaster(String name) {
		for (Submaster submaster : this.submasters) {
			if (submaster.getName().equals(name)) {
				return submaster;
			}
		}

		return null;
	}
	
	public Effect newEffect(String name) {
		if (this.getEffect(name) != null) {
			throw new RuntimeException("The effect already exists!");
		}
		
		Effect effect = new Effect(name);
		this.registerEffect(effect);
		return effect;
	}
	
	public Effect getEffect(String name) {
		for (Effect effect : this.effects) {
			if (effect.getName().equals(name)) {
				return effect;
			}
		}

		return null;
	}
	
	public Show getShow() {
		return this.show;
	}
	
	public DMXInterface getDMXInterface() {
		return this.dmxInterface;
	}
	
	public List<Submaster> getSubmasters() {
		return Collections.unmodifiableList(this.submasters);
	}
	
	public List<Submaster> getSubmasters(short dmxAddress) {
		List<Submaster> result = new ArrayList<>();
		
		for (Submaster submaster : this.submasters) {
			if (submaster.getTargetValues().containsKey(dmxAddress)) {
				result.add(submaster);
			}
		}
		
		return result;
	}
	
	public List<Effect> getEffects() {
		return Collections.unmodifiableList(this.effects);
	}
	
	public List<Effect> getEffects(short dmxAddress) {
		List<Effect> result = new ArrayList<>();
		
		for (Effect effect : this.effects) {
			if (effect.getCurrentValues().containsKey(dmxAddress)) {
				result.add(effect);
			}
		}
		
		return result;
	}
	
	private Short getFixedValue(short dmxAddress, InputType type) {
		if (type == InputType.CHANNEL) {
			return this.getCurrentData(dmxAddress, type).getCurrentValue();
		} else if (type == InputType.SUBMASTER) {
			Submaster submaster = (Submaster) this.getCurrentData(dmxAddress, type);
			
			if (submaster == null || submaster.getCurrentValue() == null) {
				return null;
			}
			
			return submaster.getCurrentValues().get(dmxAddress);
		} else if (type == InputType.EFFECT) {
			Effect effect = (Effect) this.getCurrentData(dmxAddress, type);
			
			if (effect == null || !effect.isRunning()) {
				return null;
			}
			
			return effect.getCurrentValues().get(dmxAddress);
		} else if (type == InputType.SHOW) {
			if (!this.show.isRunning()) {
				return null;
			} else {
				return this.show.getCurrentValues().get(dmxAddress);
			}
		} else if (type == InputType.NONE) {
			return null;
		} else {
			return null;
		}
	}
	
	private DMXData getCurrentData(short dmxAddress, InputType type) {
		if (type == InputType.CHANNEL) {
			return this.addresses.get(dmxAddress);
		} else if (type == InputType.SUBMASTER) {
			short current = -1;
			Submaster submaster = null;
			
			for (Submaster target : this.getSubmasters(dmxAddress)) {
				Short value = target.getCurrentValues().get(dmxAddress);
				
				if (value != null && value > current) {
					current = value;
					submaster = target;
				}
			}
			
			return submaster;
		} else if (type == InputType.EFFECT) {
			short current = -1;
			Effect effect = null;
			
			for (Effect target : this.getEffects(dmxAddress)) {
				Short value = target.getCurrentValues().get(dmxAddress);
				
				if (value != null && value > current) {
					current = value;
					effect = target;
				}
			}
			
			return effect;
		} else if (type == InputType.SHOW) {
			return this.show;
		} else if (type == InputType.NONE) {
			return null;
		} else {
			return null;
		}
	}
	
	private void registerSubmaster(Submaster submaster) {
		if (Main.server() != null) {
			for (Client client : Main.server().getClients()) {
				client.sendPacket(new PacketPlayOutSubmaster(submaster.getName(), Action.ADD));
			}
		}
		
		this.submasters.add(submaster);
	}
	
	private void registerEffect(Effect effect) {
		if (Main.server() != null) {
			for (Client client : Main.server().getClients()) {
				client.sendPacket(new PacketPlayOutEffect(effect.getName(), Action.ADD));
			}
		}
		
		this.effects.add(effect);
	}
	
	private short convert(Short input) {
		if (input == null) {
			return 0;
		} else {
			return input.shortValue();
		}
	}

}
