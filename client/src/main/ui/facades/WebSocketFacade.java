package ui.facades;

import WebSocket.MessageFormatter;
import chess.ChessBoardImpl;
import chess.ChessGameImpl;
import com.google.gson.Gson;
import serverMessages.ServerMessage;
import ui.BoardDisplay;
import ui.menus.MenuInGame;
import userCommands.UserGameCommand;

import javax.websocket.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Objects;

public class WebSocketFacade extends Endpoint {

    private static WebSocketFacade instance = null;
    public static WebSocketFacade getInstance() {
        if (instance == null) {
            instance = new WebSocketFacade();
        }
        return instance;
    }

    private Session session;

    public WebSocketFacade() {
        try {
            String serverUrl = "ws://localhost:8080";
            URI socketURI = new URI(serverUrl + "/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
               @Override
               public void onMessage(String message) {
                   System.out.println(message);
                   WebSocketFacade.this.handleWSMessage(message);
               }
            });
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
            this.session.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void joinGameAsObserver(int gameID, String username) {
        try {
            sendMessage(gameID, username, UserGameCommand.CommandType.JOIN_PLAYER, "");
            this.session.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void leaveGame(int gameID, String username) {
        try {
            sendMessage(gameID, username, UserGameCommand.CommandType.LEAVE, "");
            this.session.close();
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

    public void handleWSMessage(String message) {
        HashMap parsedBody = new Gson().fromJson(message, HashMap.class);
        if (parsedBody.containsKey("action")) {
            String action = (String) parsedBody.get("action");
            String details = (String) parsedBody.get("details");
            switch (ServerMessage.ServerMessageType.valueOf(action)) {
                case ERROR:
                case NOTIFICATION:
                    System.out.println(details);
                    break;
                case LOAD_GAME:
                    ChessGameImpl newGame = new ChessGameImpl();
                    newGame.loadGameFromString(details);
                    ChessBoardImpl chessBoard = (ChessBoardImpl) newGame.getBoard();
                    BoardDisplay.displayBoard(chessBoard, Objects.equals(orientation, "black"));
                    break;
            }
        }
    }
}
