package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * Represents a computer player for the checkers game.
 * 
 * @author Daria Berezianska
 * @version 1.3
 */
public class CheckersComputerPlayer {
    private CheckersLogic gameLogic;

    /**
     * Constructs a new CheckersComputerPlayer object.
     *
     * @param gameLogic The game logic instance.
     */
    public CheckersComputerPlayer(CheckersLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    /**
     * Generates a move for the computer player.
     *
     * @return An array representing the move [startX, startY, endX, endY].
     *         Returns null if no valid moves are available.
     */
    public int[] generateMove() {
        try {
            List<int[]> validMoves = getAllValidMoves();
            if (validMoves.isEmpty()) {
                return null; // No valid moves available
            }

            // Prioritize moves with captures if available
            List<int[]> movesWithCaptures = filterMovesWithCaptures(validMoves);
            if (!movesWithCaptures.isEmpty()) {
                return movesWithCaptures.get(0); // Choose the first move with a capture
            }

            // If no moves with captures, choose a random valid move
            Random random = new Random();
            return validMoves.get(random.nextInt(validMoves.size()));
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Error: Index out of bounds while generating move.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves all valid moves for the computer player.
     *
     * @return A list of arrays containing the start and end positions of each valid
     *         move [startX, startY, endX, endY].
     */
    private List<int[]> getAllValidMoves() {
        List<int[]> validMoves = new ArrayList<>();
        char[][] board = gameLogic.getBoard();

        // Iterate over the board to find all valid moves
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == (gameLogic.isPlayerX() ? 'x' : 'o')) {
                    List<int[]> moves = getAllValidMovesForPiece(i, j);
                    validMoves.addAll(moves);
                }
            }
        }

        return validMoves;
    }

    /**
     * Retrieves all valid moves for a specific piece on the board, including
     * capturing opponent pieces.
     *
     * @param startX The starting X coordinate of the piece.
     * @param startY The starting Y coordinate of the piece.
     * @return A list of arrays containing the start and end positions of each valid
     *         move for the piece [startX, startY, endX, endY].
     */
    private List<int[]> getAllValidMovesForPiece(int startX, int startY) {
        List<int[]> validMoves = new ArrayList<>();
        char[][] board = gameLogic.getBoard();
        char playerPiece = board[startX][startY];
        int[] directions = (playerPiece == 'x') ? new int[] { -1, 1 } : new int[] { 1, -1 };

        // Check all diagonal directions
        for (int dx : directions) {
            for (int dy : new int[] { -1, 1 }) {
                int endX = startX + dx;
                int endY = startY + dy;
                if (gameLogic.isValidMove(startX, startY, endX, endY)) {
                    validMoves.add(new int[] { startX, startY, endX, endY });
                } else if (gameLogic.isValidMove(startX, startY, endX + dx, endY + dy)) {
                    // If a move involves capturing an opponent piece, add it to valid moves
                    validMoves.add(new int[] { startX, startY, endX + dx, endY + dy });
                }
            }
        }

        return validMoves;
    }

    /**
     * Filters out moves that involve capturing opponent pieces.
     *
     * @param moves The list of moves to filter.
     * @return A list of moves that involve capturing opponent pieces.
     */
    private List<int[]> filterMovesWithCaptures(List<int[]> moves) {
        List<int[]> movesWithCaptures = new ArrayList<>();
        for (int[] move : moves) {
            int startX = move[0];
            int startY = move[1];
            int endX = move[2];
            int endY = move[3];
            if (Math.abs(startX - endX) == 2 && Math.abs(startY - endY) == 2) {
                int capturedX = (startX + endX) / 2;
                int capturedY = (startY + endY) / 2;
                if (gameLogic.getBoard()[capturedX][capturedY] != '_') {
                    movesWithCaptures.add(move);
                }
            }
        }
        return movesWithCaptures;
    }

}
