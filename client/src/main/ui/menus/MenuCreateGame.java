package ui.menus;

import Models.AuthToken;
import Responses.APIResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Scanner;

public class MenuCreateGame extends MenuBase {
    private final AuthToken authToken;

    private boolean success = false;

    private String gameID;

    private String username;

    private String playerColor;

    private String gameName;

    private boolean exited = false;

    public MenuCreateGame(Scanner scanner, AuthToken authToken) {
        super("Create Game", "Create a new Game!", null, scanner);
        this.authToken = authToken;
    }

    @Override
    public MenuBase run() {
        display();
        if (exited) {
            return new MenuMain(scanner, authToken);
        }
        if (success) {
            return new MenuInGame(gameID, username, playerColor, scanner);
        } else {
            return new MenuMain(scanner, authToken);
        }
    }

    @Override
    public void display() {
        System.out.println("Creating new game...");
        System.out.print("Game Name: (type exit to go back) ");
        this.gameName = getUserInput(scanner);
        if (gameName.equals("exit") || gameName.equals("e")) {
            this.exited = true;
        }
        HashMap<String, String> body = new HashMap<>();
        body.put("gameName", gameName);
        try {
            ServerFacade serverFacade = ServerFacade.getInstance();
            APIResponse response = serverFacade.createGame(gameName, authToken.authToken);
            HashMap responseMap = new Gson().fromJson(response.statusMessage, HashMap.class);
            System.out.println(response.statusMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
