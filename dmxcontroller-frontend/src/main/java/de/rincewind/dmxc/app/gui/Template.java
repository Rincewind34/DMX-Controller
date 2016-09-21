package de.rincewind.dmxc.app.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class Template {
	
	private ScrollPane rootPane;
	
	public Template() {
		this.rootPane = new ScrollPane();
		this.rootPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
		this.rootPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.setSpacing(20.0);
		hbox.getChildren().add(new RGBFader());
		hbox.getChildren().add(new Fader());
		hbox.getChildren().add(new RGBWFader());
		hbox.getChildren().add(new NumberPad());
		hbox.getChildren().add(new HardFader());
		hbox.getChildren().add(new SceneTool());
		hbox.setMinHeight(Region.USE_COMPUTED_SIZE);
		hbox.setMaxHeight(Integer.MAX_VALUE);
		hbox.setMinWidth(Region.USE_COMPUTED_SIZE);
		hbox.setMaxWidth(Integer.MAX_VALUE);
		hbox.setPadding(new Insets(20, 20, 20, 20));
		
		this.rootPane.setContent(hbox);
		this.rootPane.setFitToHeight(true);
		this.rootPane.setMinHeight(Region.USE_COMPUTED_SIZE);
	}
	
	public ScrollPane getRootPane() {
		return rootPane;
	}
	
}
