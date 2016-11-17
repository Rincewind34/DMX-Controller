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

@Command(name = "dmxvalues", executor = CommandDMXValues.class)
public class CommandDMXValues implements CommandExecutor, AnnotationExecutor<CommandDMXValues> {
	
	private static ModuleCommand command;
	
	static {
		CommandDMXValues.command = new AnnotatedCommand<>(CommandDMXValues.class, Messages.instance);
	}
	
	@Override
	public void execute(String commandLine) {
		CommandDMXValues.command.execute(Console.instance(), commandLine);
	}

	@Override
	public ExecutorResult execute(Sender sender, CommandDMXValues command) {
		for (int i = 1; i <= 64; i++) {
			StringBuilder builder = new StringBuilder();
			
			for (int j = 1; j <= 8; j++) {
				short dmxAddress = (short) ((i - 1) * 8 + j);
				
				this.format(builder, dmxAddress);
				builder.append(": ");
				builder.append(Main.environment().getCurrentType(dmxAddress).getConsoleColor());
				this.format(builder, Main.environment().getCurrentValue(dmxAddress));
				builder.append(Console.ANSI_RESET);
				
				if (j != 8) {
					builder.append(" | ");
				}
			}
			
			Console.println(builder.toString());
		}
		
		return ExecutorResult.SUCCESS;
	}
	
	private void format(StringBuilder builder, int input) {
		if (input < 100) {
			builder.append(" ");
		}
		
		if (input < 10) {
			builder.append(" ");
		}
		
		builder.append(input);
	}
	
}
