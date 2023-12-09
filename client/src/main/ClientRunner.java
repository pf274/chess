import ui.menus.MenuBase;
import ui.menus.MenuHome;

import java.util.Scanner;

public class ClientRunner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MenuBase.initialize(scanner);
        try {
            while (MenuBase.getInstance() != null) {
                MenuBase.getInstance().run();
            }
        } catch(Exception e) {
            // System.in has been closed
            System.out.println("Client ended unexpectedly.");
        }
    }
}
