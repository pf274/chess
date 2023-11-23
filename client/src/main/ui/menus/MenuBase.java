package ui.menus;

import java.util.Scanner;

public abstract class MenuBase {
    String title;

    String subtitle;

    String[] options;

    Scanner scanner;

    public MenuBase(String title, String subtitle, String[] options, Scanner scanner) {
        this.title = title;
        this.subtitle = subtitle;
        this.options = options;
        this.scanner = scanner;
    }
    public void display() {
        System.out.println(title);
        System.out.println(subtitle);
        for (String option : options) {
            System.out.println("\t" + option);
        }
    }

    public abstract MenuBase run();

    public static String getUserInput(Scanner scanner) {
        System.out.print(">>> ");
        return scanner.nextLine().trim().toLowerCase();
    }

    public boolean isValidOption(String option) {
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
}
