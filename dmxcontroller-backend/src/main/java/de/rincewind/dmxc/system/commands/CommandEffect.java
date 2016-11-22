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
import de.rincewind.commandlib.util.enums.ComplexListAction;
import de.rincewind.commandlib.util.enums.SimpleListAction;
import de.rincewind.dmxc.common.CommandExecutor;
import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.system.Main;
import de.rincewind.dmxc.system.environment.Effect;
import de.rincewind.dmxc.system.environment.Scene;
import de.rincewind.dmxc.system.environment.SceneRegistry;

/*
 *   /effect edit <effect> list
 *   /effect edit <effect> add <scene> [replace]
 *   /effect edit <effect> remove <index>
 *   /effect edit <effect> scene <index> add <dmxAddress> <value>
 *   /effect edit <effect> scene <index> remove <dmxAddress>
 *   /effect edit <effect> setfade <index> <value>
 *   /effect edit <effect> setstay <index> <value>
 *   /effect edit <effect> dimmer {add | remove} <dmxAddress>
 */

@Command(name = "effect", executor = CommandEffect.class)
public class CommandEffect implements CommandExecutor, AnnotationExecutor<CommandEffect> {

	private static ModuleCommand command;

	static {
		CommandEffect.command = new AnnotatedCommand<>(CommandEffect.class, Messages.instance);
	}

	@ActionGroup(name = "action")
	private ComplexListAction action;

	@Parameter(name = "effect", bind = { "action.GROUP:add", "action.GROUP:remove", "action.GROUP:edit" })
	private String effect;

	@ActionGroup(name = "effectaction", bind = "action.GROUP:edit")
	private EffectAction effectaction;

	@Parameter(name = "scene", bind = "action.GROUP:edit.effectaction.GROUP:add")
	private String scene;

	@Parameter(name = "replace", bind = "action.GROUP:edit.effectaction.GROUP:add", type = ComponentType.OPTIONAL)
	private Integer replaceIndex;

	@Parameter(name = "index", bind = { "action.GROUP:edit.effectaction.GROUP:remove", "action.GROUP:edit.effectaction.GROUP:scene",
			"action.GROUP:edit.effectaction.GROUP:setfade", "action.GROUP:edit.effectaction.GROUP:setstay" })
	private Integer index;

	@ActionGroup(name = "sceneaction", bind = "action.GROUP:edit.effectaction.GROUP:scene")
	private SimpleListAction sceneaction;

	@ActionGroup(name = "dimmeraction", bind = "action.GROUP:edit.effectaction.GROUP:dimmer")
	private SimpleListAction dimmeraction;

	@Parameter(name = "dmxAddress", bind = { "action.GROUP:edit.effectaction.GROUP:scene.sceneaction.GROUP:add",
			"action.GROUP:edit.effectaction.GROUP:scene.sceneaction.GROUP:remove", "action.GROUP:edit.effectaction.GROUP:dimmer" })
	private Integer dmxAddress;

	@Parameter(name = "value", bind = { "action.GROUP:edit.effectaction.GROUP:scene.sceneaction.GROUP:add", "action.GROUP:edit.effectaction.GROUP:setfade",
			"action.GROUP:edit.effectaction.GROUP:setstay" })
	private Integer value;

	@Override
	public void execute(String commandLine) {
		CommandEffect.command.execute(Console.instance(), commandLine);
	}

