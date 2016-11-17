package de.rincewind.dmxc.system.commands;

import de.rincewind.commandlib.ModuleCommand;
import de.rincewind.commandlib.ModuleCommand.ExecutorResult;
import de.rincewind.commandlib.Sender;
import de.rincewind.commandlib.anntotations.AnnotatedCommand;
import de.rincewind.commandlib.anntotations.AnnotationExecutor;
import de.rincewind.commandlib.anntotations.components.Command;
import de.rincewind.dmxc.common.CommandExecutor;
import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.system.Main;

@Command(name = "stop", executor = CommandStop.class)
public class CommandStop implements CommandExecutor, AnnotationExecutor<CommandStop> {
	
	private static ModuleCommand command;
	
	static {
		CommandStop.command = new AnnotatedCommand<>(CommandStop.class, Messages.instance);
	}
	
	@Override
	public void execute(String commandLine) {
		CommandStop.command.execute(Console.instance(), commandLine);
	}

	@Override
	public ExecutorResult execute(Sender sender, CommandStop command) {
		Main.shutdown();
		return ExecutorResult.SUCCESS;
	}
	
}
