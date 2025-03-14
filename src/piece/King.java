package piece;

import main.GamePanel;
import main.Type;

public class King extends Piece {

    public King(int color, int col, int row) {
        super(color, col, row);

        type = Type.KING;
        image = color == GamePanel.WHITE ? getImage("white_king") : getImage("black_king");
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isInsideBoard(targetCol, targetRow)) {
            int colDistance = Math.abs(targetCol - previousCol);
            int rowDistance = Math.abs(targetRow - previousRow);

            if ((colDistance + rowDistance == 1) || (colDistance * rowDistance == 1)) {
                return isValidPosition(targetCol, targetRow);
            }
        }

        if (!hasMoved) {
            if (targetCol == previousCol + 2 && targetRow == previousRow && !isStraightPathBlocked(targetCol, targetRow)) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == previousCol + 3 && piece.row == previousRow && !piece.hasMoved && piece.color == this.color) {
                        GamePanel.castlingPiece = piece;
                        return true;
                    }
                }
            }
            if (targetCol == previousCol - 2 && targetRow == previousRow && !isStraightPathBlocked(targetCol, targetRow)) {
                Piece[] pieces = new Piece[2];
                for (Piece piece : GamePanel.pieces) {
                    if (piece.col == previousCol - 3 && piece.row == targetRow) {
                        pieces[0] = piece;
                    }
                    if (piece.col == previousCol - 4 && piece.row == targetRow) {
                        pieces[1] = piece;
                    }
                    if (pieces[0] == null && pieces[1] != null && !pieces[1].hasMoved && pieces[1].color == this.color) {
                        GamePanel.castlingPiece = pieces[1];
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
