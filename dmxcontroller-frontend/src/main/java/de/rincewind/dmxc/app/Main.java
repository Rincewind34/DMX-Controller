package de.rincewind.dmxc.app;

import java.io.File;
import java.util.Optional;

import de.rincewind.dmxc.app.api.Channel;
import de.rincewind.dmxc.app.gui.Fader;
import de.rincewind.dmxc.app.gui.SubmasterFader;
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
	
//	private static final String TAB_DRAG_KEY = "titledpane";
//	private ObjectProperty<TitledPane> draggingTab;
//
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		this.draggingTab = new SimpleObjectProperty<>();
//		VBox vbox = new VBox();
//		for (int i = 0; i < 4; i++) {
//			final TitledPane pane = new TitledPane();
//			pane.setText("pane" + (i + 1));
//			vbox.getChildren().add(pane);
//			pane.setOnDragOver(new EventHandler<DragEvent>() {
//				@Override
//				public void handle(DragEvent event) {
//					final Dragboard dragboard = event.getDragboard();
//					if (dragboard.hasString() && Main.TAB_DRAG_KEY.equals(dragboard.getString()) && Main.this.draggingTab.get() != null) {
//						event.acceptTransferModes(TransferMode.MOVE);
//						event.consume();
//					}
//				}
//			});
//			pane.setOnDragDropped(new EventHandler<DragEvent>() {
//				@Override
//				public void handle(final DragEvent event) {
//					Dragboard db = event.getDragboard();
//					boolean success = false;
//					if (db.hasString()) {
//						Pane parent = (Pane) pane.getParent();
//						Object source = event.getGestureSource();
//						int sourceIndex = parent.getChildren().indexOf(source);
//						int targetIndex = parent.getChildren().indexOf(pane);
//						List<Node> nodes = new ArrayList<>(parent.getChildren().stream().collect(Collectors.toList()));
//						if (sourceIndex < targetIndex) {
//							Collections.rotate(nodes.subList(sourceIndex, targetIndex + 1), -1);
//						} else {
//							Collections.rotate(nodes.subList(targetIndex, sourceIndex + 1), 1);
//						}
//						parent.getChildren().clear();
//						parent.getChildren().addAll(nodes.toArray(new Node[nodes.size()]));
//						success = true;
//					}
//					event.setDropCompleted(success);
//					event.consume();
//				}
//			});
//			pane.setOnDragDetected(new EventHandler<MouseEvent>() {
//				@Override
//				public void handle(MouseEvent event) {
//					Dragboard dragboard = pane.startDragAndDrop(TransferMode.MOVE);
//					ClipboardContent clipboardContent = new ClipboardContent();
//					clipboardContent.putString(Main.TAB_DRAG_KEY);
//					dragboard.setContent(clipboardContent);
//					Main.this.draggingTab.set(pane);
//					event.consume();
//				}
//			});
//		}
//		TitledPane pane = new TitledPane("MAIN", vbox);
//		primaryStage.setScene(new Scene(pane, 890, 570));
//		primaryStage.show();
//	}

	public static void shutdown() {
		Main.inShutdown = true;
		Console.println("Shuting down client");
		
		if (Main.templateFile.exists()) {
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
			Main.template = new Template();
			
			for (int i = 1; i <= 10; i++) {
				Fader fader = new Fader(Channel.fromAddress((short) i));
				fader.setCaption("Channel " + i);
				Main.template.addComponent(fader);
			}
			
			Main.template.addComponent(new SubmasterFader());
			
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
