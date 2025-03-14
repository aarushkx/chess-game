package piece;

import main.Board;
import main.GamePanel;
import main.Type;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Piece {

    public Type type;
    public BufferedImage image;
    public int color;
    public int x, y;
    public int col, row, previousCol, previousRow;
    public Piece capturablePiece;
    public boolean hasMoved, hasAdvancedTwoSteps;

    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        previousCol = col;
        previousRow = row;
    }

    public BufferedImage getImage(String path) {
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/piece/" + path + ".png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getIndex() {
        for (int index = 0; index < GamePanel.simPieces.size(); index++) {
            if (GamePanel.simPieces.get(index) == this) {
                return index;
            }
        }
        return -1;
    }

    public void updatePosition() {
        if (type == Type.PAWN) {
            int rowDistance = Math.abs(row - previousRow);
            if (rowDistance == 2) {
                hasAdvancedTwoSteps = true;
            }
        }

        x = getX(col);
        y = getY(row);
        previousCol = getCol(x);
        previousRow = getRow(y);
        hasMoved = true;
    }

    public void resetPosition() {
        col = previousCol;
        row = previousRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    public boolean isInsideBoard(int targetCol, int targetRow) {
        return targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7;
    }

    public boolean isPositionSame(int targetCol, int targetRow) {
        return targetCol == previousCol && targetRow == previousRow;
    }

    public Piece getCapturablePiece(int targetCol, int targetRow) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidPosition(int targetCol, int targetRow) {
        capturablePiece = getCapturablePiece(targetCol, targetRow);

        if (capturablePiece == null) {
            return true;
        } else {
            if (capturablePiece.color != this.color) {
                return true;
            } else {
                capturablePiece = null;
            }
        }
        return false;
    }

    public boolean isStraightPathBlocked(int targetCol, int targetRow) {
        for (int c = previousCol - 1; c > targetCol; c--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    capturablePiece = piece;
                    return true;
                }
            }
        }
        for (int c = previousCol + 1; c < targetCol; c++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    capturablePiece = piece;
                    return true;
                }
            }
        }
        for (int r = previousRow - 1; r > targetRow; r--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == r) {
                    capturablePiece = piece;
                    return true;
                }
            }
        }
        for (int r = previousRow + 1; r < targetRow; r++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == r) {
                    capturablePiece = piece;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDiagonalPathBlocked(int targetCol, int targetRow) {
        if (targetRow < previousRow) {
            for (int c = previousCol - 1; c > targetCol; c--) {
                int diff = Math.abs(c - previousCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == previousRow - diff) {
                        capturablePiece = piece;
                        return true;
                    }
                }
            }
            for (int c = previousCol + 1; c < targetCol; c++) {
                int diff = Math.abs(c - previousCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == previousRow - diff) {
                        capturablePiece = piece;
                        return true;
                    }
                }
            }
        }
        if (targetRow > previousRow) {
            for (int c = previousCol - 1; c > targetCol; c--) {
                int diff = Math.abs(c - previousCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == previousRow + diff) {
                        capturablePiece = piece;
                        return true;
                    }
                }
            }
            for (int c = previousCol + 1; c < targetCol; c++) {
                int diff = Math.abs(c - previousCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == previousRow + diff) {
                        capturablePiece = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

}
