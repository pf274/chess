package ui.menus;

import Responses.APIResponse;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import ui.facades.ServerFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MenuMain extends MenuBase {
    private boolean initialized = false;

    private final String[] options = new String[6];

    @Override
    public void run() {
        if (!initialized) {
            printOptions(options);
            this.initialized = true;
        }
        String input = getUserInput(scanner);
        while (!isValidOption(input, options)) {
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
                MenuBase.setInstance(new MenuCreateGame(scanner));
                break;
            case "list games":
            case "lg":
            case "2":
                listGames();
                break;
            case "join game":
            case "jg":
            case "3":
                MenuBase.setInstance(new MenuJoinGame(scanner));
                break;
            case "observe game":
            case "og":
            case "4":
                MenuBase.setInstance(new MenuJoinObserver(scanner));
                break;
            case "help":
            case "h":
            case "5":
                System.out.println("Options:");
                for (String option : options) {
                    System.out.println("\t" + option);
                }
                break;
            case "logout":
            case "l":
            case "6":
            case "exit":
                attemptLogout();
                MenuBase.setAuthToken(null);
                MenuBase.setInstance(new MenuHome(scanner));
                break;
            default:
                System.out.println("Invalid input");
        }
    }

    public MenuMain(Scanner scanner) {
        super(scanner);
        options[0] = "Create Game";
        options[1] = "List Games";
        options[2] = "Join Game";
        options[3] = "Observe Game";
        options[4] = "Help";
        options[5] = "Logout";
    }

    private void attemptLogout() {
        System.out.println("Logging out...");
        try {
            ServerFacade serverFacade = ServerFacade.getInstance();
            APIResponse response = serverFacade.logout(authToken.authString);
            if (response.statusCode == 200) {
                System.out.println("Logged out!");
            } else {
                System.out.println("Failed to log out");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void listGames() {
        try {
            ServerFacade serverFacade = ServerFacade.getInstance();
            APIResponse response = serverFacade.listGames(authToken.authString);
            if (response.statusCode == 200) {
                HashMap responseMap = new Gson().fromJson(response.statusMessage, HashMap.class);
                if (responseMap.containsKey("games")) {
                    ArrayList games = (ArrayList) responseMap.get("games");
                    if (games.isEmpty()) {
                        System.out.println("\tNo games available");
                    }
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
