package ui.menus;

import Models.AuthToken;
import Responses.APIResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Scanner;

public class MenuJoinGame extends MenuBase {
    private final AuthToken authToken;
    private boolean initialized = false;

    private boolean exited = false;

    private int gameID = 0;

    private String gameName = null;

    private String playerColor = null;

    private boolean success = false;

    public MenuJoinGame(Scanner scanner, AuthToken authToken) {
        super("Join Game", "", null, scanner);
        this.authToken = authToken;
    }

    @Override
    public MenuBase run() {
        display();
        if (exited) {
            return new MenuMain(scanner, authToken);
        }
        if (success) {
            return new MenuInGame(gameID, gameName, playerColor, scanner, authToken);
        }
        return this;
    }

    @Override
    public void display() {
        if (!initialized) {
            System.out.println(title);
            System.out.println(subtitle);
            this.initialized = true;
        }
        System.out.println("Please enter a game number. (\"exit\" to go back)");
        String input = getUserInput(scanner);
        if (input.equals("exit")) {
            exited = true;
            return;
        }
        while (!isNumber(input)) {
            System.out.println("Invalid input");
            System.out.println("Please enter a game number. (\"exit\" to go back)");
            input = getUserInput(scanner);
            if (input.equals("exit")) {
                exited = true;
                return;
            }
        }
        gameID = Integer.parseInt(input);
        System.out.println("Please enter a color (white/black):");
        input = getUserInput(scanner);
        while (!input.equals("w") && !input.equals("b") && !input.equals("white") && !input.equals("black")) {
            System.out.println("Invalid input");
            System.out.println("Please enter a color (white/black):");
            input = getUserInput(scanner);
        }
        if (input.equals("w")) {
            input = "white";
        } else if (input.equals("b")) {
            input = "black";
        }
        playerColor = input;
        System.out.println("Joining game " + gameID + "...");
        ServerFacade serverFacade = new ServerFacade();
        APIResponse response = serverFacade.joinGame(authToken.authToken, gameID, playerColor);
        if (response.statusCode == 200) {
            HashMap responseMap = new Gson().fromJson(response.statusMessage, HashMap.class);
            if (responseMap.containsKey("gameName")) {
                gameName = (String) responseMap.get("gameName");
                success = true;
            } else {
                System.out.println("Error: " + response.statusMessage);
            }
        } else if (response.statusCode == 403) {
            System.out.println("Error: player color already taken");
        } else {
            System.out.println("Error: " + response.statusMessage);
        }
    }
}
