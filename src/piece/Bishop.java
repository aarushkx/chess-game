package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece {

    public Bishop(int color, int col, int row) {
        super(color, col, row);

        type = Type.BISHOP;
        image = color == GamePanel.WHITE ? getImage("white_bishop") : getImage("black_bishop");
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isInsideBoard(targetCol, targetRow) && !isPositionSame(targetCol, targetRow)) {
            int colDistance = Math.abs(targetCol - previousCol);
            int rowDistance = Math.abs(targetRow - previousRow);

            if (colDistance == rowDistance) {
                return isValidPosition(targetCol, targetRow) && !isDiagonalPathBlocked(targetCol, targetRow);
            }
        }
        return false;
    }

}
