package ui.menus;

import Models.AuthToken;
import chess.ChessBoardImpl;
import ui.BoardDisplay;
import ui.facades.ServerFacade;

import java.util.Objects;
import java.util.Scanner;

public class MenuInGame extends MenuBase {
    private boolean initialized = false;
    private boolean exited = false;
    public String orientation;
    private String turn = "white";
    public ChessBoardImpl chessBoard = null;
    public MenuInGame(int gameID, String gameName, String playerColor, Scanner scanner, AuthToken authToken) {
        super(
                "Playing \"" + gameName + "\" Game ID: " + gameID,
                playerColor != null ? "Playing as: " + playerColor : "Observing",
                new String[] {
                    "Move",
                    "Flip Board",
                    "Help",
                    "Resign",
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
        BoardDisplay.displayBoard(chessBoard, Objects.equals(orientation, "black"));
        display();
        if (!exited) {
            return this;
        } else {
            ServerFacade serverFacade = ServerFacade.getInstance();
            serverFacade.leaveGame(authToken.authToken, gameID);
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
                System.out.println("Specify your starting position: (e.g. \"e7\")");
                String startingPosition = getValidPosition();
                System.out.println("Specify your ending position: (e.g. \"f8\")");
                String endingPosition = getValidPosition();
                webSocketFacade.makeMove(startingPosition.substring(0, 2) + " " + endingPosition.substring(0, 2));
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

    private String getValidPosition() {
        String position = getUserInput(scanner);
        char row = position.length() >= 1 ? position.charAt(0): 'z';
        char col = position.length() >= 2 ? position.charAt(1): '0';
        while (position.length() < 2 || row < 'a' || row > 'h' || col < '1' || col > '8') {
            System.out.println("Invalid position. Try again.");
            position = getUserInput(scanner);
            row = position.charAt(0);
            col = position.charAt(1);
            if (position.equals("exit")) {
                return "exit";
            }
        }
        return position;
    }
}
