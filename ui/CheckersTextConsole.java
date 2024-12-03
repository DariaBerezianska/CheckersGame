package ui;

import core.CheckersLogic;
import core.CheckersComputerPlayer;
import java.util.List;
import java.util.Scanner;
import javafx.application.Application;

/**
 * 
 * Represents a text-based console interface for playing checkers.
 * 
 * @author Daria Berezianska
 * @version 1.3
 */
public class CheckersTextConsole {
    private CheckersLogic gameLogic;
    private Scanner scanner;

    /**
     * Constructs a new CheckersTextConsole object, initializing the game logic and
     * scanner.
     */
    public CheckersTextConsole() {
        this.gameLogic = new CheckersLogic();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the checkers game based on user's choice of opponent.
     */
    public void startGame() {
        // Prompt the player to choose the opponent
        System.out.println(
                "Begin Game. Enter 'P' if you want to play against another player; enter 'C' to play against the computer.");
        String opponentChoice = scanner.nextLine().toUpperCase();
        if (!opponentChoice.equals("P") && !opponentChoice.equals("C")) {
            System.out.println("Invalid choice. Exiting game.");
            return;
        }

        // Start the game based on the opponent choice
        System.out.println(
                opponentChoice.equals("P") ? "Start game against another player." : "Start game against computer.");

        if (opponentChoice.equals("P")) {
            playAgainstAnotherPlayer();
        } else {
            playAgainstComputer();
        }

        // Display the final state of the board and end the game
        displayBoard();
        System.out.println("Game over!");
    }

    /**
     * Handles the game logic when playing against another player.
     */
    private void playAgainstAnotherPlayer() {
        // Game loop for playing against another player
        while (!gameLogic.isGameOver()) {
            displayBoard();
            char currentPlayer = gameLogic.isPlayerX() ? 'X' : 'O';
            System.out.println("You are Player " + currentPlayer + ". It is your turn.");
            if (gameLogic.noPiecesLeft() || gameLogic.getAllValidMoves().isEmpty()) {
                char opponent = gameLogic.isPlayerX() ? 'O' : 'X';
                System.out
                        .println("No valid moves left for Player " + currentPlayer + ". Player " + opponent + " wins!");
                break;
            }

            // Prompt for player's move input
            int[] move = getPlayerMove();
            if (move == null) {
                System.out.println("Invalid move. Try again.");
            } else {
                gameLogic.makeMove(move[0], move[1], move[2], move[3]);
            }
        }
    }

    /**
     * Handles the game logic when playing against the computer.
     */
    private void playAgainstComputer() {
        // Game loop for playing against the computer
        while (!gameLogic.isGameOver()) {
            // Computer's turn
            if (!gameLogic.isPlayerX()) {
                List<int[]> computerMoves = gameLogic.getAllValidMoves();
                if (!computerMoves.isEmpty()) {
                    int[] computerMove = getComputerMove();
                    gameLogic.makeMove(computerMove[0], computerMove[1], computerMove[2], computerMove[3]);
                    System.out.println("Computer made his move!");
                } else {
                    System.out.println("Computer has no valid moves. Player X wins!");
                    break;
                }
            }

            // Player's turn
            displayBoard();
            char currentPlayer = gameLogic.isPlayerX() ? 'X' : 'O';
            System.out.println("You are Player " + currentPlayer + ". It is your turn.");

            if (gameLogic.noPiecesLeft() || gameLogic.getAllValidMoves().isEmpty()) {
                char opponent = gameLogic.isPlayerX() ? 'O' : 'X';
                System.out
                        .println("No valid moves left for Player " + currentPlayer + ". Player " + opponent + " wins!");
                break;
            }

            // Prompt for player's move input
            int[] move = getPlayerMove();
            if (move == null) {
                System.out.println("Invalid move. Try again.");
            } else {
                gameLogic.makeMove(move[0], move[1], move[2], move[3]);
            }
        }
    }

    /**
     * Prompts the player to enter a move and validates it.
     * 
     * @return An array containing the start and end positions of the move [startX,
     *         startY, endX, endY].
     */
    private int[] getPlayerMove() {
        System.out.println("Choose a cell position of piece to be moved and the new position. e.g., 3a-4b");
        System.out.print("Enter move: ");
        String move = scanner.nextLine();
        return makeMove(move);
    }

    /**
     * Generates a move for the computer player and validates it.
     * 
     * @return An array containing the start and end positions of the move [startX,
     *         startY, endX, endY].
     */
    private int[] getComputerMove() {
        CheckersComputerPlayer computerPlayer = new CheckersComputerPlayer(gameLogic);
        return computerPlayer.generateMove();
    }

    /**
     * Converts a letter representing a column to its corresponding index.
     * 
     * @param letter The letter representing the column.
     * @return The index of the column.
     */
    private int letterToIndex(char letter) {
        return letter - 'a';
    }

    /**
     * Displays the current state of the game board.
     */
    private void displayBoard() {
        char[][] board = gameLogic.getBoard();
        System.out.println("  a b c d e f g h");
        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print("|" + board[i][j]);
            }
            System.out.println("|");
        }
    }

    /**
     * Attempts to make a move based on user input.
     * Handles NumberFormatException if the input format is incorrect.
     * 
     * @param move The move input by the user.
     * @return An array containing the start and end positions of the move [startX,
     *         startY, endX, endY], or null if the move is invalid.
     */
    private int[] makeMove(String move) {
        try {
            String[] positions = move.split("-");
            int startX = 8 - Integer.parseInt(positions[0].substring(0, 1));
            int startY = letterToIndex(positions[0].charAt(1));
            int endX = 8 - Integer.parseInt(positions[1].substring(0, 1));
            int endY = letterToIndex(positions[1].charAt(1));

            if (gameLogic.makeMove(startX, startY, endX, endY)) {
                return new int[] { startX, startY, endX, endY };
            } else {
                return null;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid move format. Please enter the move in the correct format (e.g., 3a-4b).");
            return null;
        }
    }

    /**
     * Main method to start the checkers game console.
     * 
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to use the GUI or Console-based UI? (Enter GUI or Console)");
        String uiType = scanner.nextLine();

        if ("GUI".equalsIgnoreCase(uiType)) {
            System.out.println("Do you want to play against another player (P) or a computer (C)?");
            String gameType = scanner.nextLine();
            Application.launch(CheckersGui.class, gameType);
        } else {
            CheckersTextConsole console = new CheckersTextConsole();
            console.startGame();
        }

    }
}
