package de.rincewind.dmxc.app.commands;

import de.rincewind.commandlib.CommandMessages;
import de.rincewind.commandlib.commandline.CommandLineElement;

public class Messages extends CommandMessages {
	
	public final static Messages instance;
	
	static {
		instance = new Messages();
	}
	
	@Override
	public String syntax(String syntax) {
		return "Syntax: " + syntax;
	}

	@Override
	public String invalidNumberFormat(String parameter, NumberType numberType) {
		return "The parameter '" + parameter + "' has to match to the format '" + numberType.name() + "'";
	}

	@Override
	public String invalidStringFormat(String parameter) {
		return "The parameter '" + parameter + "' does not match to the format";
	}

	@Override
	public String invalidLineElementType(Class<? extends CommandLineElement> cls) {
		return "The element '" + cls.getSimpleName() + "' is invalid!";
	}

}
