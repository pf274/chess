package ui.menus;

import Models.AuthToken;
import Responses.APIResponse;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import ui.facades.ServerFacade;
import ui.facades.WebSocketFacade;
import userCommands.UserGameCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MenuJoinGame extends MenuBase {
    private boolean initialized = false;

    private boolean exited = false;

    private String gameName = null;

    private String playerColor = null;

    private boolean success = false;
    int gameID;

    private HashMap<String, String> games = new HashMap<>();

    public MenuJoinGame(Scanner scanner) {
        super(scanner);
    }

    @Override
    public void run() {
        getListOfGames();
        display();
        if (exited) {
            MenuBase.setInstance(new MenuMain(scanner));
        } else if (success) {
            MenuBase.orientation = playerColor;
            WebSocketFacade.getInstance().joinGameAsPlayer(gameID, playerColor);
            MenuBase.setInstance(new MenuInGame(gameID, playerColor, scanner));
        }
    }

    public void display() {
        if (!initialized) {
            System.out.println("Join Game\n");
            this.initialized = true;
        }
        printGames();
        String input = "";
        System.out.println("Please enter a game number or game name. (\"exit\" to go back)");
        while (!games.containsKey(input.toLowerCase())) {
            input = getUserInput(scanner);
            if (input.equals("exit")) {
                exited = true;
                return;
            }
            if (games.containsKey(input)) {
                gameID = Integer.parseInt(games.get(input));
            }
        }
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
        success = joinGame(gameID);
    }

    private boolean joinGame(int gameID) {
        APIResponse response = ServerFacade.getInstance().joinGame(authToken.authToken, gameID, playerColor);
        if (response.statusCode == 200) {
            HashMap responseMap = new Gson().fromJson(response.statusMessage, HashMap.class);
            if (responseMap.containsKey("gameName")) {
                gameName = (String) responseMap.get("gameName");
                return true;
            } else {
                System.out.println("Error: " + response.statusMessage);
                return false;
            }
        } else if (response.statusCode == 403) {
            System.out.println("Error: player color already taken");
            return false;
        } else {
            System.out.println("Error: " + response.statusMessage);
            return false;
        }
    }

    private void getListOfGames() {
        games.clear();
        APIResponse response = ServerFacade.getInstance().listGames(authToken.authToken);
        HashMap responseMap = new Gson().fromJson(response.statusMessage, HashMap.class);
        ArrayList<LinkedTreeMap> games = (ArrayList<LinkedTreeMap>) responseMap.get("games");
        for (LinkedTreeMap game : games) {
            Double gameIDDouble = (Double) game.get("gameID");
            int gameIDint = gameIDDouble.intValue();
            String gameID = Integer.toString(gameIDint);
            String gameName = (String) game.get("gameName");
            this.games.put(gameName.toLowerCase(), gameID);
            this.games.put(gameID, gameID);
        }
    }

    private void printGames() {
        System.out.println("Games:");
        ArrayList<String> gameNames = new ArrayList<>();
        for (String game : games.keySet()) {
            if (!games.get(game).equals(game)) {
                gameNames.add(game);
            }
        }
        for (String gameName : gameNames) {
            System.out.println("\t" + gameName + " (" + games.get(gameName) + ")");
        }
    }
}
