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
            case "1":
                return new MenuLogin(scanner);
            case "register":
            case "r":
            case "2":
                return new MenuRegister(scanner);
            case "help":
            case "h":
            case "3":
                System.out.println("Options:");
                for (int i = 0; i < options.length; i++) {
                    System.out.println(i + 1 + "\t" + options[i]);
                }
                return this;
            case "exit":
            case "e":
            case "4":
                return null;
            default:
                System.out.println("Invalid input");
                return this;
        }
    }
}