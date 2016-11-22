package de.rincewind.dmxc.system.commands;

import de.rincewind.commandlib.ModuleCommand;
import de.rincewind.commandlib.ModuleCommand.ExecutorResult;
import de.rincewind.commandlib.Sender;
import de.rincewind.commandlib.anntotations.AnnotatedCommand;
import de.rincewind.commandlib.anntotations.AnnotationExecutor;
import de.rincewind.commandlib.anntotations.components.ActionGroup;
import de.rincewind.commandlib.anntotations.components.Command;
import de.rincewind.commandlib.anntotations.components.Parameter;
import de.rincewind.commandlib.util.ComponentType;
import de.rincewind.commandlib.util.enums.SimpleListAction;
import de.rincewind.dmxc.common.CommandExecutor;
import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.environment.Scene;

/*
 *   /show next
 *   /show previous
 *   /show info [optIndex]
 *   /show edit add <scene> [optIndex]
 *   /show edit remove <index>
 *   /show edit scene <index> add <dmxAddress> <value>
 *   /show edit scene <index> remove <dmxAddress>
 *   /show edit setfade <index> <value>
 *   /show edit setstay <index> <value>
 *   /show edit dimmer {add | remove} <dmxAddress>
 */
@Command(name = "show", executor = CommandShow.class)
public class CommandShow implements CommandExecutor, AnnotationExecutor<CommandShow> {

	private static ModuleCommand command;

	static {
		CommandShow.command = new AnnotatedCommand<>(CommandShow.class, Messages.instance);
	}

	@ActionGroup(name = "action")
	private ShowAction action;

	@ActionGroup(name = "editaction", bind = "action.GROUP:edit")
	private ShowEditAction editaction;

	@Parameter(name = "scene", bind = "action.GROUP:edit.editaction.GROUP:add")
	private String scene;

	@Parameter(name = "optIndex", bind = { "action.GROUP:edit.editaction.GROUP:add", "action.GROUP:info" }, type = ComponentType.OPTIONAL)
	private Integer optIndex;

	@Parameter(name = "index", bind = { "action.GROUP:edit.editaction.GROUP:remove", "action.GROUP:edit.editaction.GROUP:scene",
			"action.GROUP:edit.editaction.GROUP:setfade", "action.GROUP:edit.editaction.GROUP:setstay" })
	private Integer index;

	@ActionGroup(name = "sceneaction", bind = "action.GROUP:edit.editaction.GROUP:scene")
	private SimpleListAction sceneaction;

	@ActionGroup(name = "dimmeraction", bind = "action.GROUP:edit.editaction.GROUP:dimmer")
	private SimpleListAction dimmeraction;

	@Parameter(name = "dmxAddress", bind = { "action.GROUP:edit.editaction.GROUP:scene.sceneaction.GROUP:add",
			"action.GROUP:edit.editaction.GROUP:scene.sceneaction.GROUP:remove", "action.GROUP:edit.editaction.GROUP:dimmer" })
	private Integer dmxAddress;

	@Parameter(name = "value", bind = { "action.GROUP:edit.editaction.GROUP:scene.sceneaction.GROUP:add", "action.GROUP:edit.editaction.GROUP:setfade",
			"action.GROUP:edit.editaction.GROUP:setstay" })
	private Integer value;
	
	@Override
	public void execute(String commandLine) {
		CommandShow.command.execute(Console.instance(), commandLine);
	}

	@Override
	public ExecutorResult execute(Sender sender, CommandShow command) {
		if (this.action == ShowAction.NEXT) {
			if (!Main.environment().getShow().isRunning()) {
				Console.println("The show is not running!");
				return ExecutorResult.SUCCESS;
			}
			
			if (Main.environment().getShow().getCurrentIndex() == Main.environment().getShow().getScenes().size() - 1) {
				Console.println("Already at last scene!");
				return ExecutorResult.SUCCESS;
			}
			
			Main.environment().getShow().nextScene();
			Console.println("Scene switched!");
		} else if (this.action == ShowAction.PREV) {
			if (!Main.environment().getShow().isRunning()) {
				Console.println("The show is not running!");
				return ExecutorResult.SUCCESS;
			}
			
			if (Main.environment().getShow().getCurrentIndex() == 0) {
				Console.println("Already at first scene!");
				return ExecutorResult.SUCCESS;
			}
			
			Main.environment().getShow().previousScene();
			Console.println("Scene switched!");
			
		} else if (this.action == ShowAction.INFO) {
			Console.println("Show (running: " + Main.environment().getShow().isRunning() + "; current: " + Main.environment().getShow().getCurrentIndex() + ")");
			
			for (Scene scene : Main.environment().getShow().getScenes()) {
				Console.println("- " + scene.getName() + " (fade time: " + scene.getFadeIn() + ")");
			}
		}
		
		return ExecutorResult.SUCCESS;
	}
	
	public static enum ShowAction {
		
		NEXT, PREV, INFO, EDIT;
		
		public static ShowAction fromCommandLine(String input) {
			if (input.equals("next")) {
				return ShowAction.NEXT;
			} else if (input.equals("prev")) {
				return ShowAction.PREV;
			} else if (input.equals("info")) {
				return ShowAction.INFO;
			} else if (input.equals("edit")) {
				return ShowAction.EDIT;
			} else {
				return null;
			}
		}

		public static String toCommandLine(ShowAction input) {
			if (input == ShowAction.NEXT) {
				return "next";
			} else if (input == ShowAction.PREV) {
				return "prev";
			} else if (input == ShowAction.INFO) {
				return "info";
			} else if (input == ShowAction.EDIT) {
				return "edit";
			} else {
				return null;
			}
		}
		
	}
	
	public static enum ShowEditAction {

		ADD, REMOVE, LIST, SCENE, DIMMER, SET_FADE, SET_STAY;

		public static ShowEditAction fromCommandLine(String input) {
			if (input.equals("add")) {
				return ShowEditAction.ADD;
			} else if (input.equals("remove")) {
				return ShowEditAction.REMOVE;
			} else if (input.equals("list")) {
				return ShowEditAction.LIST;
			} else if (input.equals("scene")) {
				return ShowEditAction.SCENE;
			} else if (input.equals("dimmer")) {
				return ShowEditAction.DIMMER;
			} else if (input.equals("setfade")) {
				return ShowEditAction.SET_FADE;
			} else if (input.equals("setstay")) {
				return ShowEditAction.SET_STAY;
			} else {
				return null;
			}
		}

		public static String toCommandLine(ShowEditAction input) {
			if (input == ShowEditAction.ADD) {
				return "add";
			} else if (input == ShowEditAction.REMOVE) {
				return "remove";
			} else if (input == ShowEditAction.LIST) {
				return "list";
			} else if (input == ShowEditAction.SCENE) {
				return "scene";
			} else if (input == ShowEditAction.DIMMER) {
				return "dimmer";
			} else if (input == ShowEditAction.SET_FADE) {
				return "setfade";
			} else if (input == ShowEditAction.SET_STAY) {
				return "setstay";
			} else {
				return null;
			}
		}

	}

}
