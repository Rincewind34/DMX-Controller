package de.rincewind.dmxc.app.commands;

import de.rincewind.commandlib.ModuleCommand;
import de.rincewind.commandlib.ModuleCommand.ExecutorResult;
import de.rincewind.commandlib.Sender;
import de.rincewind.commandlib.anntotations.AnnotatedCommand;
import de.rincewind.commandlib.anntotations.AnnotationExecutor;
import de.rincewind.commandlib.anntotations.components.Command;
import de.rincewind.commandlib.anntotations.components.Parameter;
import de.rincewind.commandlib.util.ComponentType;
import de.rincewind.dmxc.app.network.Client;
import de.rincewind.dmxc.common.CommandExecutor;
import de.rincewind.dmxc.common.Console;

@Command(name = "connect", executor = CommandConnect.class)
public class CommandConnect implements CommandExecutor, AnnotationExecutor<CommandConnect> {
	
	private static ModuleCommand command;
	
	static {
		CommandConnect.command = new AnnotatedCommand<>(CommandConnect.class, Messages.instance);
	}
	
	@Parameter(name = "host")
	private String host;
	
	@Parameter(name = "username")
	private String username;
	
	@Parameter(name = "password", type = ComponentType.OPTIONAL)
	private String password;
	
	@Override
	public void execute(String commandLine) {
		CommandConnect.command.execute(Console.instance(), commandLine);
	}
	
	@Override
	public ExecutorResult execute(Sender sender, CommandConnect command) {
		String hostname;
		int port = 24578;
		
		if (this.host.contains(":")) {
			hostname = this.host.split("\\:")[0];
			
			try {
				port = Integer.parseInt(this.host.split("\\:")[1]);
			} catch (NumberFormatException ex) {
				sender.sendMessage("The port could not be parsed!");
			}
		} else {
			hostname = this.host;
		}
		
		Client.get().setHostname(hostname);
		Client.get().setPort(port);
		Client.get().setUsername(this.username);
		Client.get().setPassword(this.password == null ? "" : this.password);
		Client.get().connect();
		return ExecutorResult.SUCCESS;
	}
	
}
