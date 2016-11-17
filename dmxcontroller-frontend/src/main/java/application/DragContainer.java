package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.input.DataFormat;
import javafx.util.Pair;

public class DragContainer implements Serializable {

	private static final long serialVersionUID = -1890998765646621338L;

	public static final DataFormat AddNode = new DataFormat("application.DragIcon.add");

	public static final DataFormat DragNode = new DataFormat("application.DraggableNode.drag");

	private final List<Pair<String, Object>> mDataPairs = new ArrayList<>();

	public void addData(String key, Object value) {
		this.mDataPairs.add(new Pair<>(key, value));
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String key) {
		for (Pair<String, Object> data : this.mDataPairs) {
			if (data.getKey().equals(key)) {
				return (T) data.getValue();
			}
		}

		return null;
	}

	public List<Pair<String, Object>> getData() {
		return this.mDataPairs;
	}
	
}
