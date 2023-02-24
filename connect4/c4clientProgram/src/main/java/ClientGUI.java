import java.util.HashMap;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.Pair;

public class ClientGUI extends Application{

	
	//TextField s1,s2,s3,s4, c1;
	Label l1;
	Button quit, playAgain;
	Button clientChoice,b1;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	HBox clientBox;
	Scene startScene;
	BorderPane startPane;
	//Server serverConnection;
	Client clientConnection;
	TextField ipInput, portInput;
	
	ListView<String> introInstructions;
	ObservableList<String> instruct;
	
	VBox introBox;
	ListView<String> listItems;
	CFourInfo info = new CFourInfo();
	static GameButton[][] buttons = new GameButton[6][7];
	//static boolean moveMade = false;
	static boolean p1, p2;
	static int buttonMapSize;
	static int playerWinner;
	
	static PauseTransition pause = new PauseTransition(Duration.seconds(3));
	PauseTransition pause2 = new PauseTransition(Duration.seconds(1));
	
	EventHandler<ActionEvent> h1;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Client GUI");
		
		setButtonActions();
		
		ipInput = new TextField();
		ipInput.setPromptText("Input IP address:");
		portInput = new TextField();
		portInput.setPromptText("Input port #:");
		
		this.instruct = FXCollections.observableArrayList("Enter IP and port #. Invalid port "
				+ "will be set to default value of 5555");
		this.introInstructions = new ListView<String>(instruct);
		this.introInstructions.setPrefWidth(400);
		
		this.clientChoice = new Button("Connect to Server");
		/*
		this.clientChoice.setStyle("-fx-pref-width: 300px");
		this.clientChoice.setStyle("-fx-pref-height: 300px");
		*/
		this.clientChoice.setPrefSize(325, 50);
		this.clientChoice.setStyle("-fx-background-color: lightgray;");
		this.clientChoice.setOnAction(e-> {
			primaryStage.setScene(sceneMap.get("client"));
			primaryStage.setTitle("Connect Four");
			int port;
			String ip = ipInput.getText();
			try { 
				port = Integer.parseInt(portInput.getText());
				if (port < 0) {
					port = 5555;
				}
			} catch (NumberFormatException exc) {
				port = 5555;
			}
			clientConnection = new Client(data-> {
				Platform.runLater(()->{
					//listItems.getItems().clear();
					listItems.getItems().add(data.toString());
					
				});
			}, ip, port);
							
			clientConnection.start();
		});
		
		this.buttonBox = new HBox(ipInput, portInput);
		this.buttonBox.setSpacing(5);
		this.introBox = new VBox (this.buttonBox, clientChoice);
		introBox.setSpacing(5);
		introBox.setPadding(new Insets(0, 0, 0, 20));
		startPane = new BorderPane();
		startPane.setPadding(new Insets(10));
		startPane.setCenter(introBox);
		startPane.setLeft(introInstructions);
		startPane.setStyle("-fx-background-color: lightblue;");
		
		startScene = new Scene(startPane, 780, 500);
		
		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("client",  createClientGui());
		sceneMap.put("results", createResultsScene());
		
		pause.setOnFinished(e-> { 
			primaryStage.setScene(sceneMap.get("results"));
			if (playerWinner == 1) {
				l1.setText("Player 1 Wins!");
			} else if (playerWinner == 2) {
				l1.setText("Player 2 Wins!");
			} else {
				l1.setText("The game ended in a tie!");
			}
		});
		
		pause2.setOnFinished(e-> { 
			primaryStage.setScene(sceneMap.get("client"));
		});
		
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
	
