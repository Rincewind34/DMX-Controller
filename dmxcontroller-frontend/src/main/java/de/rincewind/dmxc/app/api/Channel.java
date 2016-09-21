package de.rincewind.dmxc.app.api;

public class Channel implements Fadeable {
	
	private short dmxAddress;
	private Short value;
	
	public Channel(short dmxAddress) {
		this.dmxAddress = dmxAddress;
		this.value = null;
	}
	
	@Override
	public void setValue(Short value) {
		this.value = value;
	}

	@Override
	public Short getValue() {
		return this.value;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Channel ? this.dmxAddress == ((Channel) obj).dmxAddress : false;
	}
	
	@Override
	public String toString() {
		return "Channel (" + this.dmxAddress + ") = " + this.value;
	}
	
	public short getDmxAddress() {
		return this.dmxAddress;
	}
	
}
