package de.rincewind.dmxc.app.gui.dialog;

import de.rincewind.dmxc.app.gui.util.FileLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class DialogCredetial extends Dialog<Credential> {
	
	public static void setupButton(Node button) {
		button.setStyle("-fx-background-radius: 0.0px");
	}
	
	public DialogCredetial(Credential credential) {
		ButtonType cancelType = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		ButtonType connectType = new ButtonType("Verbinden", ButtonData.OK_DONE);

		this.setTitle("DMXController - Call detail records");
		this.setHeaderText("Enter the call detail records.");
		this.getDialogPane().getButtonTypes().addAll(cancelType, connectType);

		Node btnConnect = this.getDialogPane().lookupButton(connectType);
		
		DialogCredetial.setupButton(btnConnect);
		DialogCredetial.setupButton(this.getDialogPane().lookupButton(cancelType));
		
		DialogContent content = this.new DialogContent(btnConnect, credential);
		FileLoader.loadFXML(content, "dialog/credential.fxml");
		content.init();
		this.getDialogPane().setContent(content);

		this.setResultConverter((clickedType) -> {
			if (clickedType == connectType) {
				return new Credential(content.textHostname.getText(), Integer.parseInt(content.textPort.getText()), content.textUsername.getText(),
						content.textPassword.getText());
			} else {
				return null;
			}
		});
	}
	
	
	private class DialogContent extends VBox {
		
		@FXML
		private TextField textHostname;

		@FXML
		private TextField textPort;

		@FXML
		private TextField textUsername;

		@FXML
		private PasswordField textPassword;

		private Node confirmButton;
		private Credential credential;
		
		private boolean skip;
		
		public DialogContent(Node confirmButton, Credential credential) {
			this.confirmButton = confirmButton;
			this.credential = credential;
			this.skip = false;
		}
		
		private void init() {
			if (this.credential.getHostname() != null) {
				this.textHostname.setText(this.credential.getHostname());
			}
			
			if (this.credential.getUsername() != null) {
				this.textUsername.setText(this.credential.getUsername());
			}
			
			if (this.credential.getPassword() != null) {
				this.textPassword.setText(this.credential.getPassword());
			}
			
			if (this.credential.getPort() > 0) {
				this.textPort.setText(Integer.toString(this.credential.getPort()));
			}
			
			this.textHostname.textProperty().addListener((observable, oldValue, newValue) -> {
				this.check();
			});

			this.textPort.textProperty().addListener((observable, oldValue, newValue) -> {
				if (this.skip) {
					this.skip = false;
					return;
				}
				
				if (!newValue.isEmpty()) {
					try {
						Integer.parseInt(newValue);
					} catch (Exception ex) {
						this.skip = true;
						this.textPort.setText(oldValue);
						return;
					}
				}
				
				this.check();
			});

			this.textUsername.textProperty().addListener((observable, oldValue, newValue) -> {
				this.check();
			});
			
			this.textPassword.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue == null || newValue.isEmpty()) {
					this.textPassword.setStyle("-fx-font-size: 17.0px;");
				} else {
					this.textPassword.setStyle("-fx-font-size: 10.0px;");
				}
			});

			this.check();
		}
		
		private void check() {
			this.confirmButton.setDisable(this.textHostname.getText().isEmpty() || this.textPort.getText().isEmpty() || this.textUsername.getText().isEmpty());
		}

	}

}
