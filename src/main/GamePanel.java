package main;

import input.Mouse;
import piece.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 880;
    public static final int HEIGHT = 640;
    public static final int FPS = 60;
    public static final String FONT = "Montserrat";
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    public static List<Piece> pieces = new ArrayList<>();
    public static List<Piece> simPieces = new ArrayList<>();

    public static Piece castlingPiece;
    public List<Piece> promotionPieces = new ArrayList<>();

    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    int currentColor = WHITE;

    private Piece selectedPiece, checkingPiece;

    private boolean canMove;
    private boolean isPositionValid;
    private boolean isPromoted;
    private boolean isStalemate;
    private boolean isGameOver;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(148, 134, 98));
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void setPieces() {
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }

    private void copyPieces(List<Piece> source, List<Piece> target) {
        target.clear();
        target.addAll(source);
    }

    public void launch() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void update() {
        if (isPromoted) {
            promote();
        } else if (!isGameOver && !isStalemate) {
            if (mouse.pressed) {
                if (selectedPiece == null) {
                    for (Piece piece : simPieces) {
                        if (piece.color == currentColor && piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                            selectedPiece = piece;
                        }
                    }
                } else {
                    simulate();
                }
            }

            if (!mouse.pressed) {
                if (selectedPiece != null) {
                    if (isPositionValid) {
                        copyPieces(simPieces, pieces);
                        selectedPiece.updatePosition();

                        if (castlingPiece != null) {
                            castlingPiece.updatePosition();
                        }

                        if (isKingInCheck() && isCheckmate()) {
                            isGameOver = true;
                        } else if (isStalemate() && !isKingInCheck()) {
                            isStalemate = true;
                        } else {
                            if (canPromote()) {
                                isPromoted = true;
                            } else {
                                changePlayerTurn();
                            }
                        }
                    } else {
                        copyPieces(pieces, simPieces);
                        selectedPiece.resetPosition();
                        selectedPiece = null;
                    }
                }
            }
        }
    }

    private void simulate() {
        canMove = false;
        isPositionValid = false;

        copyPieces(pieces, simPieces);

        if (castlingPiece != null) {
            castlingPiece.col = castlingPiece.previousCol;
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
            castlingPiece = null;
        }

        selectedPiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
        selectedPiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
        selectedPiece.col = selectedPiece.getCol(selectedPiece.x);
        selectedPiece.row = selectedPiece.getRow(selectedPiece.y);

        if (selectedPiece.canMove(selectedPiece.col, selectedPiece.row)) {
            canMove = true;

            if (selectedPiece.capturablePiece != null) {
                simPieces.remove(selectedPiece.capturablePiece.getIndex());
            }

            checkCastling();

            if (!isMoveIllegal(selectedPiece) && !canOpponentCaptureKing()) {
                isPositionValid = true;
            }
        }
    }

    private void checkCastling() {
        if (castlingPiece != null) {
            if (castlingPiece.col == 0) {
                castlingPiece.col += 3;
            } else if (castlingPiece.col == 7) {
                castlingPiece.col -= 2;
            }
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
        }
    }

    private boolean canPromote() {
        if (selectedPiece.type == Type.PAWN) {
            if (currentColor == WHITE && selectedPiece.row == 0 || currentColor == BLACK && selectedPiece.row == 7) {
                promotionPieces.clear();
                promotionPieces.add(new Rook(currentColor, 9, 2));
                promotionPieces.add(new Knight(currentColor, 9, 3));
                promotionPieces.add(new Bishop(currentColor, 9, 4));
                promotionPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }

    private void promote() {
        if (mouse.pressed) {
            for (Piece piece : promotionPieces) {
                if (piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                    switch (piece.type) {
                        case ROOK -> simPieces.add(new Rook(currentColor, selectedPiece.col, selectedPiece.row));
                        case KNIGHT -> simPieces.add(new Knight(currentColor, selectedPiece.col, selectedPiece.row));
                        case BISHOP -> simPieces.add(new Bishop(currentColor, selectedPiece.col, selectedPiece.row));
                        case QUEEN -> simPieces.add(new Queen(currentColor, selectedPiece.col, selectedPiece.row));
                    }
                    simPieces.remove(selectedPiece.getIndex());
                    copyPieces(simPieces, pieces);
                    selectedPiece = null;
                    isPromoted = false;
                    changePlayerTurn();
                }
            }
        }
    }

    private void changePlayerTurn() {
        if (currentColor == WHITE) {
            currentColor = BLACK;

            for (Piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.hasAdvancedTwoSteps = false;
                }
            }
        } else {
            currentColor = WHITE;

            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.hasAdvancedTwoSteps = false;
                }
            }
        }
        selectedPiece = null;
    }

    private Piece getKing(boolean opponent) {
        Piece king = null;

        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    king = piece;
                }
            } else {
                if (piece.type == Type.KING && piece.color == currentColor) {
                    king = piece;
                }
            }
        }
        return king;
    }

    private boolean isMoveIllegal(Piece king) {
        if (king.type == Type.KING) {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isKingInCheck() {
        Piece king = getKing(true);

        if (selectedPiece.canMove(king.col, king.row)) {
            checkingPiece = selectedPiece;
            return true;
        } else {
            checkingPiece = null;
        }
        return false;
    }

    private boolean canOpponentCaptureKing() {
        Piece king = getKing(false);

        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCheckmate() {
        Piece king = getKing(true);

        if (canKingMove(king)) {
            return false;
        } else {
            int colDiff = Math.abs(checkingPiece.col - king.col);
            int rowDiff = Math.abs(checkingPiece.row - king.row);

            if (colDiff == 0) {
                if (checkingPiece.row < king.row) {
                    for (int row = 0; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingPiece.row > king.row) {
                    for (int row = checkingPiece.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (rowDiff == 0) {
                if (checkingPiece.col < king.col) {
                    for (int col = checkingPiece.col; col < king.col; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingPiece.col > king.col) {
                    for (int col = checkingPiece.col; col > king.col; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (colDiff == rowDiff) {
                if (checkingPiece.row < king.row) {
                    if (checkingPiece.col < king.col) {
                        for (int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingPiece.col > king.col) {
                        for (int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                if (checkingPiece.row > king.row) {
                    if (checkingPiece.col < king.col) {
                        for (int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingPiece.col > king.col) {
                        for (int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean canKingMove(Piece king) {
        int[] colOffsets = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] rowOffsets = {-1, -1, -1, 0, 0, 1, 1, 1};

        for (int i = 0; i < rowOffsets.length; i++) {
            if (isValidMove(king, colOffsets[i], rowOffsets[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidMove(Piece king, int moveCol, int moveRow) {
        boolean isValidMove = false;

        king.col += moveCol;
        king.row += moveRow;

        if (king.canMove(king.col, king.row)) {
            if (king.capturablePiece != null) {
                simPieces.remove(king.capturablePiece.getIndex());
            }
            if (!isMoveIllegal(king)) {
                isValidMove = true;
            }
        }

        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }

    private boolean isStalemate() {
        int opponentPieceCount = 0;

        for (Piece piece : simPieces) {
            if (piece.color != currentColor) {
                opponentPieceCount++;
            }
        }
        if (opponentPieceCount == 1) {
            return !canKingMove(getKing(true));
        }
        return false;
    }

    @Override
    public void run() {
        double drawInterval = (double) 1_000_000_000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        board.draw(g2);

        for (Piece piece : new ArrayList<>(simPieces)) {
            piece.draw(g2);
        }

        if (selectedPiece != null) {
            if (canMove) {
                if (isMoveIllegal(selectedPiece) || canOpponentCaptureKing()) {
                    g2.setColor(new Color(174, 0, 0));
                } else {
                    g2.setColor(new Color(255, 255, 143));
                }
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2.fillRect(selectedPiece.col * Board.SQUARE_SIZE, selectedPiece.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            selectedPiece.draw(g2);
        }

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font(FONT, Font.BOLD, 20));
        g2.setColor(Color.WHITE);

        if (isPromoted) {
            g2.drawString("Promote to:", 700, 150);
            for (Piece piece : promotionPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        } else {
            if (currentColor == WHITE) {
                g2.drawString("White's Turn", 690, 520);

                if (checkingPiece != null && checkingPiece.color == BLACK) {
                    g2.setColor(new Color(130, 42, 21));
                    g2.drawString("Check!", 720, 550);
                }
            } else {
                g2.drawString("Black's Turn", 690, 120);
                if (checkingPiece != null && checkingPiece.color == WHITE) {
                    g2.setColor(new Color(130, 42, 21));
                    g2.drawString("Check!", 720, 90);
                }
            }
        }

        if (isStalemate) {
            g2.setColor(new Color(48, 46, 41));
            g2.drawString("Stalemate!", 710, 330);
        }
        if (isGameOver) {
            String text = currentColor == WHITE ? "White Won!" : "Black Won!";
            g2.setColor(new Color(145, 230, 55));
            g2.drawString(text, 700, 330);
        }
    }

}
