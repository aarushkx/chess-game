package piece;

import main.GamePanel;
import main.Type;

public class Knight extends Piece {

    public Knight(int color, int col, int row) {
        super(color, col, row);

        type = Type.KNIGHT;
        image = color == GamePanel.WHITE ? getImage("white_knight") : getImage("black_knight");
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isInsideBoard(targetCol, targetRow)) {
            int colDistance = Math.abs(targetCol - previousCol);
            int rowDistance = Math.abs(targetRow - previousRow);

            if (colDistance * rowDistance == 2) {
                return isValidPosition(targetCol, targetRow);
            }
        }
        return false;
    }

}
