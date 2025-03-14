package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece {

    public Pawn(int color, int col, int row) {
        super(color, col, row);

        type = Type.PAWN;
        image = color == GamePanel.WHITE ? getImage("white_pawn") : getImage("black_pawn");
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isInsideBoard(targetCol, targetRow) && !isPositionSame(targetCol, targetRow)) {

            int move = color == GamePanel.WHITE ? -1 : 1;

            capturablePiece = getCapturablePiece(targetCol, targetRow);

            if (targetCol == previousCol && targetRow == previousRow + move && capturablePiece == null) {
                return true;
            }
            if (targetCol == previousCol && targetRow == previousRow + move * 2 && capturablePiece == null && !hasMoved && !isStraightPathBlocked(targetCol, targetRow)) {
                return true;
            }

            int colDistance = Math.abs(targetCol - previousCol);

            if (colDistance == 1 && targetRow == previousRow + move && capturablePiece != null && capturablePiece.color != color) {
                return true;
            }

            if (colDistance == 1 && targetRow == previousRow + move) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == targetCol && piece.row == previousRow && piece.hasAdvancedTwoSteps) {
                        capturablePiece = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
