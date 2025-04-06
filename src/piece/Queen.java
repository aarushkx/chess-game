package piece;

import main.GamePanel;
import main.Type;

public class Queen extends Piece {

    public Queen(int color, int col, int row) {
        super(color, col, row);

        type = Type.QUEEN;
        image = color == GamePanel.WHITE ? getImage("white_queen") : getImage("black_queen");
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isInsideBoard(targetCol, targetRow) && !isPositionSame(targetCol, targetRow)) {
            if (targetCol == previousCol || targetRow == previousRow) {
                return isValidPosition(targetCol, targetRow) && !isStraightPathBlocked(targetCol, targetRow);
            }

            int colDistance = Math.abs(targetCol - previousCol);
            int rowDistance = Math.abs(targetRow - previousRow);

            if (colDistance == rowDistance) {
                return isValidPosition(targetCol, targetRow) && !isDiagonalPathBlocked(targetCol, targetRow);
            }
        }
        return false;
    }

}