	public Scene createClientGui() {
		listItems = new ListView<String>();
		grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);
		//grid.setStyle("-fx-background-color: lightblue;");
		addGrid(grid);
		clientBox = new HBox(grid, listItems);
		clientBox.setPadding(new Insets(10));
		clientBox.setSpacing(10);
		clientBox.setStyle("-fx-background-color: lightblue;");
		return new Scene(clientBox, 800, 800);
	}
	
	
	public void addGrid(GridPane grid) {
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 7; j++) {
				GameButton b1 = new GameButton(i, j);
				b1.setMinHeight(75);
				b1.setMinWidth(75);
				//b1.setPadding(new Insets(10,10,10,10));
				b1.setStyle("-fx-border-size: 20;" + "-fx-border-color: white;" + 
						"-fx-background-color: lightgrey;");
				b1.setDisable(true);
				b1.setOnAction(h1);
				buttons[i][j] = b1;
				grid.add(b1, j, i);
			}
		}
	}
	
	public void setButtonActions() {
		h1 = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				GameButton b1 = (GameButton) e.getSource();
				CFourInfo info  = new CFourInfo();
				info.lastMove = "(" + b1.row + "," + b1.col + ")";
				b1.setDisable(true);
				if (p1) {
					b1.setStyle("-fx-background-color: red;");
					b1.playerClicked = 1;
				} else if (p2) {
					b1.setStyle("-fx-background-color: yellow;");
					b1.playerClicked = 2;
				}
				if (b1.row > 0) {
					buttons[b1.row-1][b1.col].setDisable(false); 
				}
				// check to see if there is a win or tie
				int[][] board = new int[6][7];
				for (int i = 0; i < 6; i++) {
					for (int j = 0; j < 7; j++) {
						board[i][j] = buttons[i][j].playerClicked;
					}
				}
				int[] green = new int[8];
				
				int checkWinner = checkWinner(board, green);
				// check if no win and map size 42
				if (checkWinner == 1 || checkWinner == 2) {
					info.winner = checkWinner;
					playerWinner = checkWinner;
					// turn buttons green
					turnGreen(green);
				} else if (checkWinner == -1 && buttonMapSize == 42) {
					info.winner = 0;
					playerWinner = 0;
				}
				for (int i = 0; i < 6; i++) {
					for (int j = 0; j < 7; j++) {
						if (buttons[i][j].playerClicked != 0 || !buttons[i][j].isDisable()) {
							Pair<Integer, Integer> coord = new Pair<>(i,j);
							info.buttonMap.put(coord, buttons[i][j].playerClicked);
						}
					}
				}
				clientConnection.send(info);
			}
		};
	}
	
	public Scene createResultsScene() {
		l1 = new Label();
		l1.setPadding(new Insets(0,0,0,50));
		l1.setStyle("-fx-font-family: courier;" + "-fx-font-size: 25;");
		quit = new Button("Quit");
		quit.setPrefSize(150, 25);
		quit.setOnAction(e-> {
			System.exit(0);
		});
		playAgain = new Button("Play Again");
		playAgain.setPrefSize(150, 25);
		playAgain.setOnAction(e->{
			CFourInfo info = new CFourInfo();
			info.newGame = true;
			clientConnection.send(info);
			listItems.getItems().clear();
			resetGrid();
			pause2.play();
		});
		HBox box = new HBox (quit, playAgain);
		box.setSpacing(10);
		VBox box2 = new VBox(l1, box);
		box2.setSpacing(50);
		box2.setPadding(new Insets(100));
		BorderPane p1 = new BorderPane();
		p1.setCenter(box2);
		p1.setStyle("-fx-font-family: courier;" + "-fx-font-size: 18;" 
		+ "-fx-background-color: lightblue;");
		Scene results = new Scene(p1, 500, 500);
		return results;
	}
	
	public void resetGrid() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				buttons[i][j].setDisable(true);
				buttons[i][j].playerClicked = 0;
				buttons[i][j].setStyle("-fx-border-size: 20;" + "-fx-border-color: white;" + 
						"-fx-background-color: lightgrey;");
				buttons[i][j].setOnAction(h1);
			}
		}
		//sceneMap.put("client", createClientGui());
	}
	
	public static int checkWinner(int[][] board, int[] green) {
		/*
		int[][] board = new int[6][7];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				board[i][j] = buttons[i][j].playerClicked;
			}
		}
		*/
		//check horizontal
		for(int row = 0; row<board.length; row++){
			for (int col = 0;col < board[0].length - 3;col++){
				if (board[row][col] == 1 && board[row][col+1] == 1 &&
					board[row][col+2] == 1 && board[row][col+3] == 1){
					/*
					buttons[row][col].playerClicked = 3;
					buttons[row][col+1].playerClicked = 3;
					buttons[row][col+2].playerClicked = 3;
					buttons[row][col+3].playerClicked = 3;
					*/
					green[0] = row;
					green[1] = col;
					green[2] = row; 
					green[3] = col+1;
					green[4] = row;
					green[5] = col+2;
					green[6] = row;
					green[7] = col+3;
					return 1;
					
				} else if (board[row][col] == 2 && board[row][col+1] == 2 &&
					board[row][col+2] == 2 && board[row][col+3] == 2){
					/*
					buttons[row][col].playerClicked = 3;
					buttons[row][col+1].playerClicked = 3;
					buttons[row][col+2].playerClicked = 3;
					buttons[row][col+3].playerClicked = 3;
					*/
					green[0] = row;
					green[1] = col;
					green[2] = row; 
					green[3] = col+1;
					green[4] = row;
					green[5] = col+2;
					green[6] = row;
					green[7] = col+3;
					return 2;
				} 
			}			
		}
		//check vertical
		for(int row = 0; row < board.length - 3; row++){
			for(int col = 0; col < board[0].length; col++){
				if (board[row][col] == 1 && board[row+1][col] == 1 &&
					board[row+2][col] == 1 && board[row+3][col] == 1){
					/*
					buttons[row][col].playerClicked = 3;
					buttons[row+1][col].playerClicked = 3;
					buttons[row+2][col].playerClicked = 3;
					buttons[row+3][col].playerClicked = 3;
					*/
					green[0] = row;
					green[1] = col;
					green[2] = row+1; 
					green[3] = col;
					green[4] = row+2;
					green[5] = col;
					green[6] = row+3;
					green[7] = col;
					return 1;
				} else if (board[row][col] == 2 && board[row+1][col] == 2 &&
					board[row+2][col] == 2 && board[row+3][col] == 2){
					/*
					buttons[row][col].playerClicked = 3;
					buttons[row+1][col].playerClicked = 3;
					buttons[row+2][col].playerClicked = 3;
					buttons[row+3][col].playerClicked = 3;
					*/
					green[0] = row;
					green[1] = col;
					green[2] = row+1; 
					green[3] = col;
					green[4] = row+2;
					green[5] = col;
					green[6] = row+3;
					green[7] = col;
					return 2;
				}
			}
		}
		
		//check upward diagonal
		for(int row = 3; row < board.length; row++){
			for(int col = 0; col < board[0].length - 3; col++){
				if (board[row][col] == 1 && board[row-1][col+1] == 1 &&
					board[row-2][col+2] == 1 && board[row-3][col+3] == 1){
					/*
					buttons[row][col].playerClicked = 3;
					buttons[row-1][col+1].playerClicked = 3;
					buttons[row-2][col+2].playerClicked = 3;
					buttons[row-3][col+3].playerClicked = 3;
					*/
					green[0] = row;
					green[1] = col;
					green[2] = row-1; 
					green[3] = col+1;
					green[4] = row-2;
					green[5] = col+2;
					green[6] = row-3;
					green[7] = col+3;
					return 1;
				} else if (board[row][col] == 2 && board[row-1][col+1] == 2 &&
					board[row-2][col+2] == 2 && board[row-3][col+3] == 2){
					/*
					buttons[row][col].playerClicked = 3;
					buttons[row-1][col+1].playerClicked = 3;
					buttons[row-2][col+2].playerClicked = 3;
					buttons[row-3][col+3].playerClicked = 3;
					*/
					green[0] = row;
					green[1] = col;
					green[2] = row-1; 
					green[3] = col+1;
					green[4] = row-2;
					green[5] = col+2;
					green[6] = row-3;
					green[7] = col+3;
					return 2;
				}
			}
		}
		//check downward diagonal
		for(int row = 0; row < board.length - 3; row++){
			for(int col = 0; col < board[0].length - 3; col++){
				if (board[row][col] == 1 && board[row+1][col+1] == 1 &&
					board[row+2][col+2] == 1 && board[row+3][col+3] == 1){
					/*
					buttons[row][col].playerClicked = 3;
					buttons[row+1][col+1].playerClicked = 3;
					buttons[row+2][col+2].playerClicked = 3;
					buttons[row+3][col+3].playerClicked = 3;
					*/
					green[0] = row;
					green[1] = col;
					green[2] = row+1; 
					green[3] = col+1;
					green[4] = row+2;
					green[5] = col+2;
					green[6] = row+3;
					green[7] = col+3;
					return 1;
				} else if (board[row][col] == 2 && board[row+1][col+1] == 2 &&
					board[row+2][col+2] == 2 && board[row+3][col+3] == 2){
					/*
					buttons[row][col].playerClicked = 3;
					buttons[row+1][col+1].playerClicked = 3;
					buttons[row+2][col+2].playerClicked = 3;
					buttons[row+3][col+3].playerClicked = 3;
					*/
					green[0] = row;
					green[1] = col;
					green[2] = row+1; 
					green[3] = col+1;
					green[4] = row+2;
					green[5] = col+2;
					green[6] = row+3;
					green[7] = col+3;
					return 2;
				}
			}
		}
		return -1; // no winner
	}
	
	public void turnGreen(int[] green) {
		buttons[green[0]][green[1]].playerClicked = 3;
		buttons[green[2]][green[3]].playerClicked = 3;
		buttons[green[4]][green[5]].playerClicked = 3;
		buttons[green[6]][green[7]].playerClicked = 3;
	}

}