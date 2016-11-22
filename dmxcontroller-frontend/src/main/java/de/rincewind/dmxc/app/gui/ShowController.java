package de.rincewind.dmxc.app.gui;

import java.io.InputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import de.rincewind.dmxc.app.api.Show;
import de.rincewind.dmxc.app.gui.util.Color;
import de.rincewind.dmxc.app.gui.util.FaderBase;
import de.rincewind.dmxc.app.gui.util.FileLoader;
import de.rincewind.dmxc.common.packets.outgoing.PacketPlayOutShow.SceneState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class ShowController extends TemplateComponent {
	
	private ToolController toolPane;
	private ConfigDefaultController configPane;
	
	public ShowController() {
		this.setCaption("Show-Controller");
		
		this.toolPane = new ToolController();
		FileLoader.loadFXML(this.toolPane, "show-controller.fxml");
		this.toolPane.init();
		
		this.configPane = TemplateComponent.loadConfigController(this);
		
		TemplateComponent.setBackgroundColor(this.toolPane, new Color(0xF2, 0xD0, 0xFF));
	}
	
	@Override
	public String getType() {
		return "showcontroller";
	}
	
	@Override
	public TemplateComponent newOne() {
		return new ShowController();
	}
	
	@Override
	public void update() {
		this.toolPane.faderBase.updateSlider();
	}
	
	public void setCurrentScene(int index) {
		Platform.runLater(() -> {
			this.toolPane.setCurrent(index);
		});
	}
	
	public void setSceneState(SceneState state) {
		Platform.runLater(() -> {
			this.toolPane.labelState.setText(state.name());
			this.toolPane.updateSwitchButtons();
		});
	}
	
	public void setActivated(boolean value) {
		Platform.runLater(() -> {
			this.toolPane.buttonActivate.setSelected(value);
			this.toolPane.updateDisable();
		});
	}
	
	public void addScene(String name) {
		Platform.runLater(() -> {
			this.toolPane.listScenes.getItems().add(name);
		});
	}
	
	public void addScene(String name, int index) {
		Platform.runLater(() -> {
			this.toolPane.listScenes.getItems().add(index, name);
		});
	}
	
	public void removeScene(int index) {
		Platform.runLater(() -> {
			this.toolPane.listScenes.getItems().remove(index);
		});
	}
	
	@Override
	protected String getTooltip() {
		return "Show Controller\n\nThis tool controlls the hole show\nprogrammed on the server.";
	}

	@Override
	protected JsonElement serializeSimplified() {
		return JsonNull.INSTANCE;
	}

	@Override
	protected Pane getToolPane() {
		return this.toolPane;
	}

	@Override
	protected Pane getConfigPane() {
		return this.configPane;
	}

	@Override
	protected InputStream getDragDropImageStream() {
		return FileLoader.getImageStream("stage");
	}
	
	@Override
	protected void setRoot(Template template) {
		super.setRoot(template);
		
		if (this.getRoot() == null) {
			Show.instance().unregisterController(this);
		} else {
			Show.instance().registerController(this);
		}
	}
	
	
	private static class ToolController extends HBox {
		
		@FXML
		private Slider fader;
		
		@FXML
		private ListView<String> listScenes;
		
		@FXML
		private ToggleButton buttonActivate;
		
		@FXML
		private Button buttonFlash;
		
		@FXML
		private Button buttonNext;
		
		@FXML
		private Button buttonPrevious;
		
		@FXML
		private Label labelScene;
		
		@FXML
		private Label labelState;
		
		private FaderBase faderBase;
		
		private void init() {
			this.faderBase = new FaderBase(this.fader, this.buttonFlash, null);
			this.faderBase.setFaderValue((short) 255);
			this.faderBase.setTarget(Show.instance());
			
			this.buttonNext.setOnAction((event) -> {
				Show.sendScene(Show.instance().getCurrentScene() + 1);
			});
			
			this.buttonPrevious.setOnAction((event) -> {
				Show.sendScene(Show.instance().getCurrentScene() - 1);
			});
			
			this.listScenes.setOnMouseClicked((event) -> {
				if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && Show.instance().getState() != SceneState.FADING) {
					Show.sendScene(this.listScenes.getSelectionModel().getSelectedIndex());
				}
			});
			
			this.buttonActivate.selectedProperty().addListener((observeable, oldValue, newValue) -> {
				Show.sendActivated(newValue);
			});
			
			this.updateDisable();
		}
		
		private void updateDisable() {
			boolean value = !Show.instance().isActivated();
			
			if (value) {
				this.listScenes.getSelectionModel().select(-1);
				this.labelScene.setText("");
				this.labelState.setText("");
			} else {
				this.updateLabel(Show.instance().getCurrentScene());
				this.labelState.setText(Show.instance().getState().name());
				this.faderBase.fireChange();
			}
			
			this.updateList();
			
			this.listScenes.setDisable(value);
			this.faderBase.setDisabled(value);
			this.updateSwitchButtons();
		}
		
		private void updateSwitchButtons() {
			boolean value = !Show.instance().isActivated() || Show.instance().getState() == SceneState.FADING;
			
			this.buttonNext.setDisable(value || Show.instance().getCurrentScene() == Show.instance().getScenes().size() - 1);
			this.buttonPrevious.setDisable(value || Show.instance().getCurrentScene() == 0);
		}
		
		private void setCurrent(int index) {
			this.updateLabel(index);
			this.updateList();
		}
		
		private void updateLabel(int index) {
			this.labelScene.setText("(" + index + ") " + Show.instance().getScenes().get(index));
		}
		
		private void updateList() {
			this.listScenes.getItems().clear();
			
			for (int i = 0; i < Show.instance().getScenes().size(); i++) {
				String item = Show.instance().getScenes().get(i);
				
				if (Show.instance().isActivated() && Show.instance().getCurrentScene() == i) {
					item = "(CURRENT) " + item;
				}
				
				this.listScenes.getItems().add(item);
			}
		}
		
	}
	
}
