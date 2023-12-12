package ui.menus;

import Models.AuthToken;
import Responses.APIResponse;
import com.google.gson.Gson;
import ui.facades.ServerFacade;

import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class MenuRegister extends MenuBase {
    private String username;
    private String password;

    private boolean loggedIn = false;
    private String email;

    public MenuRegister(Scanner scanner) {
        super(scanner);
    }

    public void run() {
        display();
        if (loggedIn) {
            System.out.println("Registered and logged in!");
            MenuBase.setInstance(new MenuMain(scanner));
        } else {
            System.out.println("Going back...");
            MenuBase.setInstance(new MenuHome(scanner));
        }
    }

    public void display() {
        System.out.println("Register\n");
        System.out.print("Username: (type exit to go back) ");
        this.username = getUserInput(scanner);
        if (username.equals("exit") || username.equals("e")) {
            return;
        }
        System.out.print("Password: (type exit to go back) ");
        this.password = getUserInput(scanner);
        if (password.equals("exit") || password.equals("e")) {
            return;
        }
        System.out.print("Reenter your password: (type exit to go back) ");
        String reenteredPassword = getUserInput(scanner);
        if (reenteredPassword.equals("exit") || reenteredPassword.equals("e")) {
            return;
        }
        if (!Objects.equals(password, reenteredPassword)) {
            System.out.println("Passwords do not match!");
            return;
        }
        System.out.print("Email: (type exit to go back) ");
        this.email = getUserInput(scanner);
        if (email.equals("exit") || email.equals("e")) {
            return;
        }
        attemptRegister();
    }

    private void attemptRegister() {
        System.out.println("Registering...");
        try {
            APIResponse response = ServerFacade.getInstance().register(username, password, email);
            HashMap responseMap = new Gson().fromJson(response.statusMessage, HashMap.class);
            if (responseMap.containsKey("message")) {
                System.out.println(responseMap.get("message"));
            } else {
                AuthToken newAuthToken = new AuthToken(username);
                newAuthToken.authString = responseMap.get("authToken").toString();
                MenuBase.setAuthToken(newAuthToken);
                loggedIn = true;
                System.out.println("Logged in!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
