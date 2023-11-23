package ui.menus;

import Models.AuthToken;

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
        System.out.println("Logged in!");
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
        System.out.println("Logging in...");
        // TODO: attempt login
        loggedIn = true;
        authToken = new AuthToken(username);
    }
}
