package ui.menus;

import Models.AuthToken;
import Responses.APIResponse;

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
            for (String option : options) {
                System.out.println("\t" + option);
            }
            input = getUserInput(scanner);
        }
        switch (input) {
            case "create game":
            case "cg":
                return new MenuCreateGame(scanner, authToken);
            case "list games":
            case "lg":
                System.out.println("Listing games...");
                // TODO: LIST GAMES
                return this;
            case "join game":
            case "jg":
                return new MenuJoinGame(scanner, authToken);
            case "join observer":
            case "jo":
                return new MenuJoinObserver(scanner, authToken);
            case "help":
            case "h":
                System.out.println("Options:");
                for (String option : options) {
                    System.out.println("\t" + option);
                }
                return this;
            case "logout":
            case "l":
                boolean logoutSuccessful = attemptLogout();
                if (logoutSuccessful) {
                    APICaller.withAuth(null);
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
            APICaller.withAuth(authToken);
            APIResponse response = APICaller.delete("session");
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
}
