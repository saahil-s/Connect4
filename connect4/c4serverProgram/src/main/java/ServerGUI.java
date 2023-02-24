import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerGUI extends Application{

	Button serverChoice;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	VBox buttonBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	TextField portInput;
	ListView<String> introInstructions;
	ObservableList<String> instruct;
	int port;
	ListView<String> listItems;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Intro Screen");
		
		this.portInput = new TextField();
		this.portInput.setPromptText("Type in port #:");
		
		this.instruct = FXCollections.observableArrayList("Enter a port # to listen to. "
				+ "Invalid input will be set to default port # of 5555");
		this.introInstructions = new ListView<String>(instruct);
		this.introInstructions.setPrefWidth(420);
		
		this.serverChoice = new Button("Launch Server");
		this.serverChoice.setStyle("-fx-background-color: lightgray");
		/*
		this.serverChoice.setStyle("-fx-pref-width: 300px");
		this.serverChoice.setStyle("-fx-pref-height: 300px");
		*/
		this.serverChoice.setPrefSize(310, 50);
		this.serverChoice.setOnAction(e->{ 
			primaryStage.setScene(sceneMap.get("server"));
			primaryStage.setTitle("Server GUI");
			try { 
				port = Integer.parseInt(portInput.getText());
				if (port < 0) {
					port = 5555;
				}
			} catch (NumberFormatException exc){
				port = 5555;
			}						
			serverConnection = new Server(
				data -> {
					Platform.runLater(()->{
					listItems.getItems().add(data.toString());});
				}, port);							
		});
		
		this.buttonBox = new VBox(/*400,*/ portInput, serverChoice/*, clientChoice*/);
		buttonBox.setPadding(new Insets(0,10,0,20));
		buttonBox.setSpacing(5);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(10));
		startPane.setCenter(buttonBox);
		startPane.setLeft(introInstructions);
		startPane.setStyle("-fx-background-color: blanchedalmond;");
		
		startScene = new Scene(startPane, 780, 500);
		
		listItems = new ListView<String>();
		
		sceneMap = new HashMap<String, Scene>();
		
		sceneMap.put("server",  createServerGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		primaryStage.setScene(startScene);
		primaryStage.show();
	}
	
	public Scene createServerGui() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: blanchedalmond");
		pane.setCenter(listItems);

		return new Scene(pane, 700, 700);
	}
}
