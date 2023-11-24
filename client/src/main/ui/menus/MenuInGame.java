package ui.menus;

import java.util.Scanner;

public class MenuInGame extends MenuBase {

    public MenuInGame(String gameID, String username, String playerColor, Scanner scanner) {
        super("In Game" + gameID, username + " is " + playerColor, null, scanner);
    }

    @Override
    public MenuBase run() {
        return null;
    }
}
