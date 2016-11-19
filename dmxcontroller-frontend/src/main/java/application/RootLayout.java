package application;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class RootLayout extends AnchorPane {

	@FXML
	SplitPane base_pane;
	@FXML
	AnchorPane right_pane;
	@FXML
	VBox left_pane;

	private DragIcon mDragOverIcon = null;

	private EventHandler<DragEvent> mIconDragOverRoot = null;
	private EventHandler<DragEvent> mIconDragDropped = null;
	private EventHandler<DragEvent> mIconDragOverRightPane = null;

	public RootLayout() {

		FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("RootLayout.fxml"));

		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@FXML
	private void initialize() {

		// Add one icon that will be used for the drag-drop process
		// This is added as a child to the root anchorpane so it can be visible
		// on both sides of the split pane.
		this.mDragOverIcon = new DragIcon();

		this.mDragOverIcon.setVisible(false);
		this.mDragOverIcon.setOpacity(0.65);
		this.getChildren().add(this.mDragOverIcon);

		// populate left pane with multiple colored icons for testing
		for (int i = 0; i < 7; i++) {

			DragIcon icn = new DragIcon();

			this.addDragDetection(icn);

			icn.setType(DragIconType.values()[i]);
			this.left_pane.getChildren().add(icn);
		}

		this.buildDragHandlers();
	}

	private void addDragDetection(DragIcon dragIcon) {

		dragIcon.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				System.out.println("ROOT - DRAG DETECT");
				
				// set drag event handlers on their respective objects
				RootLayout.this.base_pane.setOnDragOver(RootLayout.this.mIconDragOverRoot);
				RootLayout.this.right_pane.setOnDragOver(RootLayout.this.mIconDragOverRightPane);
				RootLayout.this.right_pane.setOnDragDropped(RootLayout.this.mIconDragDropped);

				// get a reference to the clicked DragIcon object
				DragIcon icn = (DragIcon) event.getSource();

				// begin drag ops
				RootLayout.this.mDragOverIcon.setType(icn.getType());
				RootLayout.this.mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

				ClipboardContent content = new ClipboardContent();
				DragContainer container = new DragContainer();

				container.addData("type", RootLayout.this.mDragOverIcon.getType().toString());
				content.put(DragContainer.AddNode, container);

				RootLayout.this.mDragOverIcon.startDragAndDrop(TransferMode.ANY).setContent(content);
				RootLayout.this.mDragOverIcon.setVisible(true);
				RootLayout.this.mDragOverIcon.setMouseTransparent(true);
				event.consume();
			}
		});
	}

	private void buildDragHandlers() {

		// drag over transition to move widget form left pane to right pane
		this.mIconDragOverRoot = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				Point2D p = RootLayout.this.right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

				// turn on transfer mode and track in the right-pane's context
				// if (and only if) the mouse cursor falls within the right
				// pane's bounds.
				if (!RootLayout.this.right_pane.boundsInLocalProperty().get().contains(p)) {

					event.acceptTransferModes(TransferMode.ANY);
					RootLayout.this.mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
					return;
				}

				event.consume();
			}
		};

		this.mIconDragOverRightPane = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				event.acceptTransferModes(TransferMode.ANY);
				RootLayout.this.mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
				event.consume();
			}
		};

		this.mIconDragDropped = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

				container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));

				ClipboardContent content = new ClipboardContent();
				content.put(DragContainer.AddNode, container);

				event.getDragboard().setContent(content);
				event.setDropCompleted(true);
			}
		};

		this.setOnDragDone(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				System.out.println("ROOT - DRAGDONE");
				
				RootLayout.this.right_pane.removeEventHandler(DragEvent.DRAG_OVER, RootLayout.this.mIconDragOverRightPane);
				RootLayout.this.right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, RootLayout.this.mIconDragDropped);
				RootLayout.this.base_pane.removeEventHandler(DragEvent.DRAG_OVER, RootLayout.this.mIconDragOverRoot);

				RootLayout.this.mDragOverIcon.setVisible(false);

				DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

				if (container != null) {
					if (container.getValue("scene_coords") != null) {

						DraggableNode node = new DraggableNode();

						node.setType(DragIconType.valueOf(container.getValue("type")));
						RootLayout.this.right_pane.getChildren().add(node);

						Point2D cursorPoint = container.getValue("scene_coords");

						node.relocateToPoint(new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32));
					}
				}

				container = (DragContainer) event.getDragboard().getContent(DragContainer.DragNode);

				if (container != null) {
					if (container.getValue("type") != null) {
						System.out.println("Moved node " + container.getValue("type"));
					}
				}

				event.consume();
			}
		});
	}
}
