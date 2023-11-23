package ui;

import ui.menus.MenuBase;
import ui.menus.MenuHome;

import java.awt.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientRunner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean completed = false;
        MenuBase menu = new MenuHome(scanner);
        try {
            while (menu != null) {
                menu = menu.run();
            }
        } catch(IllegalStateException | NoSuchElementException e) {
            // System.in has been closed
            System.out.println("System.in was closed; exiting");
        }
    }


}
