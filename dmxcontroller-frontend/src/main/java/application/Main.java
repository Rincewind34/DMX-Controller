package application;
	
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	
//	@Override
//	public void start(Stage primaryStage) {
//		BorderPane root = new BorderPane();
//		
//		try {
//			Scene scene = new Scene(root,640,480);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//			primaryStage.setScene(scene);
//			primaryStage.show();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		root.setCenter(new RootLayout());
//	}
//	
//	public static void main(String[] args) {
//		launch(args);
//	}
	
    private static final String PREFIX =
            "http://icons.iconarchive.com/icons/jozef89/origami-birds/72/bird";

    private static final String SUFFIX =
            "-icon.png";

    private static final ObservableList<String> birds = FXCollections.observableArrayList(
            "-black",
            "-blue",
            "-red",
            "-red-2",
            "-yellow",
            "s-green",
            "s-green-2"
    );

    private static final ObservableList<Image> birdImages = FXCollections.observableArrayList();

    @Override
	public void start(Stage stage) throws Exception {
    	birds.forEach(bird -> birdImages.add(new Image(PREFIX + bird + SUFFIX)));
        
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToHeight(true);
        HBox hbox = new HBox();
        hbox.setSpacing(10.0);
        hbox.setAlignment(Pos.CENTER);
        
        Main.birdImages.forEach((bird) -> {
        	ImageView image = new ImageView(bird);
        	
        	StackPane pane = new StackPane();
        	pane.setMinHeight(100.0);
        	pane.setMaxHeight(100.0);
        	pane.setMinWidth(100.0);
        	pane.setMaxWidth(100.0);
        	pane.getChildren().add(image);
        	StackPane.setAlignment(image, Pos.CENTER);
        	pane.setStyle("-fx-background-color: #DDD");
        	
        	Main.setup(pane, bird, hbox);
        	
        	hbox.getChildren().add(pane);
        });
        
        scroll.setContent(hbox);
        
        ListView<String> birdList = new ListView<>(birds);
        birdList.setCellFactory(param -> new BirdCell());
        birdList.setPrefWidth(180);

        VBox layout = new VBox(birdList);
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(scroll));
        stage.show();
    }

    public static void main(String[] args) {
        launch(Main.class);
    }

    private class BirdCell extends ListCell<String> {
        private final ImageView imageView = new ImageView();

        public BirdCell() {
//            ListCell<String> thisCell = this;

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setAlignment(Pos.CENTER);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                imageView.setImage(
                    birdImages.get(
                        getListView().getItems().indexOf(item)
                    )
                );
                setGraphic(imageView);
            }
        }
    }
    
    private static DataFormat format = new DataFormat("mainid");
    
    private static Map<Integer, Node> registry = new HashMap<>();
    
    private static void setup(Node node, Image image, Pane root) {
    	int index = Main.nextIndex();
    	Main.registry.put(index, node);
    	
    	node.setOnDragDetected(event -> {
    		Dragboard dragboard = node.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(Main.format, index);
            dragboard.setDragView(image);
            dragboard.setContent(content);

            event.consume();
        });

    	node.setOnDragOver(event -> {
            if (event.getGestureSource() != node &&
                   event.getDragboard().hasContent(Main.format)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

    	node.setOnDragEntered(event -> {
            if (event.getGestureSource() != node &&
                    event.getDragboard().hasContent(Main.format)) {
            	node.setOpacity(0.3);
            }
        });

    	node.setOnDragExited(event -> {
            if (event.getGestureSource() != node &&
                    event.getDragboard().hasContent(Main.format)) {
            	node.setOpacity(1);
            }
        });
    	
    	node.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            
            if (db.hasContent(Main.format)) {
                ObservableList<Node> items = root.getChildren();
                int draggedIdx = items.indexOf(node);
                
                Node draged = Main.registry.get(db.getContent(Main.format));
                
                items.remove(draged);
                items.add(draggedIdx, draged);
                success = true;
            }
            
            event.setDropCompleted(success);
            event.consume();
        });

        node.setOnDragDone(DragEvent::consume);
        
        root.setOnDragOver((event) -> {
        	 if (event.getGestureSource() != node &&
                     event.getDragboard().hasContent(Main.format)) {
                  event.acceptTransferModes(TransferMode.MOVE);
              }
        	 
        	 event.consume();
        });
        
        root.setOnDragDropped((event) -> {
        	Dragboard db = event.getDragboard();
            boolean success = false;
            
            if (db.hasContent(Main.format)) {
                ObservableList<Node> items = root.getChildren();
                Node draged = Main.registry.get(db.getContent(Main.format));
                
                items.remove(draged);
                items.add(draged);
            }
            
            event.setDropCompleted(success);
            event.consume();
        });
    }
    
    private static int nextIndex() {
    	int result = 0;
    	
    	while (Main.registry.containsKey(result)) {
    		result = result + 1;
    	}
    	
    	return result;
    }
    
}
