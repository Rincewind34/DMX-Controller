package de.rincewind.dmxc.system.commands;

import de.rincewind.commandlib.ModuleCommand;
import de.rincewind.commandlib.ModuleCommand.ExecutorResult;
import de.rincewind.commandlib.Sender;
import de.rincewind.commandlib.anntotations.AnnotatedCommand;
import de.rincewind.commandlib.anntotations.AnnotationExecutor;
import de.rincewind.commandlib.anntotations.components.ActionGroup;
import de.rincewind.commandlib.anntotations.components.Command;
import de.rincewind.commandlib.anntotations.components.Parameter;
import de.rincewind.commandlib.util.enums.ComplexListAction;
import de.rincewind.commandlib.util.enums.SimpleListAction;
import de.rincewind.dmxc.common.CommandExecutor;
import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.environment.Submaster;

@Command(name = "submaster", executor = CommandSubmaster.class)
public class CommandSubmaster implements CommandExecutor, AnnotationExecutor<CommandSubmaster> {

	private static ModuleCommand command;

	static {
		CommandSubmaster.command = new AnnotatedCommand<>(CommandSubmaster.class, Messages.instance);
	}

	@ActionGroup(name = "action")
	private ComplexListAction action;

	@Parameter(name = "submaster", bind = { "action.GROUP:add", "action.GROUP:remove", "action.GROUP:edit" })
	private String submaster;
	
	@ActionGroup(name = "editaction", bind = "action.GROUP:edit")
	private SimpleListAction editaction;
	
	@Parameter(name = "dmxaddress", bind = { "action.GROUP:edit.editaction.GROUP:add", "action.GROUP:edit.editaction.GROUP:remove" })
	private Integer dmxAddress;
	
	@Parameter(name = "targetvalue", bind = "action.GROUP:edit.editaction.GROUP:add")
	private Integer targetValue;
	
	@Override
	public void execute(String commandLine) {
		CommandSubmaster.command.execute(Console.instance(), commandLine);
	}

	@Override
	public ExecutorResult execute(Sender sender, CommandSubmaster command) {
		if (this.action == ComplexListAction.LIST) {
			Console.println("Currently are " + Main.environment().getSubmasters().size() + " submasters registered:");
			
			for (Submaster submaster : Main.environment().getSubmasters()) {
				Console.println("- " + submaster.getName() + ": " + submaster.toString());
			}
		} else if (this.action == ComplexListAction.ADD) {
			if (Main.environment().getSubmaster(this.submaster) != null) {
				Console.println("This submaster already exists!");
				return ExecutorResult.SUCCESS;
			}
			
			Main.environment().newSubmaster(this.submaster);
			Console.println("Submaster created!");
		} else if (this.action == ComplexListAction.REMOVE) {
			if (Main.environment().getSubmaster(this.submaster) == null) {
				Console.println("This submaster does not exist!");
				return ExecutorResult.SUCCESS;
			}
			
			Main.environment().getSubmaster(this.submaster).delete();
			Console.println("Submaster deleted!");
		} else if (this.action == ComplexListAction.EDIT) {
			if (Main.environment().getSubmaster(this.submaster) == null) {
				Console.println("This submaster does not exist!");
				return ExecutorResult.SUCCESS;
			}
			
			Submaster submaster = Main.environment().getSubmaster(this.submaster);
			
			if (this.dmxAddress < 1 || this.dmxAddress > 512) {
				Console.println("The dmx-address is out of range!");
				return ExecutorResult.SUCCESS;
			}
			
			if (this.editaction == SimpleListAction.ADD) {
				if (this.targetValue < 0 || this.targetValue > 255) {
					Console.println("The target value is out of range!");
					return ExecutorResult.SUCCESS;
				}
				
				submaster.setTargetValue(this.dmxAddress.shortValue(), this.targetValue.shortValue());
				Console.println("Submaster edited!");
			} else if (this.editaction == SimpleListAction.REMOVE) {
				submaster.removeTargetValue(this.dmxAddress.shortValue());
				Console.println("Submaster edited!");
			}
		}
		
		return ExecutorResult.SUCCESS;
	}

}
