package ui.menus;

import Models.AuthToken;

import java.util.Scanner;

public class MenuCreateGame extends MenuBase {
    private final AuthToken authToken;

    public MenuCreateGame(Scanner scanner, AuthToken authToken) {
        super("Create Game", "Create a new Game!", null, scanner);
        this.authToken = authToken;
    }

    @Override
    public MenuBase run() {
        display();
        return new MenuMain(scanner, authToken);
    }

    @Override
    public void display() {
        System.out.println("Creating new game...");
        // TODO: create new game
    }
}
