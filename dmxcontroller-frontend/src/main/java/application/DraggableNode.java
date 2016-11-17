package application;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

public class DraggableNode extends AnchorPane {

	@FXML
	AnchorPane root_pane;

	private EventHandler<DragEvent> mContextDragOver;
	private EventHandler<DragEvent> mContextDragDropped;

	private DragIconType mType = null;

	private Point2D mDragOffset = new Point2D(0.0, 0.0);

	@FXML
	private Label title_bar;
	
	@FXML
	private Label close_button;

	private final DraggableNode self;

	public DraggableNode() {

		FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("DraggableNode.fxml"));

		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		this.self = this;

		try {
			fxmlLoader.load();

		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@FXML
	private void initialize() {
		this.buildNodeDragHandlers();
	}

	public void relocateToPoint(Point2D p) {

		// relocates the object to a point that has been converted to
		// scene coordinates
		Point2D localCoords = this.getParent().sceneToLocal(p);

		this.relocate((int) (localCoords.getX() - this.mDragOffset.getX()), (int) (localCoords.getY() - this.mDragOffset.getY()));
	}

	public DragIconType getType() {
		return this.mType;
	}

	public void setType(DragIconType type) {

		this.mType = type;

		this.getStyleClass().clear();
		this.getStyleClass().add("dragicon");

		switch (this.mType) {

		case blue:
			this.getStyleClass().add("icon-blue");
			break;

		case red:
			this.getStyleClass().add("icon-red");
			break;

		case green:
			this.getStyleClass().add("icon-green");
			break;

		case grey:
			this.getStyleClass().add("icon-grey");
			break;

		case purple:
			this.getStyleClass().add("icon-purple");
			break;

		case yellow:
			this.getStyleClass().add("icon-yellow");
			break;

		case black:
			this.getStyleClass().add("icon-black");
			break;

		default:
			break;
		}
	}

	public void buildNodeDragHandlers() {

		this.mContextDragOver = new EventHandler<DragEvent>() {

			// dragover to handle node dragging in the right pane view
			@Override
			public void handle(DragEvent event) {

				event.acceptTransferModes(TransferMode.ANY);
				DraggableNode.this.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

				event.consume();
			}
		};

		// dragdrop for node dragging
		this.mContextDragDropped = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				DraggableNode.this.getParent().setOnDragOver(null);
				DraggableNode.this.getParent().setOnDragDropped(null);

				event.setDropCompleted(true);

				event.consume();
			}
		};
		// close button click
		this.close_button.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				AnchorPane parent = (AnchorPane) DraggableNode.this.self.getParent();
				parent.getChildren().remove(DraggableNode.this.self);
			}

		});

		// drag detection for node dragging
		this.title_bar.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				DraggableNode.this.getParent().setOnDragOver(null);
				DraggableNode.this.getParent().setOnDragDropped(null);

				DraggableNode.this.getParent().setOnDragOver(DraggableNode.this.mContextDragOver);
				DraggableNode.this.getParent().setOnDragDropped(DraggableNode.this.mContextDragDropped);

				// begin drag ops
				DraggableNode.this.mDragOffset = new Point2D(event.getX(), event.getY());

				DraggableNode.this.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

				ClipboardContent content = new ClipboardContent();
				DragContainer container = new DragContainer();

				container.addData("type", DraggableNode.this.mType.toString());
				content.put(DragContainer.AddNode, container);

				DraggableNode.this.startDragAndDrop(TransferMode.ANY).setContent(content);

				event.consume();
			}

		});
	}
}
