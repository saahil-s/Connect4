import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;

public class Server{

	int index = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	private int port;
	int playerNum;
	int turn = 1; // p1 is 1, p2 is 0
	static int joinedNewGame = 0;
	static int newGameGoFirst;
	
	//CFourInfo data/* = new CFourInfo()*/;
	
	Server(Consumer<Serializable> call, int port){
	
		callback = call;
		this.port = port;
		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(port);){
				System.out.println("Server is waiting for a client!");

		    	while(true) {
		    		/*
		    		if (index % 2 == 0) {
		    			playerNum = 2;
		    		} else if (index % 2 == 1) {
		    			playerNum = 1;
		    		}
		    		*/
		    		ClientThread c = new ClientThread(mysocket.accept(), index);
		    		callback.accept("client has connected to server: " + "client #" + index);
		    		if (index == 1) {
		    			clients.add(0, c);
		    		} else {
		    			clients.add(1, c);
		    		}
		    		//clients.add(c);
		    		callback.accept("There are " + clients.size() + " player(s) connected");
		    		/*
		    		if (clients.size() > 1) {
		    			data.have2Players = true;
		    		} else {
		    			data.have2Players = false;
		    		}
		    		*/
		    		c.start();
		    		index++;
			    }
			}//end of try
			catch(Exception e) {
				callback.accept("Server socket did not launch");
				e.printStackTrace();
			}
			
		}//end of while
	}
	
	public class ClientThread extends Thread{
			
		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;
			
		ClientThread(Socket s, int count){
			this.connection = s;
			this.count = count;	
		}
			
		public void updateClients(CFourInfo info) {
			
			for(int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);
				try {
					t.out.writeObject(info);
				}
				catch(Exception e) {}
			}
			
			/*
			if (info.have2Players) {
				if (info.playerTurn == 1) {
					try {
						for (int i = 0; i < 5; i++) {
							for (int j = 0; j < 7; j++) {
								System.out.print(info.board[i][j]);
							}
							System.out.println();
						}
						info.playerTurn =2;
						CFourInfo temp = info;
						CFourInfo temp2 = info;
						temp.play = true;
						clients.get(1).out.writeObject(temp);
						temp2.play = false;
						clients.get(0).out.writeObject(temp2);
					}
					catch(Exception e) {}
				}
				if (info.playerTurn == 2) {
					try {
						for (int i = 0; i < 5; i++) {
							for (int j = 0; j < 7; j++) {
								System.out.print(info.board[i][j]);
							}
							System.out.println();
						}
						info.playerTurn = 1;
						CFourInfo temp = info;
						CFourInfo temp2 = info;
						temp.play = false;
						clients.get(1).out.writeObject(temp);
						temp2.play = true;
						clients.get(0).out.writeObject(temp2);
					}
					catch(Exception e) {}
				}
			} else {
				try {
					clients.get(0).out.writeObject(info);
				}
				catch(Exception e) {}
			}
			*/
		}
		
		public void updateClients(CFourInfo info, int playerIndex) {
			try {
				clients.get(playerIndex).out.writeObject(info);
			}
			catch(Exception e) {}
		}
		
		public void run(){
					
			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);	
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}
			// if count is 1 joined ++ or 2
			joinedNewGame++;
			
			//updateClients("new client on server: client #"+count);
			if (joinedNewGame == 2) { // change this to joined == 2 keeps track of how many players are in the game
				joinedNewGame = 0;
				CFourInfo info = new CFourInfo();
				CFourInfo tempInfo = new CFourInfo();
				CFourInfo tempInfo2 = new CFourInfo();
				info.gameInfo = "The game has started";
				updateClients(info);
				
				tempInfo.have2Players = true;
				tempInfo.p1Turn = true;
				tempInfo.play = true;
				tempInfo.gameInfo = "Player 1: Make a move";
				updateClients(tempInfo, 0);
				
				tempInfo2.gameInfo = "Player 2: Wait for other player to move";
				updateClients(tempInfo2, 1);
			} else {
				CFourInfo info = new CFourInfo();
				info.gameInfo = "Waiting for another player to connect";
				updateClients(info);
			}
			while(true) {
				try {
					//updateClients(data); 
					CFourInfo data = (CFourInfo)in.readObject();
					//callback.accept("client: " + count + " sent: " + data);
					//updateClients("client #"+count+" said: "+data); 
					CFourInfo tempInfo = new CFourInfo();
					CFourInfo tempInfo2 = new CFourInfo();
					/*
					if (!data.newGame) {
					callback.accept("Player " + count + " played " + data.lastMove);
					
					data.gameInfo = "Player " + count + " played " + data.lastMove;
					updateClients(data);
					}*/
					// when the game is still ongoing
					if (data.newGame) {
						// increment joined
						joinedNewGame++;
						if (joinedNewGame == 1) {
							newGameGoFirst = count;
							callback.accept("Player " + count + " is playing again!");
						}
						// check if joined is 2
						// if joined is 2 then send messages to both players
						// turn = 1
						if (joinedNewGame == 2) {
							joinedNewGame = 0;
							callback.accept("Player " + count + " is playing again!");
							/*
							turn = 1;
							// decide who moves
							CFourInfo info = new CFourInfo();
							CFourInfo newGameInfo = new CFourInfo();
							CFourInfo newGameInfo2 = new CFourInfo();
							info.gameInfo = "The game has started";
							updateClients(info);
							
							newGameInfo.have2Players = true;
							//System.out.println(newGameInfo.have2Players);
							newGameInfo.p1Turn = true;
							newGameInfo.play = true;
							newGameInfo.gameInfo = "Player 1: Make a move";
							//newGameInfo.newGame = true;
							//newGameInfo.notDisable = true; // maybe delete // dont disable board
							updateClients(newGameInfo, 0);
							
							newGameInfo2.gameInfo = "Player 2: Wait for other player to move";
							updateClients(newGameInfo2, 1);
							*/
							//turn = 2;
							turn = newGameGoFirst;
							// decide who moves
							CFourInfo info = new CFourInfo();
							CFourInfo newGameInfo = new CFourInfo();
							CFourInfo newGameInfo2 = new CFourInfo();
							info.gameInfo = "The game has started";
							updateClients(info);
							
							if (turn == 1) {
							newGameInfo.have2Players = true;
							//System.out.println(newGameInfo.have2Players);
							newGameInfo.p1Turn = true;
							newGameInfo.play = true;
							newGameInfo.gameInfo = "Player 1: Make a move";
							//newGameInfo.newGame = true;
							//newGameInfo.notDisable = true; // maybe delete // dont disable board
							updateClients(newGameInfo, 0);
							
							newGameInfo2.gameInfo = "Player 2: Wait for other player to move";
							updateClients(newGameInfo2, 1);
							} else if (turn == 2) {
								newGameInfo.have2Players = true;
								//System.out.println(newGameInfo.have2Players);
								newGameInfo.p2Turn = true;
								newGameInfo.play = true;
								newGameInfo.gameInfo = "Player 2: Make a move";
								//newGameInfo.newGame = true;
								//newGameInfo.notDisable = true; // maybe delete // dont disable board
								updateClients(newGameInfo, 1);
								
								newGameInfo2.gameInfo = "Player 1: Wait for other player to move";
								updateClients(newGameInfo2, 0);
							}
							//turn++;
						} else {
							CFourInfo info = new CFourInfo();
							info.gameInfo = "Waiting for another player to connect";
							updateClients(info, count - 1);
						}
						
					} else {
						callback.accept("Player " + count + " played " + data.lastMove);
						
						data.gameInfo = "Player " + count + " played " + data.lastMove;
						updateClients(data);
						
						CFourInfo tempInfo3 = new CFourInfo();
						
						if (data.winner == 0) {
							//CFourInfo tempInfo3 = new CFourInfo();
							tempInfo3.gameInfo = "The game ended in a tie";
							tempInfo3.disableScreens = true; // disable after win
							updateClients(tempInfo3);
						} else if (data.winner == 1) {
							//CFourInfo tempInfo3 = new CFourInfo();
							tempInfo3.gameInfo = "Player 1 wins";
							//tempInfo3.winningButtons = data.winningButtons;
							tempInfo3.disableScreens = true; // disable after win
							updateClients(tempInfo3);
						} else if (data.winner == 2) {
							//CFourInfo tempInfo3 = new CFourInfo();
							tempInfo3.gameInfo = "Player 2 wins";
							//tempInfo3.winningButtons = data.winningButtons;
							tempInfo3.disableScreens = true; // disable after win
							updateClients(tempInfo3);
						} else {
							turn++;
							if (turn % 2 == 0) {
								tempInfo2.gameInfo = "Player 2: move";
								tempInfo.gameInfo = "Player 1: wait for other player to move";
								tempInfo2.p2Turn = true;
								tempInfo2.play = true;
								tempInfo2.buttonMap = data.buttonMap;
							} else {
								tempInfo.gameInfo = "Player 1: move";
								tempInfo2.gameInfo = "Player 2: wait for other player to move";
								tempInfo.p1Turn = true;
								tempInfo.play = true;
								tempInfo.buttonMap = data.buttonMap;
							}
							updateClients(tempInfo, 0);
							updateClients(tempInfo2, 1);
						}
					}
					/*
					if (clients.size() > 1) {
		    			data.have2Players = true;
		    		} else {
		    			data.have2Players = false;
		    		}
		    		*/
					//updateClients(data);
					/*
					if (data.lastMove[0] != 0) {
						callback.accept("Player " + data.lastMove[0] + " played" + "(" + data.lastMove[1] 
								+ "," + data.lastMove[2] + ")");
						if (data.winner == 1) {
							callback.accept("Player 1 won!");
						}
						if (data.winner == 2) {
							callback.accept("Player 2 won!");
						}
						if (data.winner == 0) {
							callback.accept("Game tied!");
						}
					}
					*/
					//updateClients(data);
					//data = (CFourInfo)in.readObject();
				}
				catch(Exception e) {
					callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					//updateClients("Client #"+count+" has left the server!");
					clients.remove(this);
					if (count == 2) {
						index = 2;
					} else {
						index = 1;
					}
					turn = 1;
					break;
				}
			}
		}//end of run
		
	}//end of client thread
}