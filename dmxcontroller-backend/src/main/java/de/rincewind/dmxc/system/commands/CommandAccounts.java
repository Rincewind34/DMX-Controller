package de.rincewind.dmxc.system.commands;

import de.rincewind.commandlib.ModuleCommand;
import de.rincewind.commandlib.ModuleCommand.ExecutorResult;
import de.rincewind.commandlib.Sender;
import de.rincewind.commandlib.anntotations.AnnotatedCommand;
import de.rincewind.commandlib.anntotations.AnnotationExecutor;
import de.rincewind.commandlib.anntotations.components.ActionGroup;
import de.rincewind.commandlib.anntotations.components.Command;
import de.rincewind.commandlib.anntotations.components.Flag;
import de.rincewind.commandlib.anntotations.components.Parameter;
import de.rincewind.commandlib.util.ComponentType;
import de.rincewind.commandlib.util.enums.ListAction;
import de.rincewind.dmxc.common.CommandExecutor;
import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.system.Account;
import de.rincewind.dmxc.system.Main;

@Command(name = "accounts", executor = CommandAccounts.class)
public class CommandAccounts implements CommandExecutor, AnnotationExecutor<CommandAccounts> {

	private static ModuleCommand command;

	static {
		CommandAccounts.command = new AnnotatedCommand<>(CommandAccounts.class, Messages.instance);
	}

	@ActionGroup(name = "action")
	private ListAction action;

	@Parameter(name = "username", bind = { "action.GROUP:add", "action.GROUP:remove" })
	private String username;
	
	@Parameter(name = "password", bind = "action.GROUP:add", type = ComponentType.OPTIONAL)
	private String password;
	
	@Flag(name = "-hard", bind = "action.GROUP:remove")
	private Boolean removeHard;
	
	@Override
	public void execute(String commandLine) {
		CommandAccounts.command.execute(Console.instance(), commandLine);
	}

	@Override
	public ExecutorResult execute(Sender sender, CommandAccounts command) {
		if (this.action == ListAction.LIST) {
			Console.println("Currently are " + Main.management().getAccounts().size() + " accounts registered:");
			
			for (Account account : Main.management().getAccounts()) {
				Console.println("- Username: " + account.getUsername() + ", Password: " + (account.hasPassword() ? "YES" : "NO") + " (In use: "
						+ (account.isInUse() ? "YES" : "NO") + ")");
			}
		} else if (this.action == ListAction.ADD) {
			if (Main.management().getAccount(this.username) != null) {
				Console.println("The username is already in use!");
				return ExecutorResult.SUCCESS;
			}
			
			Main.management().addAccount(new Account(this.username, this.password));
			Console.println("Account added!");
		} else if (this.action == ListAction.REMOVE) {
			if (Main.management().getAccount(this.username) == null) {
				Console.println("The username is unknown!");
				return ExecutorResult.SUCCESS;
			}
			
			Account account = Main.management().getAccount(this.username);
			
			if (account.isInUse()) {
				if (!this.removeHard) {
					Console.println("The account is currently in use!");
					return ExecutorResult.SUCCESS;
				} else {
					Main.management().getClient(account).disconnect();
				}
			}
			
			Main.management().removeAccount(account);
			Console.println("Account removed!");
		}
		
		return ExecutorResult.SUCCESS;
	}

}
