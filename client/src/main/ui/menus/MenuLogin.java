package ui.menus;

import Models.AuthToken;
import Responses.APIResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuLogin extends MenuBase {
    private String username;
    private String password;
    private AuthToken authToken;
    private boolean exited = false;
    private boolean loggedIn = false;
    public MenuLogin(Scanner scanner) {
        super("Login", "Please enter your username and password", null, scanner);
    }

    public MenuBase run() {
        while (!exited && !loggedIn) {
            display();
        }
        if (exited) {
            System.out.println("Going back...");
            return new MenuHome(scanner);
        }
        return new MenuMain(scanner, authToken);
    }

    @Override
    public void display() {
        System.out.println(title);
        System.out.println(subtitle);
        System.out.print("Username: (type exit to go back) ");
        this.username = getUserInput(scanner);
        if (username.equals("exit") || username.equals("e")) {
            this.exited = true;
            return;
        }
        System.out.print("Password: (type exit to go back) ");
        this.password = getUserInput(scanner);
        if (password.equals("exit") || password.equals("e")) {
            this.exited = true;
            return;
        }
        attemptLogin();
    }

    private void attemptLogin() {
        System.out.println("Logging in...");
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        try {
            APIResponse response = APICaller.post("session", body);
            HashMap responseMap = new Gson().fromJson(response.statusMessage, HashMap.class);
            if (responseMap.containsKey("message")) {
                System.out.println(responseMap.get("message"));
            } else if (responseMap.containsKey("error")) {
                System.out.println(responseMap.get("error"));
            } else {
                authToken = new AuthToken(username);
                authToken.authToken = responseMap.get("authToken").toString();
                APICaller.withAuth(authToken);
                loggedIn = true;
                System.out.println("Logged in!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
