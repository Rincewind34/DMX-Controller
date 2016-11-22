package de.rincewind.dmxc.app.gui;

import com.google.gson.JsonElement;

import de.rincewind.dmxc.app.api.Effect;

public class EffectFader extends MultiFader<Effect> {
	
	public EffectFader() {
		super(Effect.class, "effect", "Effect");
		
		this.setCaption("Effects");
		
		TemplateComponent.setBackgroundColor(this.getToolPane(), Fader.COLOR_EFFECT);
	}
	
	protected EffectFader(JsonElement element) {
		this();
		
		this.deserialize(element);
	}
	
	@Override
	public String getType() {
		return "effectfader";
	}
	
	@Override
	public TemplateComponent newOne() {
		return new EffectFader();
	}
	
	@Override
	protected String getTooltip() {
		return "Effects\n\nThis tool allows you to control multiple\neffects on one panel.";
	}
	
	@Override
	protected Effect[] newArray(int size) {
		return new Effect[size];
	}
	
}
