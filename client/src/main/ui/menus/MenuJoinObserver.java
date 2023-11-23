package ui.menus;

import Models.AuthToken;

import java.util.Scanner;

public class MenuJoinObserver extends MenuBase {
    private final AuthToken authToken;

    public MenuJoinObserver(Scanner scanner, AuthToken authToken) {
        super("Join Game As Observer", "Please enter a game number.", null, scanner);
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
        // TODO: join a game as observer
    }
}
