package ui.menus;

import Responses.APIResponse;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import ui.facades.ServerFacade;
import ui.facades.WebSocketFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MenuJoinObserver extends MenuBase {

    private boolean initialized = false;

    private boolean exited = false;

    private boolean success = false;

    private int gameID;

    private final HashMap<String, String> games = new HashMap<>();

    public MenuJoinObserver(Scanner scanner) {
        super(scanner);
    }

    @Override
    public void run() {
        getListOfGames();
        display();
        if (exited) {
            MenuBase.setInstance(new MenuMain(scanner));
        } else if (success) {
            WebSocketFacade.getInstance().joinGameAsObserver(gameID);
            MenuBase.setInstance(new MenuObservingGame(gameID, scanner));
        } else {
            MenuBase.setInstance(new MenuMain(scanner));
        }
    }

    public void display() {
        if (!initialized) {
            System.out.println("Join Game as Observer\n");
            initialized = true;
        }
        printGames();
        System.out.println("Please enter a game name or game number. (\"exit\" to go back)");
        String input = "";
        while (!games.containsKey(input)) {
            input = getUserInput(scanner);
            if (input.equals("exit")) {
                exited = true;
                return;
            }
            if (games.containsKey(input)) {
                gameID = Integer.parseInt(games.get(input));
            }
        }
        success = joinGame(gameID);
    }

    private boolean joinGame(int gameID) {
        var response = ServerFacade.getInstance().joinGame(authToken.authString, gameID, null);
        if (response.statusCode == 200) {
            System.out.println("Successfully joined game " + gameID + " as observer");
            return true;
        } else {
            System.out.println("Failed to join game " + gameID + " as observer");
            System.out.println(response.statusMessage);
            return false;
        }
    }

    private void getListOfGames() {
        games.clear();
        APIResponse response = ServerFacade.getInstance().listGames(authToken.authString);
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
