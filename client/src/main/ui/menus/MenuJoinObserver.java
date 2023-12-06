package ui.menus;

import Models.AuthToken;
import WebSocket.ChessAction;
import com.google.gson.Gson;
import ui.facades.ServerFacade;
import ui.facades.WebSocketFacade;

import java.util.HashMap;
import java.util.Scanner;

public class MenuJoinObserver extends MenuBase {

    private boolean initialized = false;

    private boolean exited = false;

    private boolean success = false;


    private String gameName = null;

    public MenuJoinObserver(Scanner scanner, AuthToken authToken) {
        super("Join Game As Observer", "", null, scanner);
        this.authToken = authToken;
    }

    @Override
    public MenuBase run() {
        display();
        if (exited) {
            return new MenuMain(scanner, authToken);
        }
        if (success) {
            MenuBase newMenu = new MenuInGame(gameID, gameName, null, scanner, authToken);
            newMenu.webSocketFacade = new WebSocketFacade(newMenu);
            newMenu.webSocketFacade.sendMessage(gameID, authToken.username, ChessAction.CONNECT, "");
            return newMenu;
        }
        return new MenuMain(scanner, authToken);
    }

    @Override
    public void display() {
        if (!initialized) {
            System.out.println(title);
            System.out.println(subtitle);
            initialized = true;
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
        ServerFacade serverFacade = ServerFacade.getInstance();
        var response = serverFacade.joinGame(authToken.authToken, gameID, null);
        if (response.statusCode == 200) {
            System.out.println("Successfully joined game " + gameID + " as observer");
            HashMap responseValues = new Gson().fromJson(response.statusMessage, HashMap.class);
            gameName = (String) responseValues.get("gameName");
            success = true;
        } else {
            System.out.println("Failed to join game " + gameID + " as observer");
            System.out.println(response.statusMessage);
        }
    }
}
