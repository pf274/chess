package ui.menus;

import ui.BoardDisplay;
import ui.facades.ServerFacade;
import ui.facades.WebSocketFacade;

import java.util.Scanner;

public class MenuObservingGame extends MenuBase {
    private boolean initialized = false;
    private boolean exited = false;
    private String[] options = new String[] {
            "Flip Board",
            "Help",
            "Leave"
    };
    private final int gameID;
    public MenuObservingGame(int gameID, Scanner scanner) {
        super(scanner);
        MenuBase.orientation = "white";
        MenuBase.playerColor = null;
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
        String currentTurn = MenuBase.chessGame.getTeamTurn().toString().toLowerCase();
        System.out.println(currentTurn + "'s turn.");
        String choice = "";
        while (!isValidOption(choice, options)) {
            printOptions(options);
            choice = getUserInput(scanner);
        }
        switch (choice) {
            case "flip board":
            case "fb":
            case "1":
                System.out.println("Flipping board...");
                if (MenuBase.orientation.equals("white")) {
                    MenuBase.orientation = "black";
                } else {
                    MenuBase.orientation = "white";
                }
                BoardDisplay.displayBoard();
                break;
            case "help":
            case "h":
            case "2":
                printOptions(options);
                break;
            case "leave":
            case "l":
            case "3":
                WebSocketFacade.getInstance().leaveGame(gameID);
                this.exited = true;
                break;
        }
    }
}
