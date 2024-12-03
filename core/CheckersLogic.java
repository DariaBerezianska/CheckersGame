package core;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Represents the logic of a checkers game.
 * 
 * @author Daria Berezianska
 * @version 1.3
 */
public class CheckersLogic {
    private char[][] board; // Game board
    private boolean playerX; // Player X's turn

    /**
     * Constructs a new CheckersLogic object, initializing the game board and
     * setting player X to start.
     */
    public CheckersLogic() {
        this.board = initializeBoard();
        this.playerX = true;
    }

    /**
     * Checks if the current player has no pieces left on the board.
     *
     * @return True if the current player has no pieces left, false otherwise.
     */
    public boolean noPiecesLeft() {
        char currentPlayerPiece = isPlayerX() ? 'x' : 'o';
        char[][] board = getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == currentPlayerPiece) {
                    return false; // Found at least one piece
                }
            }
        }
        return true; // No pieces found for the current player
    }

    /**
     * Retrieves all valid moves for the current player on the board.
     *
     * @return A list of arrays containing the start and end positions of each valid
     *         move [startX, startY, endX, endY].
     */
    public List<int[]> getAllValidMoves() {
        List<int[]> validMoves = new ArrayList<>();
        char currentPlayerPiece = isPlayerX() ? 'x' : 'o';
        char[][] board = getBoard();

        // Iterate over the board to find all valid moves for the current player
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == currentPlayerPiece) {
                    List<int[]> moves = getAllValidMovesForPiece(i, j);
                    validMoves.addAll(moves);
                }
            }
        }

        return validMoves;
    }

    /**
     * Retrieves all valid moves for a specific piece on the board.
     *
     * @param startX The starting X coordinate of the piece.
     * @param startY The starting Y coordinate of the piece.
     * @return A list of arrays containing the start and end positions of each valid
     *         move for the piece [startX, startY, endX, endY].
     */
    private List<int[]> getAllValidMovesForPiece(int startX, int startY) {
        List<int[]> validMoves = new ArrayList<>();
        char[][] board = getBoard();
        char playerPiece = board[startX][startY];
        int[] directions = (playerPiece == 'x') ? new int[] { -1, 1 } : new int[] { 1, -1 };

        // Check all diagonal directions
        for (int dx : directions) {
            for (int dy : new int[] { -1, 1 }) {
                int endX = startX + dx;
                int endY = startY + dy;
                if (isValidMove(startX, startY, endX, endY)) {
                    validMoves.add(new int[] { startX, startY, endX, endY });
                }
            }
        }

        return validMoves;
    }

    /**
     * Initializes the game board with initial pieces.
     * For simplicity, player X is represented by 'x', player O by 'o', and empty
     * cells by '_'.
     * 
     * @return The initialized game board.
     */
    private char[][] initializeBoard() {
        char[][] board = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 != 0) {
                    if (i < 3) {
                        board[i][j] = 'x'; // Player X's pieces
                    } else if (i > 4) {
                        board[i][j] = 'o'; // Player O's pieces
                    } else {
                        board[i][j] = '_'; // Empty cell
                    }
                } else {
                    board[i][j] = '_'; // Empty cell
                }
            }
        }
        return board;
    }

    /**
     * Makes a move on the game board.
     * 
     * @param startX The starting X coordinate of the piece.
     * @param startY The starting Y coordinate of the piece.
     * @param endX   The ending X coordinate of the piece.
     * @param endY   The ending Y coordinate of the piece.
     * @return True if the move was successful, false otherwise.
     */
    public boolean makeMove(int startX, int startY, int endX, int endY) {
        if (!isValidMove(startX, startY, endX, endY)) {
            return false;
        }
        board[endX][endY] = board[startX][startY];
        board[startX][startY] = '_';
        if (Math.abs(startX - endX) == 2 && Math.abs(startY - endY) == 2) {
            int capturedX = (startX + endX) / 2;
            int capturedY = (startY + endY) / 2;
            board[capturedX][capturedY] = '_';
        }
        playerX = !playerX;
        return true;
    }

    /**
     * Checks if a move from the start position to the end position is valid.
     *
     * @param startX The starting row of the piece.
     * @param startY The starting column of the piece.
     * @param endX   The ending row of the piece.
     * @param endY   The ending column of the piece.
     * @return True if the move is valid, false otherwise.
     */
    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        char[][] board = getBoard();
        char movingPiece = board[startX][startY];
        char currentPlayerPiece = isPlayerX() ? 'x' : 'o';

        // Check if the piece being moved belongs to the current player
        if (movingPiece != currentPlayerPiece) {
            return false;
        }

        // Check if the end position is within the board boundaries
        if (endX < 0 || endX > 7 || endY < 0 || endY > 7) {
            return false;
        }

        // Check if the end position is empty
        if (board[endX][endY] != '_') {
            return false;
        }

        // Check if the move is diagonal
        int deltaX = Math.abs(endX - startX);
        int deltaY = Math.abs(endY - startY);
        if (deltaX != 1 || deltaY != 1) {
            // If it's not diagonal, return false
            // If it's diagonal and involves a capture, check for opponent's piece
            if (deltaX == 2 && deltaY == 2) {
                int capturedX = (startX + endX) / 2;
                int capturedY = (startY + endY) / 2;
                char opponentPiece = (currentPlayerPiece == 'x') ? 'o' : 'x';
                if (board[capturedX][capturedY] != opponentPiece) {
                    return false; // No opponent's piece to capture
                }

                // Remove the captured piece from the board temporarily
                char capturedPiece = board[capturedX][capturedY];
                board[capturedX][capturedY] = '_';

                // Recursively check for further captures from the end position
                boolean furtherCapturePossible = false;
                for (int[] direction : new int[][] { { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } }) {
                    int furtherCaptureX = endX + direction[0];
                    int furtherCaptureY = endY + direction[1];
                    if (isValidMove(endX, endY, furtherCaptureX, furtherCaptureY)) {
                        furtherCapturePossible = true;
                        break;
                    }
                }

                // Restore the captured piece to the board
                board[capturedX][capturedY] = capturedPiece;

                if (furtherCapturePossible) {
                    return true; // Further capture is possible
                }
            } else {
                return false;
            }
        }

        // Check if the piece is moving forward
        if (movingPiece == 'x' && endX <= startX) {
            return false;
        }
        if (movingPiece == 'o' && endX >= startX) {
            return false;
        }

        // If all conditions pass, the move is valid
        return true;
    }

    /**
     * Retrieves the current game board.
     * 
     * @return The game board.
     */
    public char[][] getBoard() {
        return board;
    }

    /**
     * Checks if it's player X's turn.
     * 
     * @return True if it's player X's turn, false otherwise.
     */
    public boolean isPlayerX() {
        return playerX;
    }

    /**
     * Checks if the game is over.
     * 
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        // Game over logic would be implemented here
        return false; // For simplicity, always returns false
    }

    /**
     * Checks if the piece at the specified coordinates belongs to the current
     * player.
     *
     * @param x the row of the piece
     * @param y the column of the piece
     * @return true if the piece at (x, y) belongs to the player whose turn it is,
     *         false otherwise.
     */
    public boolean isPieceBelongsToCurrentPlayer(int x, int y) {
        char piece = board[x][y];
        return (playerX && piece == 'x') || (!playerX && piece == 'o');
    }
}
