import java.io.Serializable;
import java.util.HashMap;

import javafx.util.Pair;

public class CFourInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	int playerTurn = 1;  // p1 is 1, p2 is 2
	boolean play = false; // tells client whether its their turn
	int winner = -1; // -1-> no winner yet, 0 -> tie, 1->p1 wins, 2->p2 wins
	boolean have2Players;
	//int [][] board = new int[6][7];
	//int[] lastMove = new int[3]; // player, row, column
	String lastMove = "";
	String gameInfo= "";
	boolean p1Turn;
	boolean p2Turn;
	HashMap<Pair<Integer, Integer>, Integer> buttonMap = new HashMap<>();
	boolean newGame;
	boolean disableScreens;
	//int[] winningButtons = new int[8];
}