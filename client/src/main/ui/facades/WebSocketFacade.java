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

    private Session session;

    private MenuInGame parentMenu;

    public WebSocketFacade(MenuInGame parentMenu) {
        try {
            this.parentMenu = parentMenu;
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
    public void leaveGame() {
        try {
            sendMessage(parentMenu.gameID, parentMenu.authToken.username, UserGameCommand.CommandType.LEAVE, "");
            this.session.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void makeMove(String move) {
        try {
            sendMessage(parentMenu.gameID, parentMenu.authToken.username, UserGameCommand.CommandType.MAKE_MOVE, move);
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
                    this.parentMenu.chessBoard = (ChessBoardImpl) newGame.getBoard();
                    BoardDisplay.displayBoard(this.parentMenu.chessBoard, Objects.equals(this.parentMenu.orientation, "black"));
                    break;
            }
        }
    }
}
