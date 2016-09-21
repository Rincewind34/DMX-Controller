package de.rincewind.dmxc.app;

import de.rincewind.dmxc.app.commands.CommandConnect;
import de.rincewind.dmxc.app.commands.CommandStop;
import de.rincewind.dmxc.app.gui.Template;
import de.rincewind.dmxc.app.network.Client;
import de.rincewind.dmxc.common.Console;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Client client;
	
	public static void main(String[] args) {
		Main.client = new Client();
		
		new Thread(() -> {
			Console.initConsole();
			Console.registerCommand("stop", new CommandStop());
			Console.registerCommand("connect", new CommandConnect());
			Console.startConsole();
		}).start();
		
		Application.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(new Template().getRootPane()));
		primaryStage.show();
	}
	
	public static void shutdown() {
		Platform.exit();
		
		if (Main.client.isConnected()) {
			Main.client.disconnect();
		}
		
		Console.closeConsole();
	}

	public static Client client() {
		return Main.client;
	}

}
