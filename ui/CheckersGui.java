package ui;

import core.CheckersLogic;
import core.CheckersComputerPlayer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Represents the graphical user interface for a checkers game,
 * allowing users to play against another human or a computer opponent.
 * 
 * @author Daria Berezianska
 * @version 1.3
 */
public class CheckersGui extends Application {
    private CheckersLogic gameLogic;
    private CheckersComputerPlayer computerPlayer;
    private Button[][] buttons = new Button[8][8];
    private boolean playWithComputer;
    private int[] selectedPiece;

    /**
     * Starts and displays the checkers game GUI, setting up the game based on user
     * input.
     * 
     * @param primaryStage The primary stage for this application, onto which
     *                     the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            String gameType = getParameters().getRaw().get(0);
            playWithComputer = "C".equalsIgnoreCase(gameType);
            gameLogic = new CheckersLogic();
            if (playWithComputer) {
                computerPlayer = new CheckersComputerPlayer(gameLogic);
            }

            GridPane grid = new GridPane();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Button btn = new Button();
                    btn.setMinWidth(60);
                    btn.setMinHeight(60);
                    final int x = i;
                    final int y = j;
                    btn.setOnAction(event -> handleButtonClick(x, y));
                    buttons[i][j] = btn;
                    grid.add(btn, j, i);
                }
            }
            updateBoard();
            Scene scene = new Scene(grid, 480, 480);
            primaryStage.setTitle("Checkers Game");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Initialization Error");
            alert.setHeaderText("Error occurred during the initialization of the game");
            alert.setContentText("Details: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Handles button clicks for game moves, selecting and moving pieces on the
     * board.
     * 
     * @param x the row index of the button that was clicked.
     * @param y the column index of the button that was clicked.
     */
    private void handleButtonClick(int x, int y) {
        if (playWithComputer && !gameLogic.isPlayerX()) {
            computerMove();
            return;
        }

        if (selectedPiece == null) {
            if (gameLogic.getBoard()[x][y] != '_' && gameLogic.isPieceBelongsToCurrentPlayer(x, y)) {
                selectedPiece = new int[] { x, y };
            }
        } else {
            if (gameLogic.makeMove(selectedPiece[0], selectedPiece[1], x, y)) {
                selectedPiece = null;
                updateBoard();
                if (playWithComputer && !gameLogic.isPlayerX()) {
                    computerMove();
                }
                checkForGameOver();
            } else {
                selectedPiece = null; // Clear the selection if move is invalid
            }
        }
    }

    /**
     * Executes a move for the computer player.
     */
    private void computerMove() {
        int[] move = computerPlayer.generateMove();
        if (move != null) {
            gameLogic.makeMove(move[0], move[1], move[2], move[3]);
            updateBoard();
            checkForGameOver();
        }
    }

    /**
     * Updates the game board by refreshing the text on all buttons.
     */
    private void updateBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = gameLogic.getBoard()[i][j];
                // Set the button text to an empty string if the square is empty, else set to
                // the piece
                buttons[i][j].setText(piece == '_' ? "" : String.valueOf(piece).toUpperCase());
            }
        }
    }

    /**
     * Checks if the game is over and displays the winner if so.
     */
    private void checkForGameOver() {
        if (gameLogic.noPiecesLeft() || gameLogic.getAllValidMoves().isEmpty()) {
            char winner = gameLogic.isPlayerX() ? 'O' : 'X';
            displayWinner(winner);
        }
    }

    /**
     * Displays an alert to the user indicating who won the game.
     * 
     * @param winner The character representing the winning player.
     */
    private void displayWinner(char winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Winner: " + winner);
        alert.setContentText("Congratulations! Player " + winner + " has won the game!");
        alert.showAndWait();
        System.exit(0);

    }

    /**
     * Main method to start the checkers game GUI.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}