import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javafx.application.Application;
import javafx.stage.Stage;

class MyTest {

	int[][] board;
	int[] green;
	
	@BeforeEach
	void setup() {
		board = new int[6][7];
		green = new int[8];
	}
	
	// the following tests will test the checkWinner() function
	@Test
	void noWinnerTestOne() {
		for (int i = 0; i < 3; i++) {
			board[0][i] = 1;
		}
		for (int i = 0; i < 3; i++) {
			board[i][0] = 1;
		}
		int winner = ClientGUI.checkWinner(board, green);
		assertEquals(-1, winner, "winner should be -1");
	}
	
	@Test
	void noWinnerTestTwo() {
		board[0][3] = 1;
		board[5][2] = 1;
		board[3][3] = 2;
		board[3][0] = 2;
		board[2][3] = 1;
		board[0][6] = 1;
		int winner = ClientGUI.checkWinner(board, green);
		assertEquals(-1, winner, "winner should be -1");
	}
	
	@Test
	void horizontalWinnerTestOne() {
		for (int i = 0; i < 4; i++) {
			board[0][i] = 1;
		}
		
		int horizontalWinner = ClientGUI.checkWinner(board, green);
		assertEquals(1, horizontalWinner, "winner should be 1");
		assertEquals(0, green[0]);
		assertEquals(0, green[1]);
		assertEquals(0, green[2]);
		assertEquals(1, green[3]);
		assertEquals(0, green[4]);
		assertEquals(2, green[5]);
		assertEquals(0, green[6]);
		assertEquals(3, green[7]);
	}
	
	@Test
	void horizontalWinnerTestTwo() {
		for (int i = 0; i < 4; i++) {
			board[0][i] = 2;
		}
		
		int horizontalWinner = ClientGUI.checkWinner(board, green);
		assertEquals(2, horizontalWinner, "winner should be 2");
		assertEquals(0, green[0]);
		assertEquals(0, green[1]);
		assertEquals(0, green[2]);
		assertEquals(1, green[3]);
		assertEquals(0, green[4]);
		assertEquals(2, green[5]);
		assertEquals(0, green[6]);
		assertEquals(3, green[7]);
	}

	@Test
	void verticalWinnerTestOne() {
		for (int i = 0; i < 4; i++) {
			board[i][0] = 1;
		}
		
		int verticalWinner = ClientGUI.checkWinner(board, green);
		assertEquals(1, verticalWinner, "winner should be 1");
		assertEquals(0, green[0]);
		assertEquals(0, green[1]);
		assertEquals(1, green[2]);
		assertEquals(0, green[3]);
		assertEquals(2, green[4]);
		assertEquals(0, green[5]);
		assertEquals(3, green[6]);
		assertEquals(0, green[7]);
	}
	
	@Test
	void verticalWinnerTestTwo() {
		for (int i = 0; i < 4; i++) {
			board[i][0] = 2;
		}
		
		int verticalWinner = ClientGUI.checkWinner(board, green);
		assertEquals(2, verticalWinner, "winner should be 2");
		assertEquals(0, green[0]);
		assertEquals(0, green[1]);
		assertEquals(1, green[2]);
		assertEquals(0, green[3]);
		assertEquals(2, green[4]);
		assertEquals(0, green[5]);
		assertEquals(3, green[6]);
		assertEquals(0, green[7]);
	}
	
	@Test
	void upwardDiagonalWinnerTestOne() {
		board[5][0] = 1;
		board[4][1] = 1;
		board[3][2] = 1;
		board[2][3] = 1;
		
		int val = ClientGUI.checkWinner(board, green);
		assertEquals(1, val, "winner should be 1");
		assertEquals(5, green[0]);
		assertEquals(0, green[1]);
		assertEquals(4, green[2]);
		assertEquals(1, green[3]);
		assertEquals(3, green[4]);
		assertEquals(2, green[5]);
		assertEquals(2, green[6]);
		assertEquals(3, green[7]);
	}
	
	@Test
	void upwardDiagonalWinnerTestTwo() {
		board[5][0] = 2;
		board[4][1] = 2;
		board[3][2] = 2;
		board[2][3] = 2;
		
		int val = ClientGUI.checkWinner(board, green);
		assertEquals(2, val, "winner should be 2");
		assertEquals(5, green[0]);
		assertEquals(0, green[1]);
		assertEquals(4, green[2]);
		assertEquals(1, green[3]);
		assertEquals(3, green[4]);
		assertEquals(2, green[5]);
		assertEquals(2, green[6]);
		assertEquals(3, green[7]);
	}

	@Test
	void downwardDiagonalWinnerTestOne() {
		board[0][0] = 1;
		board[1][1] = 1;
		board[2][2] = 1;
		board[3][3] = 1;
		
		int val = ClientGUI.checkWinner(board, green);
		assertEquals(1, val, "winner should be 1");
		assertEquals(0, green[0]);
		assertEquals(0, green[1]);
		assertEquals(1, green[2]);
		assertEquals(1, green[3]);
		assertEquals(2, green[4]);
		assertEquals(2, green[5]);
		assertEquals(3, green[6]);
		assertEquals(3, green[7]);
	}
	
	@Test
	void downwardDiagonalWinnerTestTwo() {
		board[0][0] = 2;
		board[1][1] = 2;
		board[2][2] = 2;
		board[3][3] = 2;
		
		int val = ClientGUI.checkWinner(board, green);
		assertEquals(2, val, "winner should be 2");
		assertEquals(0, green[0]);
		assertEquals(0, green[1]);
		assertEquals(1, green[2]);
		assertEquals(1, green[3]);
		assertEquals(2, green[4]);
		assertEquals(2, green[5]);
		assertEquals(3, green[6]);
		assertEquals(3, green[7]);
	}

}
