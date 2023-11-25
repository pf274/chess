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

    public MenuJoinGame(Scanner scanner, AuthToken authToken) {
        super("Join Game", "Please enter a game number.", null, scanner);
        this.authToken = authToken;
    }

    @Override
    public MenuBase run() {
        display();
        if (exited) {
            return new MenuMain(scanner, authToken);
        }
        return new MenuInGame(gameID, gameName, playerColor, scanner, authToken);
    }

    @Override
    public void display() {
        if (!initialized) {
            System.out.println(title);
            System.out.println(subtitle);
            this.initialized = true;
        }
        String input = getUserInput(scanner);
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
            } else {
                System.out.println("Error: " + response.statusMessage);
            }
        } else {
            System.out.println("Error: " + response.statusMessage);
        }
    }
}
