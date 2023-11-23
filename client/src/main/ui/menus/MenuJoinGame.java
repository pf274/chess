package ui.menus;

import Models.AuthToken;

import java.util.Scanner;

public class MenuJoinGame extends MenuBase {
    private final AuthToken authToken;

    public MenuJoinGame(Scanner scanner, AuthToken authToken) {
        super("Join Game", "Please enter a game number.", null, scanner);
        this.authToken = authToken;
    }

    @Override
    public MenuBase run() {
        display();
        return new MenuMain(scanner, authToken);
    }

    @Override
    public void display() {
        System.out.println(title);
        System.out.println(subtitle);
        // TODO: join a game
    }
}
