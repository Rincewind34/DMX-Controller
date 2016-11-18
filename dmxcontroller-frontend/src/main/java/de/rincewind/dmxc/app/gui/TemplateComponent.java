package de.rincewind.dmxc.app.gui;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

import de.rincewind.dmxc.app.gui.util.Color;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public abstract class TemplateComponent extends VBox {
	
	public static final Color BORDER_COLOR = new Color(0x55, 0x55, 0x55);
	
	protected static void setBackgroundColor(Pane pane, Color color) {
		pane.setStyle("-fx-background-color: " + TemplateComponent.BORDER_COLOR.toCSS() + ", " + color.toCSS() + ";");
	}
	
	private Template root;
	
	private Label labelCaption;
	private Pane currentContent;
	
	private List<TextField> captionControler;
	
	public TemplateComponent() {
		this.captionControler = new ArrayList<>();
		
		this.labelCaption = new Label("");
		this.labelCaption.getStyleClass().add(".basic-element");
		this.labelCaption.getStyleClass().add(".basic-label");
		this.labelCaption.getStyleClass().add(FileLoader.PATH_CSS + "basics.css");
		
		this.setSpacing(5.0D);
		this.setAlignment(Pos.CENTER);
		this.setPrefHeight(Region.USE_COMPUTED_SIZE);
		this.setMaxHeight(Double.MAX_VALUE);
	}
	
	public void update() {
		
	}
	
	public void setContent(TemplateContent content) {
		if (this.currentContent != null) {
			this.removeContent();
			this.removeCaption();
		}
		
		Pane pane = null;
		
		if (content == TemplateContent.CONTROLER) {
			pane = this.getToolPane();
		} else if (content == TemplateContent.CONFIG) {
			pane = this.getConfigPane();
		} else if (content == TemplateContent.DRAG_DROP) {
			
		}
		
		if (pane != null) {
			this.currentContent = pane;
			VBox.setVgrow(this.currentContent, Priority.ALWAYS);
			this.addCaption();
			this.addContent();
		}
	}
	
	public void setCaption(String caption) {
		this.labelCaption.setText(caption);
		
		for (TextField field : this.captionControler) {
			field.setText(caption);
		}
	}
	
	public Template getRoot() {
		return this.root;
	}
	
	protected abstract JsonElement serializeSimplified();
	
	protected abstract Pane getToolPane();
	
	protected abstract Pane getConfigPane();
	
	protected void setRoot(Template template) {
		this.root = template;
	}
	
	protected void bindCaptionField(TextField textCaption) {
		textCaption.setText(this.labelCaption.getText());
		textCaption.textProperty().addListener((observeable, oldValue, newValue) -> {
			this.setCaption(newValue);
		});
		
		this.captionControler.add(textCaption);
	}
	
	private void addCaption() {
		this.getChildren().add(this.labelCaption);
	}
	
	private void addContent() {
		this.getChildren().add(this.currentContent);
	}
	
	private void removeCaption() {
		this.getChildren().remove(this.labelCaption);
	}
	
	private void removeContent() {
		this.getChildren().remove(this.currentContent);
	}
	
}