package ui.menus;

import chess.*;
import ui.BoardDisplay;
import ui.facades.ServerFacade;
import ui.facades.WebSocketFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class MenuInGame extends MenuBase {
    private boolean initialized = false;
    private boolean exited = false;
    private String[] options = new String[] {
            "Move",
            "Flip Board",
            "Highlight Legal Moves",
            "Help",
            "Resign",
            "Leave",
    };
    private final int gameID;
    public MenuInGame(int gameID, String playerColor, Scanner scanner) {
        super(scanner);
        MenuBase.orientation = playerColor;
        MenuBase.playerColor = playerColor;
        this.gameID = gameID;
    }

    @Override
    public void run() {
        display();
        if (exited) {
            ServerFacade.getInstance().leaveGame(authToken.authToken, gameID);
            WebSocketFacade.getInstance().leaveGame(gameID);
            MenuBase.setInstance(new MenuMain(scanner));
        }
    }

    public void display() {
        if (!initialized) {
            System.out.println("In Game\n");
            waitUntilGameLoads();
            initialized = true;
        }
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
                flipBoard();
                break;
            case "highlight legal moves":
            case "hlm":
            case "3":
                highlightLegalMoves();
                break;
            case "help":
            case "h":
            case "4":
                printOptions(options);
                break;
            case "resign":
            case "r":
            case "5":
                resign();
                break;
            case "leave":
            case "l":
            case "6":
                WebSocketFacade.getInstance().leaveGame(gameID);
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
        try {
            if (!isMyTurn()) {
                System.out.println("It's not your turn!");
                return;
            }
            System.out.println("Specify your starting position: (e.g. \"e7\")");
            String start = getValidPosition();
            if (start.equals("exit")) {
                return;
            }
            System.out.println("Specify your ending position: (e.g. \"f8\")");
            String end = getValidPosition();
            if (end.equals("exit")) {
                return;
            }
            ChessPiece.PieceType promotionPiece = null;
            ChessPositionImpl startingPosition = new ChessPositionImpl(start.charAt(1) - '1' + 1, start.charAt(0) - 'a' + 1);
            ChessPositionImpl endingPosition = new ChessPositionImpl(end.charAt(1) - '1' + 1, end.charAt(0) - 'a' + 1);
            ChessPiece movingPiece = MenuBase.chessGame.getBoard().getPiece(startingPosition);
            if (movingPiece == null) {
                System.out.println("There is no piece at that position.");
                return;
            }
            if (!movingPiece.getTeamColor().toString().toLowerCase().equals(MenuBase.playerColor)) {
                System.out.println("That piece is not yours.");
                return;
            }
            if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN && (endingPosition.getRow() == 1 || endingPosition.getRow() == 8)) {
                System.out.println("Promote your pawn to a queen, knight, bishop, or rook? (q/n/b/r)");
                String input = getUserInput(scanner);
                while (!input.equals("q") && !input.equals("n") && !input.equals("b") && !input.equals("r")) {
                    System.out.println("Invalid input. Try again.");
                    input = getUserInput(scanner);
                }
                promotionPiece = switch (input) {
                    case "q" -> ChessPiece.PieceType.QUEEN;
                    case "n" -> ChessPiece.PieceType.KNIGHT;
                    case "b" -> ChessPiece.PieceType.BISHOP;
                    default -> ChessPiece.PieceType.ROOK;
                };
            }
            ChessMove move = new ChessMoveImpl(startingPosition, endingPosition, promotionPiece);
            WebSocketFacade.getInstance().makeMove(move, gameID);
            waitUntilSocketResponds();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void resign() {
        try {
            System.out.println("Are you sure you want to resign? (y/n)");
            String input = getUserInput(scanner);
            while (!input.equals("y") && !input.equals("n")) {
                System.out.println("Invalid input. Try again.");
                input = getUserInput(scanner);
            }
            if (input.equals("y")) {
                WebSocketFacade.getInstance().resign(gameID);
                waitUntilSocketResponds();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void highlightLegalMoves() {
        try {
            System.out.println("Highlighting legal moves...");
            System.out.println("Please specify a starting position: (e.g. \"e7\")");
            String startingPosition = getValidPosition();
            if (startingPosition.equals("exit")) {
                return;
            }
            int row = startingPosition.charAt(1) - '1' + 1;
            int col = startingPosition.charAt(0) - 'a' + 1;
            ChessPositionImpl startingPos = new ChessPositionImpl(row, col);
            ChessPieceImpl pieceAtPosition = (ChessPieceImpl) MenuBase.chessGame.getBoard().getPiece(startingPos);
            if (pieceAtPosition == null) {
                System.out.println("There is no piece at that position.");
                return;
            }
            if (!pieceAtPosition.getTeamColor().toString().toLowerCase().equals(MenuBase.playerColor)) {
                System.out.println("That piece is not yours.");
                return;
            }
            try {
                Collection<ChessMove> validMoves = MenuBase.chessGame.validMoves(startingPos);
                BoardDisplay.displayBoardHighlighted(validMoves);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void flipBoard() {
        try {
            System.out.println("Flipping board...");
            if (MenuBase.orientation.equals("white")) {
                MenuBase.orientation = "black";
            } else {
                MenuBase.orientation = "white";
            }
            BoardDisplay.displayBoard();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
