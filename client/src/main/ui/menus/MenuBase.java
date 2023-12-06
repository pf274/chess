package ui.menus;

import Models.AuthToken;
import ui.facades.WebSocketFacade;

import java.util.Scanner;

public abstract class MenuBase {
    String title;

    String subtitle;

    String[] options;

    Scanner scanner;

    public AuthToken authToken;

    public WebSocketFacade webSocketFacade;

    public int gameID;

    public MenuBase(String title, String subtitle, String[] options, Scanner scanner) {
        this.title = title;
        this.subtitle = subtitle;
        this.options = options;
        this.scanner = scanner;
    }
    public void display() {
        System.out.println(title);
        System.out.println(subtitle);
        for (int i = 0; i < options.length; i++) {
            System.out.println(i + 1 + "\t" + options[i]);
        }
    }

    public abstract MenuBase run();

    public static String getUserInput(Scanner scanner) {
        System.out.print(">>> ");
        return scanner.nextLine().trim().toLowerCase();
    }

    public boolean isValidOption(String option) {
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

    public boolean isNumber(String option) {
        try {
            Integer.parseInt(option);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void handleWSMessage(String message) {
        System.out.println("I got the message!" + message);
    }
}
