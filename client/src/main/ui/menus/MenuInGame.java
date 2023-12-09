package ui.menus;

import Models.AuthToken;
import chess.ChessBoardImpl;
import ui.BoardDisplay;
import ui.facades.ServerFacade;
import ui.facades.WebSocketFacade;

import java.util.Objects;
import java.util.Scanner;

public class MenuInGame extends MenuBase {
    private boolean initialized = false;
    private boolean exited = false;
    public String orientation;
    private String turn = "white";

    private String[] options = new String[] {
            "Move",
            "Flip Board",
            "Help",
            "Resign"
    };
    private final int gameID;
    public ChessBoardImpl chessBoard = null;
    public MenuInGame(int gameID, String playerColor, Scanner scanner) {
        super(scanner);
        this.orientation = playerColor;
        this.gameID = gameID;
    }

    @Override
    public void run() {
        display();
        if (exited) {
            ServerFacade.getInstance().leaveGame(authToken.authToken, gameID);
            WebSocketFacade.getInstance().leaveGame(gameID, authToken.username);
            MenuBase.setInstance(new MenuMain(scanner));
        }
    }

    public void display() {
        if (!initialized) {
            System.out.println("In Game\n");
            waitUntilSocketResponds();
            initialized = true;
        }
        System.out.println(turn + "'s turn!");
        String choice = "";
        while (!isValidOption(choice, options)) {
            printOptions(options);
            choice = getUserInput(scanner);
        }
        switch (choice) {
            case "move":
            case "m":
            case "1":
                makeMove();
                break;
            case "flip board":
            case "fb":
            case "2":
                // TODO: FLIP BOARD
                System.out.println("Flipping board...");
                break;
            case "help":
            case "h":
            case "3":
                printOptions(options);
                break;
            case "resign":
            case "r":
            case "4":
                // TODO: resign
                System.out.println("Resigning...");
                this.exited = true;
                break;
        }
    }

    private String getValidPosition() {
        String position = getUserInput(scanner);
        char row = !position.isEmpty() ? position.charAt(0): 'z';
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

    private void makeMove() {
        System.out.println("Specify your starting position: (e.g. \"e7\")");
        String startingPosition = getValidPosition();
        if (startingPosition.equals("exit")) {
            this.exited = true;
            return;
        }
        System.out.println("Specify your ending position: (e.g. \"f8\")");
        String endingPosition = getValidPosition();
        if (endingPosition.equals("exit")) {
            this.exited = true;
            return;
        }
        WebSocketFacade.getInstance().makeMove(startingPosition.substring(0, 2) + " " + endingPosition.substring(0, 2), gameID, authToken.username);
    }
}
