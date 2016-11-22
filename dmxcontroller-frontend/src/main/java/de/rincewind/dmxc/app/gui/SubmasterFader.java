package de.rincewind.dmxc.app.gui;

import com.google.gson.JsonElement;

import de.rincewind.dmxc.app.api.Submaster;

public class SubmasterFader extends MultiFader<Submaster> {
	
	public SubmasterFader() {
		super(Submaster.class, "submaster", "Submaster");
		
		this.setCaption("Submasters");
		
		TemplateComponent.setBackgroundColor(this.getToolPane(), Fader.COLOR_SUBMASTER);
	}
	
	protected SubmasterFader(JsonElement element) {
		this();
		
		this.deserialize(element);
	}
	
	@Override
	public String getType() {
		return "submasterfader";
	}

	@Override
	protected Submaster[] newArray(int size) {
		return new Submaster[size];
	}
	
}
