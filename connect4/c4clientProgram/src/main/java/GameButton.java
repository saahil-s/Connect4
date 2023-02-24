import javafx.scene.control.Button;

public class GameButton extends Button {

	int row, col;
	int playerClicked = 0; // 0 for none, 1 for p1, 2 for p2, 3 for someone wins with this button
	
	public GameButton(int row, int col) {
		this.row = row;
		this.col = col;
	}
}
