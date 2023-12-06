package ui.menus;

import Models.AuthToken;
import chess.ChessBoardImpl;
import ui.BoardDisplay;

import java.util.Scanner;

public class MenuInGame extends MenuBase {
    private boolean initialized = false;
    private boolean exited = false;
    private ChessBoardImpl chessBoard = null;
    private String orientation = "white";
    private String turn = "white";
    public MenuInGame(int gameID, String gameName, String playerColor, Scanner scanner, AuthToken authToken) {
        super(
                "Playing \"" + gameName + "\" Game ID: " + gameID,
                playerColor != null ? "Playing as: " + playerColor : "Observing",
                new String[] {
//                    "Move",
//                    "Flip Board",
//                    "Help",
//                    "Resign",
                    "Exit"
                },
                scanner);
        this.orientation = playerColor;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    @Override
    public MenuBase run() {
        chessBoard = new ChessBoardImpl();
        chessBoard.resetBoard();
        System.out.println("White's View: ");
        BoardDisplay.displayBoard(chessBoard, false);
        System.out.println("\nBlack's View: ");
        BoardDisplay.displayBoard(chessBoard, true);
        display();
        if (!exited) {
            return this;
        } else {
            webSocketFacade.leaveGame();
            return new MenuMain(scanner, authToken);
        }

    }

    @Override
    public void display() {
        if (!initialized) {
            System.out.println(title);
            System.out.println(subtitle);
            initialized = true;
        }
        System.out.println(turn + "'s turn!");
        for (int i = 0; i < options.length; i++) {
            System.out.println(i + 1 + "\t" + options[i]);
        }
        String choice = getUserInput(scanner);
        while (!isValidOption(choice)) {
            for (String option : options) {
                System.out.println("\t" + option);
            }
            choice = getUserInput(scanner);
        }
        switch (choice) {
            case "move":
            case "m":
                // TODO: MOVE
                System.out.println("Moving...");
                break;
            case "flip board":
            case "fb":
                // TODO: FLIP BOARD
                System.out.println("Flipping board...");
                break;
            case "help":
            case "h":
                System.out.println("Options:");
                for (int i = 0; i < options.length; i++) {
                    System.out.println(i + 1 + "\t" + options[i]);
                }
                break;
            case "resign":
            case "r":
                // TODO: resign
                System.out.println("Resigning...");
                break;
            case "exit":
            case "e":
            case "1":
                this.exited = true;
                break;
        }
    }
}
