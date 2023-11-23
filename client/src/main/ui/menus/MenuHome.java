package ui.menus;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class MenuHome extends MenuBase {
    private boolean initialized = false;

    public MenuHome(Scanner scanner) {
        super("Main Menu", "Welcome to Chess!", new String[]{
                "Login",
                "Register",
                "Help",
                "Exit"
        }, scanner);
    }

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
            case "login":
            case "l":
                return new MenuLogin(scanner);
            case "register":
            case "r":
                return new MenuRegister(scanner);
            case "help":
            case "h":
                System.out.println("Options:");
                for (String option : options) {
                    System.out.println("\t" + option);
                }
                return this;
            case "exit":
            case "e":
                return null;
            default:
                System.out.println("Invalid input");
                return this;
        }
    }
}