	@Override
	public ExecutorResult execute(Sender sender, CommandEffect command) {
		if (this.action == ComplexListAction.LIST) {
			Console.println("Currently are " + Main.environment().getEffects().size() + " effects registered:");

			for (Effect effect : Main.environment().getEffects()) {
				Console.println("- " + effect.getName() + ": " + effect.toString());
			}
		} else if (this.action == ComplexListAction.ADD) {
			if (Main.environment().getEffect(this.effect) != null) {
				Console.println("This effect already exists!");
				return ExecutorResult.SUCCESS;
			}

			Main.environment().newEffect(this.effect);
			Console.println("Effect created!");
		} else if (this.action == ComplexListAction.REMOVE) {
			if (Main.environment().getEffect(this.effect) == null) {
				Console.println("This effect does not exist!");
				return ExecutorResult.SUCCESS;
			}

			if (Main.environment().getEffect(this.effect).isRunning()) {
				Console.println("This effect is currently running!");
				return ExecutorResult.SUCCESS;
			}

			Main.environment().getEffect(this.effect).delete();
			Console.println("Effect deleted!");
		} else if (this.action == ComplexListAction.EDIT) {
			if (Main.environment().getEffect(this.effect) == null) {
				Console.println("This effect does not exist!");
				return ExecutorResult.SUCCESS;
			}

			if (Main.environment().getEffect(this.effect).isRunning()) {
				Console.println("This effect is currently running!");
				return ExecutorResult.SUCCESS;
			}

			Effect effect = Main.environment().getEffect(this.effect);

			if (this.effectaction == EffectAction.ADD) {
				Scene scene = SceneRegistry.getScene(this.scene);
				
				if (scene == null) {
					scene = new Scene(this.scene);
				}

				if (this.replaceIndex != null) {
					effect.addScene(this.replaceIndex, scene);
				} else {
					effect.addScene(scene);
				}

				Console.println("Effect edited!");
			} else if (this.effectaction == EffectAction.REMOVE) {
				if (this.replaceIndex >= effect.getScenes().size()) {
					Console.println("The given index is out of range!");
					return ExecutorResult.SUCCESS;
				}

				effect.removeScene(this.replaceIndex);
				Console.println("Effect edited!");
			} else if (this.effectaction == EffectAction.LIST) {
				// TODO
			} else if (this.effectaction == EffectAction.SCENE) {
				if (this.index >= effect.getScenes().size()) {
					Console.println("The given index is out of range!");
					return ExecutorResult.SUCCESS;
				}
				
				Scene scene = effect.getScene(this.index);
				
				if (this.dmxAddress < 1 || 512 < this.dmxAddress) {
					Console.println("The dmx address is out of range!");
					return ExecutorResult.SUCCESS;
				}

				if (this.sceneaction == SimpleListAction.ADD) {
					if (this.value < 0 || 255 < this.value) {
						Console.println("The dmx value is out of range!");
						return ExecutorResult.SUCCESS;
					}

					scene.setTargetValue(this.dmxAddress.shortValue(), this.value.shortValue());
					Console.println("Effect edited!");
				} else if (this.sceneaction == SimpleListAction.REMOVE) {
					scene.removeTargetValue(this.dmxAddress.shortValue());
					Console.println("Effect edited!");
				}
			} else if (this.effectaction == EffectAction.DIMMER) {
				if (this.dmxAddress < 1 || 512 < this.dmxAddress) {
					Console.println("The dmx address is out of range!");
					return ExecutorResult.SUCCESS;
				}

				if (this.dimmeraction == SimpleListAction.ADD) {
					effect.addDimmer(this.dmxAddress.shortValue());
					Console.println("Effect edited!");
				} else if (this.dimmeraction == SimpleListAction.REMOVE) {
					effect.removeDimmer(this.dmxAddress.shortValue());
					Console.println("Effect edited!");
				}
			} else if (this.effectaction == EffectAction.SET_FADE) {
				if (this.index >= effect.getScenes().size()) {
					Console.println("The given index is out of range!");
					return ExecutorResult.SUCCESS;
				}

				if (this.value <= 0) {
					Console.println("The value is out of range!");
					return ExecutorResult.SUCCESS;
				}

				Scene scene = effect.getScene(this.index);
				scene.setFadeIn(this.value);
				Console.println("Effect edited!");
			} else if (this.effectaction == EffectAction.SET_STAY) {
				if (this.index >= effect.getScenes().size()) {
					Console.println("The given index is out of range!");
					return ExecutorResult.SUCCESS;
				}

				if (this.value <= 0) {
					Console.println("The value is out of range!");
					return ExecutorResult.SUCCESS;
				}

				Scene scene = effect.getScene(this.index);
				scene.setStay(this.value);
				Console.println("Effect edited!");
			}
		}

		return ExecutorResult.SUCCESS;
	}

	public static enum EffectAction {

		ADD, REMOVE, LIST, SCENE, DIMMER, SET_FADE, SET_STAY;

		public static EffectAction fromCommandLine(String input) {
			if (input.equals("add")) {
				return EffectAction.ADD;
			} else if (input.equals("remove")) {
				return EffectAction.REMOVE;
			} else if (input.equals("list")) {
				return EffectAction.LIST;
			} else if (input.equals("scene")) {
				return EffectAction.SCENE;
			} else if (input.equals("dimmer")) {
				return EffectAction.DIMMER;
			} else if (input.equals("setfade")) {
				return EffectAction.SET_FADE;
			} else if (input.equals("setstay")) {
				return EffectAction.SET_STAY;
			} else {
				return null;
			}
		}

		public static String toCommandLine(EffectAction input) {
			if (input == EffectAction.ADD) {
				return "add";
			} else if (input == EffectAction.REMOVE) {
				return "remove";
			} else if (input == EffectAction.LIST) {
				return "list";
			} else if (input == EffectAction.SCENE) {
				return "scene";
			} else if (input == EffectAction.DIMMER) {
				return "dimmer";
			} else if (input == EffectAction.SET_FADE) {
				return "setfade";
			} else if (input == EffectAction.SET_STAY) {
				return "setstay";
			} else {
				return null;
			}
		}

	}

}
