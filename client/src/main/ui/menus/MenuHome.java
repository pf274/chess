package ui.menus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class MenuHome extends MenuBase {
    private boolean initialized = false;

    private final String[] options = new String[4];

    public MenuHome(Scanner scanner) {
        super(scanner);
        options[0] = "Login";
        options[1] = "Register";
        options[2] = "Help";
        options[3] = "Exit";
    }

    public void run() {
        if (!initialized) {
            printOptions(options);
            this.initialized = true;
        }
        String input = getUserInput(scanner);
        while (!isValidOption(input, options)) {
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
                MenuBase.setInstance(new MenuLogin(scanner));
            case "register":
            case "r":
            case "2":
                MenuBase.setInstance(new MenuRegister(scanner));
            case "help":
            case "h":
            case "3":
                System.out.println("Options:");
                printOptions(options);
                return;
            case "exit":
            case "e":
            case "4":
                MenuBase.setInstance(null); // end the program
            default:
                System.out.println("Invalid input");
        }
    }
}