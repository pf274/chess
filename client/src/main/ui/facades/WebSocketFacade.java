package ui.facades;

import WebSocket.MessageFormatter;
import chess.ChessBoardImpl;
import chess.ChessGame;
import chess.ChessGameImpl;
import com.google.gson.Gson;
import serverMessages.ServerMessage;
import ui.BoardDisplay;
import ui.EscapeSequences;
import ui.menus.MenuBase;
import ui.menus.MenuInGame;
import userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Objects;

public class WebSocketFacade extends Endpoint {

    private static WebSocketFacade instance = null;
    public static WebSocketFacade getInstance() {
        if (instance == null) {
            instance = new WebSocketFacade();
        }
        if (!instance.session.isOpen()) {
            instance.session = instance.getNewSession();
        }
        return instance;
    }

    private Session session;

    public WebSocketFacade() {
        session = getNewSession();
    }

    private Session getNewSession(){
        try {
            String serverUrl = "ws://localhost:8080";
            URI socketURI = new URI(serverUrl + "/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            Session newSession = container.connectToServer(this, socketURI);
            newSession.addMessageHandler(new MessageHandler.Whole<String>() { // DON'T change this to a lambda function. IntelliJ is wrong.
                @Override
                public void onMessage(String message) {
                    handleWSMessage(message);
                }
            });
            return newSession;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to initialize websocket");
            return null;
        }
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendMessage(int gameID, String username, UserGameCommand.CommandType action, String details) {
        try {
            String body = MessageFormatter.prepareBodyClient(username, gameID, action, details);
            this.session.getBasicRemote().sendText(body);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void joinGameAsPlayer(int gameID, String username, String teamColor) {
        try {
            sendMessage(gameID, username, UserGameCommand.CommandType.JOIN_PLAYER, teamColor);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void joinGameAsObserver(int gameID, String username) {
        try {
            sendMessage(gameID, username, UserGameCommand.CommandType.JOIN_PLAYER, "");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void leaveGame(int gameID, String username) {
        try {
            sendMessage(gameID, username, UserGameCommand.CommandType.LEAVE, "");
            this.session.close();
            WebSocketFacade.instance = null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void makeMove(String move, int gameID, String username) {
        try {
            sendMessage(gameID, username, UserGameCommand.CommandType.MAKE_MOVE, move);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void resign(int gameID, String username) {
        try {
            sendMessage(gameID, username, UserGameCommand.CommandType.RESIGN, "");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleWSMessage(String message) {
        HashMap parsedBody = new Gson().fromJson(message, HashMap.class);
        if (parsedBody.containsKey("action")) {
            String action = (String) parsedBody.get("action");
            String details = (String) parsedBody.get("details");
            switch (ServerMessage.ServerMessageType.valueOf(action)) {
                case ERROR:
                case NOTIFICATION:
                    if (details.contains("resigned")) {
                        MenuBase.chessGame.gameOver = true;
                    }
                    System.out.println(details);
                    System.out.print(">>> ");
                    break;
                case LOAD_GAME:
                    ChessGameImpl loadedGame = new ChessGameImpl();
                    loadedGame.loadGameFromString(details);
                    MenuBase.chessGame = loadedGame;
                    BoardDisplay.displayBoard();
                    if (MenuBase.chessGame.isInCheckmate(ChessGame.TeamColor.valueOf(MenuBase.playerColor.toUpperCase()))) {
                        System.out.println("Checkmate!");
                    } else if (MenuBase.chessGame.isInCheck(ChessGame.TeamColor.valueOf(MenuBase.playerColor.toUpperCase()))) {
                        System.out.println("Check!");
                    } else if (MenuBase.chessGame.isInStalemate(ChessGame.TeamColor.valueOf(MenuBase.playerColor.toUpperCase()))) {
                        System.out.println("Stalemate!");
                    }
                    if (MenuBase.getInstance().isMyTurn()) {
                        System.out.println("Your turn!");
                    } else {
                        System.out.println("Opponent's turn.");
                    }
                    System.out.print(">>> ");
                    break;
            }
        }
        MenuBase.socketResponded = true;
    }
}
