package ui.menus;

import Models.AuthToken;
import ui.facades.WebSocketFacade;

import java.util.ArrayList;
import java.util.Scanner;

public abstract class MenuBase {

    Scanner scanner;

    public static AuthToken authToken;

    public WebSocketFacade webSocketFacade;

    private static MenuBase instance;

    public static String orientation = "white";

    public static boolean socketResponded = false;

    public static MenuBase getInstance() {
        return instance;
    }

    public static void initialize(Scanner scanner) {
        instance = new MenuHome(scanner);
    }

    public static void setInstance(MenuBase newInstance) {
        instance = newInstance;
    }
    public MenuBase(Scanner scanner) {
        this.scanner = scanner;
    }

    protected static void setAuthToken(AuthToken authToken) {
        MenuBase.authToken = authToken;
    }

    public abstract void run();

    public static String getUserInput(Scanner scanner) {
        System.out.print(">>> ");
        return scanner.nextLine().trim().toLowerCase();
    }

    public boolean isValidOption(String option, String[] options) {
        if (isNumber(option)) {
            int optionNum = Integer.parseInt(option);
            if (optionNum > 0 && optionNum <= options.length) {
                return true;
            }
        }
        for (String opt : options) {
            // get abbreviated versions
            String[] optParts = opt.split(" ");
            StringBuilder sbOpt = new StringBuilder();
            for (String part : optParts) {
                sbOpt.append(part.toLowerCase().charAt(0));
            }
            String abbrOpt = sbOpt.toString();
            // check if option is valid
            if (opt.toLowerCase().equals(option) || abbrOpt.equals(option)) {
                return true;
            }
        }
        return false;
    }

    public void printOptions(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println(i + 1 + "\t" + options[i]);
        }
    }

    public boolean isNumber(String option) {
        try {
            Integer.parseInt(option);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public synchronized void waitUntilSocketResponds() {
        socketResponded = false;
        while (!socketResponded) {
            try {
                wait(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
