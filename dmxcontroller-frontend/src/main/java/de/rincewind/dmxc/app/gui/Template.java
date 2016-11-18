package de.rincewind.dmxc.app.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.rincewind.dmxc.app.api.Fadeable;
import de.rincewind.dmxc.app.api.Submaster;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import de.rincewind.dmxc.app.gui.util.NumberPadInterpreter;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Template extends VBox {

	@FXML
	private ScrollPane contentScrollPane;

	@FXML
	private ScrollPane dragScrollPane;

	@FXML
	private TabPane selectorTabPane;

	@FXML
	private HBox root;

	@FXML
	private HBox content;

	@FXML
	private Button button;
	
	@FXML
	private Button buttonRefreshSubmasters;
	
	@FXML
	private ListView<Submaster> listSubmasters;

	// ==== Number Pad ==== //

	@FXML
	private Button button0;

	@FXML
	private Button button1;

	@FXML
	private Button button2;

	@FXML
	private Button button3;

	@FXML
	private Button button4;

	@FXML
	private Button button5;

	@FXML
	private Button button6;

	@FXML
	private Button button7;

	@FXML
	private Button button8;

	@FXML
	private Button button9;

	@FXML
	private Button buttonPlus;

	@FXML
	private Button buttonThru;

	@FXML
	private Button buttonClear;

	@FXML
	private Button buttonBack;

	@FXML
	private Label display;

	// ==== Number Pad ==== //
	
	private NumberPadInterpreter numberPad;

	private TemplateContent displayContent;

	public Template() {
		FileLoader.loadFXML(this, "template.fxml", "basics.css", "template.css", "numberpad.css");

		this.numberPad = new NumberPadInterpreter(this.button0, this.button1, this.button2, this.button3, this.button4, this.button5, this.button6,
				this.button7, this.button8, this.button9, this.buttonPlus, this.buttonThru, this.buttonClear, this.buttonBack, this.display);

		this.contentScrollPane.setFitToHeight(true);
		this.displayContent = TemplateContent.CONTROLER;

		this.button.setOnAction((event) -> {
			if (this.root.getChildren().contains(this.selectorTabPane)) {
				this.root.getChildren().remove(this.selectorTabPane);
				this.button.setText("Configuration");
				this.setContent(TemplateContent.CONTROLER);
			} else {
				this.root.getChildren().add(0, this.selectorTabPane);
				this.button.setText("Home");
				this.setContent(TemplateContent.CONFIG);
				this.fillSubmasters();
			}
		});
		
		this.buttonRefreshSubmasters.setOnAction((event) -> {
			this.fillSubmasters();
		});

		this.root.getChildren().remove(this.selectorTabPane);
		this.root.getChildren().remove(this.dragScrollPane);
	}
	
	public Template(JsonObject object) {
		this();
		
		JsonArray array = object.get("template").getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
			this.addComponent(TemplateComponent.deserialize(array.get(i).getAsJsonObject()));
		}
		
		this.setContent(TemplateContent.valueOf(object.get("content").getAsString()));
	}
	
	public void setContent(TemplateContent displayContent) {
		this.displayContent = displayContent;
		
		for (Node node : this.content.getChildren()) {
			if (node instanceof TemplateComponent) {
				((TemplateComponent) node).setContent(this.displayContent);
				((TemplateComponent) node).update();
			}
		}
	}

	public void addComponent(TemplateComponent component) {
		this.content.getChildren().add(component);
		component.setContent(this.displayContent);
		component.setRoot(this);
		component.update();
	}

	public void removeComponent(TemplateComponent component) {
		this.content.getChildren().remove(component);
		component.setRoot(null);
	}
	
	public Fadeable getCurrentSelection() {
		if (this.displayContent != TemplateContent.CONFIG) {
			return null;
		} else {
			Tab selected = this.selectorTabPane.getSelectionModel().getSelectedItem();
			
			if (((Pane) selected.getContent()).getChildren().get(0) instanceof ListView<?>) {
				return this.listSubmasters.getSelectionModel().getSelectedItem();
			} else {
				return this.numberPad.getSelection();
			}
		}
	}
	
	public JsonObject serialize() {
		JsonObject object = new JsonObject();
		JsonArray array = new JsonArray();
		
		for (Node node : this.getChildren()) {
			if (node instanceof TemplateComponent) {
				array.add(((TemplateComponent) node).serialize());
			}
		}
		
		object.addProperty("content", this.displayContent.name());
		object.add("template", array);
		return object;
	}
	
	private void fillSubmasters() {
		this.listSubmasters.getItems().clear();
		
		for (Submaster submaster : Submaster.getAll()) {
			this.listSubmasters.getItems().add(submaster);
		}
	}
	
}
