package de.rincewind.dmxc.common.packets.outgoing;

public enum Action {

	ADD((byte) 0), REMOVE((byte) 1);
	
	private byte id;
	
	private Action(byte id) {
		this.id = id;
	}
	
	public byte getId() {
		return this.id;
	}
	
	public static Action fromId(byte id) {
		for (Action action : Action.values()) {
			if (action.id == id) {
				return action;
			}
		}
		
		return null;
	}
	
}