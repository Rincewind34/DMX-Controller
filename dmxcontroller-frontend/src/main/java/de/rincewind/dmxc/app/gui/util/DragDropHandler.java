package de.rincewind.dmxc.app.gui.util;

import java.util.function.Supplier;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class DragDropHandler {
	
	private int index;
	private Image image;
	private Supplier<Node> supplier;
	
	private Pane root;
	private Pane creator;
	
	private Node current;
	
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
					this.registerInfiniteNode(this.supplier, this.image, this.index);
				}
				
				this.root.getChildren().add(this.current);
				this.registerNode(this.current, this.image);
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
	
	public void registerNode(Node node, Image image) {
		this.setupNode(node, image);
		
		node.setOnDragDetected((event) -> {
			if (!this.running) {
				return;
			}
			
			this.infiniteNode = false;
			this.current = node;
			this.image = image;
			
			ClipboardContent content = new ClipboardContent();
			content.putString("test");
			
			Dragboard board = node.startDragAndDrop(TransferMode.ANY);
			board.setDragView(image);
			board.setContent(content);
			
			event.consume();
		});
		
		node.setOnDragEntered((event) -> {
			if (event.getGestureSource() != node && this.current != null) {
				node.setOpacity(0.6D);
			}
		});
		
		node.setOnDragExited((event) -> {
			if (event.getGestureSource() != node && this.current != null) {
				node.setOpacity(1.0D);
			}
		});
		
		node.setOnDragDropped((event) -> {
			boolean success = false;
			
			if (this.current != null) {
				node.setOpacity(1.0D);
				int targetIndex = this.root.getChildren().indexOf(node);
				
				if (!this.infiniteNode) {
					this.root.getChildren().remove(this.current);
				} else {
					this.registerInfiniteNode(this.supplier, this.image, this.index);
				}
					
				this.root.getChildren().add(targetIndex, this.current);
				this.registerNode(this.current, this.image);
				this.current = null;
				
				success = true;
			}
			
			event.setDropCompleted(success);
			event.consume();
		});
	}
	
	public void registerInfiniteNode(Supplier<Node> supplier, Image image, int index) {
		Node node = supplier.get();
		
		this.creator.getChildren().add(index, node);
		this.setupNode(node, image);
		
		node.setOnDragDetected((event) -> {
			if (!this.running) {
				return;
			}
			
			this.infiniteNode = true;
			this.current = node;
			this.image = image;
			this.supplier = supplier;
			this.index = index;
			
			ClipboardContent content = new ClipboardContent();
			content.putString("test");
			
			Dragboard board = node.startDragAndDrop(TransferMode.ANY);
			board.setDragView(image);
			board.setContent(content);
			
			event.consume();
		});
		
		node.setOnDragDropped((event) -> {
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
	
	private void setupNode(Node node, Image image) {
		node.setOnDragOver((event) -> {
			if (event.getGestureSource() != node && this.current != null) {
				event.acceptTransferModes(TransferMode.ANY);
			}
			
			event.consume();
		});
		
		node.setOnDragDone((event) -> {
			event.consume();
		});
//		
//		Tooltip tooltip = new Tooltip("Test");
//		Tooltip.install(node, tooltip);
	}
	
	public void start() {
		this.running = true;
	}
	
	public void stop() {
		this.running = false;
	}
	
}
