package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;

public class DragIcon extends AnchorPane {

	@FXML
	AnchorPane root_pane;

	private DragIconType mType = null;

	public DragIcon() {

		FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("DragIcon.fxml"));

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
	}

	public void relocateToPoint(Point2D p) {
		Point2D localCoords = this.getParent().sceneToLocal(p);

		this.relocate((int) (localCoords.getX() - this.getBoundsInLocal().getWidth() / 2),
				(int) (localCoords.getY() - this.getBoundsInLocal().getHeight() / 2));
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
}
