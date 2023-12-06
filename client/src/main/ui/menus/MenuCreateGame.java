package ui.menus;

import Models.AuthToken;
import Responses.APIResponse;
import com.google.gson.Gson;
import ui.facades.ServerFacade;

import java.util.HashMap;
import java.util.Scanner;

public class MenuCreateGame extends MenuBase {

    public MenuCreateGame(Scanner scanner, AuthToken authToken) {
        super("Create Game", "Create a new Game!", null, scanner);
        this.authToken = authToken;
    }

    @Override
    public MenuBase run() {
        display();
        return new MenuMain(scanner, authToken);
    }

    @Override
    public void display() {
        System.out.println("Creating new game...");
        System.out.print("Game Name: (type exit to go back) ");
        String gameName = getUserInput(scanner);
        if (gameName.equals("exit") || gameName.equals("e")) {
            return;
        }
        HashMap<String, String> body = new HashMap<>();
        body.put("gameName", gameName);
        try {
            ServerFacade serverFacade = ServerFacade.getInstance();
            APIResponse response = serverFacade.createGame(gameName, authToken.authToken);
            HashMap responseMap = new Gson().fromJson(response.statusMessage, HashMap.class);
            if (responseMap.containsKey("gameID")) {
                int gameID = (int) Math.floor((Double) responseMap.get("gameID"));
                System.out.println("New Game \"" + gameName + "\" created successfully!");
                System.out.println("New Game ID: " + gameID);
            } else {
                System.out.println("Error creating game: " + responseMap.get("message"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
