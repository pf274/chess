package ui.menus;

import Models.AuthToken;
import Responses.APIResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class MenuRegister extends MenuBase {
    private String username;
    private String password;

    private String reenteredPassword;

    private AuthToken authToken;
    private boolean exited = false;
    private boolean loggedIn = false;
    private String email;

    public MenuRegister(Scanner scanner) {
        super("Register", "Please enter your username and password", null, scanner);
    }

    public MenuBase run() {
        while (!exited && !loggedIn) {
            display();
        }
        if (exited) {
            System.out.println("Going back...");
            return new MenuHome(scanner);
        }
        if (!Objects.equals(password, reenteredPassword)) {
            System.out.println("Passwords do not match!");
            return this;
        }
        System.out.println("Registered and logged in!");
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
        System.out.print("Reenter your password: (type exit to go back) ");
        this.reenteredPassword = getUserInput(scanner);
        if (reenteredPassword.equals("exit") || reenteredPassword.equals("e")) {
            this.exited = true;
            return;
        }
        if (!Objects.equals(password, reenteredPassword)) {
            System.out.println("Passwords do not match!");
            return;
        }
        System.out.print("Email: (type exit to go back) ");
        this.email = getUserInput(scanner);
        if (email.equals("exit") || email.equals("e")) {
            this.exited = true;
            return;
        }
        // TODO: attempt register
        attemptRegister();
    }

    private void attemptRegister() {
        System.out.println("Registering...");
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("email", email);
        try {
            ServerFacade serverFacade = ServerFacade.getInstance();
            APIResponse response = serverFacade.register(username, password, email);
            HashMap responseMap = new Gson().fromJson(response.statusMessage, HashMap.class);
            if (responseMap.containsKey("message")) {
                System.out.println(responseMap.get("message"));
            } else {
                authToken = new AuthToken(username);
                authToken.authToken = responseMap.get("authToken").toString();
                loggedIn = true;
                System.out.println("Logged in!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
