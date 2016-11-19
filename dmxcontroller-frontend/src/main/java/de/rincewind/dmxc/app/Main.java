package de.rincewind.dmxc.app;

import java.io.File;
import java.util.Optional;

import com.google.gson.JsonObject;

import de.rincewind.dmxc.app.gui.Template;
import de.rincewind.dmxc.app.gui.dialog.Credential;
import de.rincewind.dmxc.app.gui.dialog.DialogCredetial;
import de.rincewind.dmxc.app.network.Client;
import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.util.FileUtil;
import de.rincewind.dmxc.common.util.JsonUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Main extends Application {

	private static Client client;
	
	private static Stage stage;
	
	private static boolean inShutdown;
	private static boolean inDisconnect;
	
	private static Template template;
	
	private static File templateFile;
	
	public static void main(String[] args) {
		Main.inShutdown = false;
		Main.client = new Client();
		
		Main.templateFile = new File("template.json");
		FileUtil.setupFile(Main.templateFile);
		
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Main.stage = stage;
		Main.stage.setOnHidden((event) -> {
			if (Main.inShutdown) {
				return;
			}
			
			if (Main.templateFile.exists()) {
				JsonUtil.toJson(Main.templateFile, Main.template.serialize());
			}
			
			if (!Main.inDisconnect) {
				Main.client.disconnect();
			} else {
				Main.inDisconnect = false;
			}
			
			new Thread(() -> {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				Platform.runLater(() -> {
					Main.openConnectDialog(Main.client.asCredential());
				});
			}).start();
		});
		
		Platform.setImplicitExit(false);
		Main.openConnectDialog(new Credential("localhost", 2345, "user"));
	}
	
	public static void shutdown() {
		Main.inShutdown = true;
		Console.println("Shuting down client");
		
		if (Main.templateFile.exists() && Main.template != null) {
			JsonUtil.toJson(Main.templateFile, Main.template.serialize());
		}
		
		Platform.exit();
		
		if (Main.client.isConnected()) {
			Main.client.disconnect();
		}
	}
	
	public static void openConnectDialog(Credential backup) {
		if (Main.inShutdown || Main.client.isConnected()) {
			return;
		}
		
		DialogCredetial dialog = new DialogCredetial(backup);
		Optional<Credential> optional = dialog.showAndWait();
		
		if (!optional.isPresent()) {
			Main.shutdown();
			return;
		}
		
		Credential result = optional.get();
		
		Main.client.setHostname(result.getHostname());
		Main.client.setPort(result.getPort());
		Main.client.setUsername(result.getUsername());
		Main.client.setPassword(result.getPassword());
		Main.client.connect();
		
		if (!Main.client.isConnected()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Der Server konnte nicht gefunden werden!");
			Main.connectResponse(alert, new Credential());
		}
	}
	
	public static void connectResponse(Alert alert, Credential backup) {
		Platform.runLater(() -> {
			alert.setTitle("DMXController - Verbindungsaufbau fehlgeschalgen");
			alert.setHeaderText("Es konnte keine Verbindung zum Server ausgebaut werden");
			alert.setAlertType(AlertType.ERROR);
			alert.showAndWait();
			Main.openConnectDialog(backup);
		});
	}
	
	public static void showMainWindow() {
		Platform.runLater(() -> {
			Main.template = new Template(JsonUtil.fromJson(Main.templateFile, JsonObject.class));
			
//			for (int i = 0; i < 10; i++) {
//				Main.template.addComponent(new Fader(Channel.fromAddress((short) (i + 1))));
//			}
//			
//			Main.template.addComponent(new SubmasterFader());
			
			Main.stage.setScene(new Scene(Main.template));
			Main.stage.show();
		});
	}
	
	public static void hideMainWindow() {
		if (Main.inShutdown || !Main.stage.isShowing()) {
			return;
		}
		
		Platform.runLater(() -> {
			Main.inDisconnect = true;
			Main.stage.hide();
		});
	}

	public static Client client() {
		return Main.client;
	}

}
