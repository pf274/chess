package ui.menus;

import Models.AuthToken;
import Models.Game;
import Responses.APIResponse;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MenuMain extends MenuBase {
    private final AuthToken authToken;
    private boolean initialized = false;

    @Override
    public MenuBase run() {
        if (!initialized) {
            display();
            this.initialized = true;
        }
        String input = getUserInput(scanner);
        while (!isValidOption(input)) {
            System.out.println("Invalid option");
            System.out.println("Options:");
            for (int i = 0; i < options.length; i++) {
                System.out.println(i + 1 + "\t" + options[i]);
            }
            input = getUserInput(scanner);
        }
        switch (input) {
            case "create game":
            case "cg":
            case "1":
                return new MenuCreateGame(scanner, authToken);
            case "list games":
            case "lg":
            case "2":
                listGames();
                return this;
            case "join game":
            case "jg":
            case "3":
                return new MenuJoinGame(scanner, authToken);
            case "join observer":
            case "jo":
            case "4":
                return new MenuJoinObserver(scanner, authToken);
            case "help":
            case "h":
            case "5":
                System.out.println("Options:");
                for (String option : options) {
                    System.out.println("\t" + option);
                }
                return this;
            case "logout":
            case "l":
            case "6":
                boolean logoutSuccessful = attemptLogout();
                if (logoutSuccessful) {
                    return new MenuHome(scanner);
                } else {
                    return this;
                }
            case "exit":
                return null;
            default:
                System.out.println("Invalid input");
                return this;
        }
    }

    public MenuMain(Scanner scanner, AuthToken authToken) {
        super("Main Menu", "Welcome to Chess!", new String[]{
                "Create Game",
                "List Games",
                "Join Game",
                "Join Observer",
                "Help",
                "Logout",
        }, scanner);
        this.authToken = authToken;
    }

    private boolean attemptLogout() {
        System.out.println("Logging out...");
        try {
            ServerFacade serverFacade = ServerFacade.getInstance();
            APIResponse response = serverFacade.logout(authToken.authToken);
            if (response.statusCode == 200) {
                System.out.println("Logged out!");
                return true;
            } else {
                System.out.println("Failed to log out");
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void listGames() {
        try {
            ServerFacade serverFacade = ServerFacade.getInstance();
            APIResponse response = serverFacade.listGames(authToken.authToken);
            if (response.statusCode == 200) {
                HashMap responseMap = new Gson().fromJson(response.statusMessage, HashMap.class);
                if (responseMap.containsKey("games")) {
                    ArrayList games = (ArrayList) responseMap.get("games");
                    for (var game : games) {
                        LinkedTreeMap gameMap = (LinkedTreeMap) game;
                        int gameId = ((Double) gameMap.get("gameID")).intValue();
                        String gameName = (String) gameMap.get("gameName");
                        System.out.println("\t\"" + gameName + "\" Game ID: " + gameId);
                    }
                } else {
                    System.out.println("Error listing games: " + response.statusMessage);
                }
            } else {
                System.out.println("Error listing games: " + response.statusMessage);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
