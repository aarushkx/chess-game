package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece {

    public Rook(int color, int col, int row) {
        super(color, col, row);

        type = Type.ROOK;
        image = color == GamePanel.WHITE ? getImage("white_rook") : getImage("black_rook");
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isInsideBoard(targetCol, targetRow) && !isPositionSame(targetCol, targetRow)) {
            if (targetCol == previousCol || targetRow == previousRow) {
                return isValidPosition(targetCol, targetRow) && !isStraightPathBlocked(targetCol, targetRow);
            }
        }
        return false;
    }

}
