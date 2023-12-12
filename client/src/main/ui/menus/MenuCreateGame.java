package ui.menus;

import Responses.APIResponse;
import com.google.gson.Gson;
import ui.facades.ServerFacade;

import java.util.HashMap;
import java.util.Scanner;

public class MenuCreateGame extends MenuBase {

    public MenuCreateGame(Scanner scanner) {
        super(scanner);
    }

    @Override
    public void run() {
        display();
        MenuBase.setInstance(new MenuMain(scanner));
    }

    public void display() {
        System.out.println("Creating new game...");
        System.out.print("Game Name: (type exit to go back) ");
        String gameName = getUserInput(scanner);
        if (gameName.equals("exit") || gameName.equals("e")) {
            return;
        }
        try {
            ServerFacade serverFacade = ServerFacade.getInstance();
            APIResponse response = serverFacade.createGame(gameName, MenuBase.authToken.authString);
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
