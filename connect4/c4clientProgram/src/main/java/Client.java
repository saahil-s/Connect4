import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Map;
import java.util.function.Consumer;

import javafx.util.Pair;



public class Client extends Thread{

	
	Socket socketClient;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	//CFourInfo data = new CFourInfo();
	
	private String ip;
	private int port;
	
	private Consumer<Serializable> callback;
	
	
	Client(Consumer<Serializable> call, String ip, int port){
	
		callback = call;
		this.ip = ip;
		this.port = port;
		
	}
	
	public void run() {
		
		try {
			socketClient= new Socket(ip, port/*"127.0.0.1",5555*/);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}
		
		/*
		try {
			out.writeObject(data);
		} catch(Exception e) {}
		*/	
		
		while(true) {
			try {
				CFourInfo data = (CFourInfo)in.readObject();
				callback.accept(data.gameInfo);
				
				if (data.winner != -1) {
					ClientGUI.playerWinner = data.winner;
					//callback2.accept(data);
					/*
					for (Map.Entry<Pair<Integer, Integer>, Integer> e: data.buttonMap.entrySet()) {
						int i = e.getKey().getKey(); // row
						int j = e.getKey().getValue(); // col
						int k = e.getValue(); // playerClicked
						ClientGUI.buttons[i][j].setDisable(true);
						if (data.winningButtons[0] == i && data.winningButtons[1] == j) {
							System.out.println("entered");
							ClientGUI.buttons[data.winningButtons[0]][data.winningButtons[1]].setStyle
							("-fx-background-color: greenyellow;");
						} else if (data.winningButtons[2] == i && data.winningButtons[3] == j) {
							ClientGUI.buttons[data.winningButtons[2]][data.winningButtons[3]].setStyle
							("-fx-background-color: greenyellow;");
						} else if (data.winningButtons[4] == i && data.winningButtons[4] == j) {
							ClientGUI.buttons[data.winningButtons[4]][data.winningButtons[5]].setStyle
							("-fx-background-color: greenyellow;");
						} else if (data.winningButtons[5] == i && data.winningButtons[6] == j) {
							ClientGUI.buttons[data.winningButtons[5]][data.winningButtons[6]].setStyle
							("-fx-background-color: greenyellow;");
						}
					}
					*/
					ClientGUI.pause.play();
				}
				ClientGUI.buttonMapSize = data.buttonMap.size();
				if (data.p1Turn) {
					ClientGUI.p1 = true;
					ClientGUI.p2 = false;
				} else if (data.p2Turn) { 
					ClientGUI.p1 = false;
					ClientGUI.p2 = true;
				} else {
					ClientGUI.p1 = false;
					ClientGUI.p2 = false;
				}
				
				if (data.have2Players) {
					//System.out.println("enabled");
					//System.out.println(data.play);
					enableBottomRow();
				}
				//System.out.println(data.newGame);
				
				for (Map.Entry<Pair<Integer, Integer>, Integer> e: data.buttonMap.entrySet()) {
					int i = e.getKey().getKey(); // row
					int j = e.getKey().getValue(); // col
					int k = e.getValue(); // playerClicked
					//System.out.println(k);
					ClientGUI.buttons[i][j].setDisable(true);
					if (k == 0 && data.play) {
						//System.out.println("entered");
						ClientGUI.buttons[i][j].setDisable(false);
					} else if (k == 1) {
						ClientGUI.buttons[i][j].setStyle("-fx-background-color: red;");
						
					} else if (k == 2) {
						ClientGUI.buttons[i][j].setStyle("-fx-background-color: yellow;");
					} else if (k == 3) {
						ClientGUI.buttons[i][j].setStyle("-fx-background-color: yellowgreen;");
					}
					ClientGUI.buttons[i][j].playerClicked = k;	
				}
				//System.out.println(data.disableScreens);
				if (data.disableScreens) {
					disableGrid();
				}
				/*
				buttons[info.winningButtons[0]][info.winningButtons[1]].setStyle
				("-fx-background-color: greenyellow;");
				buttons[info.winningButtons[2]][info.winningButtons[3]].setStyle
				("-fx-background-color: greenyellow;");
				buttons[info.winningButtons[4]][info.winningButtons[5]].setStyle
				("-fx-background-color: greenyellow;");
				buttons[info.winningButtons[6]][info.winningButtons[7]].setStyle
				("-fx-background-color: greenyellow;");*/
				/*
				if (data.newGame) {
					System.out.println("entered" + ClientGUI.p1 + ClientGUI.p2);
					enableBottomRow();
				}
				/*
				/*
				if (!data.have2Players) {
					callback.accept("Waiting for another player");
				} else {
					
					callback.accept("Ready to play");
					if (data.play) {
						System.out.println("turn:" + data.playerTurn);
						System.out.println("Player " + data.lastMove[0] + " played" + "(" + data.lastMove[1] 
								+ "," + data.lastMove[2] + ")");
						
						callback.accept("Player " + data.lastMove[0] + " played" + "(" + data.lastMove[1] 
								+ "," + data.lastMove[2] + ")");
								
						
						for (int i = 0; i < 5; i++) {
							for (int j = 0; j < 7; j++) {
								if (ClientGUI.buttons[i+1][j].playerClicked == 0 && i+1 == 5) {
									ClientGUI.buttons[i+1][j].setDisable(false);
								} else if (ClientGUI.buttons[i][j].playerClicked == 0 &&
										(ClientGUI.buttons[i+1][j].playerClicked == 1 || 
										ClientGUI.buttons[i+1][j].playerClicked == 2)) {
									ClientGUI.buttons[i+1][j].setDisable(false);
								}
							}
						}
						
						
						if (ClientGUI.moveMade) {
							send(data);
							ClientGUI.moveMade = false;
						}
						
					} else {
						callback.accept("Wait for other player to move");
					}
				}
		*/
			}
			catch(Exception e) {}
		}
	
    }
	
	public void enableBottomRow() {
		//System.out.println("enabled bottom row");
		
		for (int j = 0; j < 7; j++) {
			//System.out.println("enabled");
			ClientGUI.buttons[5][j].setDisable(false);
		}
	}
	
	public void disableGrid() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				ClientGUI.buttons[i][j].setDisable(true);
			}
		}
	}
	
	public void send(CFourInfo data) {
		
		try {
			/*
			if (data.playerTurn == 1) {
				data.playerTurn = 2;
			}
			if (data.playerTurn == 2) {
				data.playerTurn = 1;
			}
			*/
			out.writeObject(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
