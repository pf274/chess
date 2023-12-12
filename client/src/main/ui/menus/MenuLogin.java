package ui.menus;

import Models.AuthToken;
import Responses.APIResponse;
import com.google.gson.Gson;
import ui.facades.ServerFacade;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuLogin extends MenuBase {
    private String username;
    private String password;
    private boolean exited = false;
    private boolean loggedIn = false;
    public MenuLogin(Scanner scanner) {
        super(scanner);
    }

    public void run() {
        display();
        if (exited) {
            System.out.println("Going back...");
            MenuBase.setInstance(new MenuHome(scanner));
        } else if (loggedIn) {
            System.out.println("Logged in!");
            MenuBase.setInstance(new MenuMain(scanner));
        }
    }

    public void display() {
        System.out.println("Login\n");
        System.out.println("Username: (type exit to go back) ");
        this.username = getUserInput(scanner);
        if (username.equals("exit") || username.equals("e")) {
            this.exited = true;
            return;
        }
        System.out.println("Password: (type exit to go back) ");
        this.password = getUserInput(scanner);
        if (password.equals("exit") || password.equals("e")) {
            this.exited = true;
            return;
        }
        attemptLogin();
    }

    private void attemptLogin() {
        System.out.println("Logging in...");
        try {
            ServerFacade serverFacade = ServerFacade.getInstance();
            APIResponse response = serverFacade.login(username, password);
            HashMap responseMap = new Gson().fromJson(response.statusMessage, HashMap.class);
            if (responseMap.containsKey("message")) {
                System.out.println(responseMap.get("message"));
            } else if (responseMap.containsKey("error")) {
                System.out.println(responseMap.get("error"));
            } else {
                AuthToken newAuthToken = new AuthToken(username);
                newAuthToken.authString = responseMap.get("authToken").toString();
                MenuBase.setAuthToken(newAuthToken);
                loggedIn = true;
            }
        } catch (Exception e) {
            System.out.println("Invalid login credentials.");
        }
    }
}
