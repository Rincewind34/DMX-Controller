package de.rincewind.dmxc.app.gui.util;

import de.rincewind.dmxc.app.gui.TemplateComponent;
import de.rincewind.dmxc.app.gui.TemplateContent;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class DragDropHandler {
	
	private int index;
	
	private Pane root;
	private Pane creator;
	
	private TemplateComponent current;
	
	private boolean running;
	private boolean infiniteNode;
	
	public DragDropHandler(Pane root, Pane creator) {
		this.root = root;
		this.creator = creator;
		
		this.root.setOnDragOver((event) -> {
			event.acceptTransferModes(TransferMode.ANY);
			event.consume();
		});
		
		this.root.setOnDragDropped((event) -> {
			boolean success = false;
			
			if (this.current != null) {
				if (!this.infiniteNode) {
					this.root.getChildren().remove(this.current);
				} else {
					this.registerInfiniteNode(this.current.newOne(), this.index);
				}
				
				this.root.getChildren().add(this.current);
				this.registerNode(this.current);
				this.current = null;
				success = true;
			}
			
			event.setDropCompleted(success);
			event.consume();
		});
		
		this.creator.setOnDragOver((event) -> {
			event.acceptTransferModes(TransferMode.ANY);
			event.consume();
		});
		
		this.creator.setOnDragDropped((event) -> {
			boolean success = false;
			
			if (this.current != null) {
				if (!this.infiniteNode) {
					this.root.getChildren().remove(this.current);
				}
				
				this.current = null;
				success = true;
			}
			
			event.setDropCompleted(success);
			event.consume();
		});
	}
	
	public void start() {
		this.running = true;
	}
	
	public void stop() {
		this.running = false;
	}
	
	public void registerNode(TemplateComponent component) {
		this.setupNode(component);
		
		component.setOnDragDetected((event) -> {
			if (!this.running) {
				return;
			}
			
			this.infiniteNode = false;
			this.current = component;
			
			ClipboardContent content = new ClipboardContent();
			content.putString("test");
			
			Dragboard board = component.startDragAndDrop(TransferMode.ANY);
			board.setDragView(component.getDragDropImage());
			board.setContent(content);
			
			event.consume();
		});
		
		component.setOnDragEntered((event) -> {
			if (event.getGestureSource() != component && this.current != null) {
				component.setOpacity(0.6D);
			}
		});
		
		component.setOnDragExited((event) -> {
			if (event.getGestureSource() != component && this.current != null) {
				component.setOpacity(1.0D);
			}
		});
		
		component.setOnDragDropped((event) -> {
			boolean success = false;
			
			if (this.current != null) {
				component.setOpacity(1.0D);
				int targetIndex = this.root.getChildren().indexOf(component);
				
				if (!this.infiniteNode) {
					this.root.getChildren().remove(this.current);
				} else {
					this.registerInfiniteNode(this.current.newOne(), this.index);
				}
					
				this.root.getChildren().add(targetIndex, this.current);
				this.registerNode(this.current);
				this.current = null;
				
				success = true;
			}
			
			event.setDropCompleted(success);
			event.consume();
		});
	}
	
	public void registerInfiniteNode(TemplateComponent component) {
		this.registerInfiniteNode(component, this.creator.getChildren().size());
	}
	
	private void registerInfiniteNode(TemplateComponent component, int index) {
		component.setContent(TemplateContent.DRAG_DROP);
		
		this.creator.getChildren().add(index, component);
		this.setupNode(component);
		
		component.setOnDragDetected((event) -> {
			if (!this.running) {
				return;
			}
			
			this.infiniteNode = true;
			this.current = component;
			this.index = index;
			
			ClipboardContent content = new ClipboardContent();
			content.putString(component.getType());
			
			Dragboard board = component.startDragAndDrop(TransferMode.ANY);
			board.setDragView(component.getDragDropImage());
			board.setContent(content);
			
			event.consume();
		});
		
		component.setOnDragDropped((event) -> {
			boolean success = false;
			
			if (this.current != null) {
				if (!this.infiniteNode) {
					this.root.getChildren().remove(this.current);
				}
					
				this.current = null;
				success = true;
			}
			
			event.setDropCompleted(success);
			event.consume();
		});
	}
	
	private void setupNode(Node node) {
		node.setOnDragOver((event) -> {
			if (event.getGestureSource() != node && this.current != null) {
				event.acceptTransferModes(TransferMode.ANY);
			}
			
			event.consume();
		});
		
		node.setOnDragDone((event) -> {
			event.consume();
		});
	}
	
}
