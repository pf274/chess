package WebSocket;

import APIHandlers.GameDataHandler;
import Models.Game;
import Services.GameDataService;
import Services.ServiceException;
import chess.ChessMove;
import chess.ChessMoveImpl;
import chess.ChessPositionImpl;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import serverMessages.ServerMessage;
import userCommands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@WebSocket
public class WebSocketHandler {

    private final GameDataService gameDataService;

    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        HashMap data = new Gson().fromJson(message, HashMap.class);
        String username = (String) data.get("username");
        String action = (String) data.get("action");
        String details = (String) data.get("details");
        int gameID = ((Double) data.get("gameID")).intValue();
        switch (UserGameCommand.CommandType.valueOf(action.toUpperCase())) {
            case JOIN_PLAYER:
            case JOIN_OBSERVER:
                connect(username, gameID, session);
                break;
            case LEAVE:
            case RESIGN:
                disconnect(username, gameID);
                break;
//            case CHAT:
//                chatMessage(username, gameID, details);
//                break;
            case MAKE_MOVE:
                attemptMove(username, gameID, details);
        }
    }

    public void connect(String username, int gameID, Session session) throws IOException {
        connectionManager.addConnection(gameID, session, username);
        String body = MessageFormatter.prepareBodyServer(username, gameID, ServerMessage.ServerMessageType.NOTIFICATION, username + " has connected to the game.");
        connectionManager.broadcastMessageToOthers(username, gameID, body);
    }

    public void disconnect(String username, int gameID) throws IOException {
        connectionManager.removeConnection(gameID, username);
        String body = MessageFormatter.prepareBodyServer(username, gameID, ServerMessage.ServerMessageType.NOTIFICATION, username + " has disconnected from the game.");
        connectionManager.broadcastMessageToOthers(username, gameID, body);
    }

//    public void chatMessage(String username, int gameID, String message) throws IOException {
//        String body = MessageFormatter.prepareBodyClient(username, gameID, ChessActionClient.CHAT, message);
//        connectionManager.broadcastMessageToOthers(username, gameID, body);
//    }

    public void attemptMove(String username, int gameID, String move) throws IOException {
        // get the board
        try {
            Game game = gameDataService.getGame(gameID);
            ChessPositionImpl startingPosition = new ChessPositionImpl(move.charAt(1) - '1' + 1, move.charAt(0) - 'a' + 1);
            ChessPositionImpl endingPosition = new ChessPositionImpl(move.charAt(4) - '1' + 1, move.charAt(3) - 'a' + 1);
            ChessMoveImpl chessMove = new ChessMoveImpl(startingPosition, endingPosition, null);
            game.game.makeMove(chessMove);
            String body = MessageFormatter.prepareBodyServer(username, gameID, ServerMessage.ServerMessageType.LOAD_GAME, game.game.getGameAsString());
            connectionManager.broadcastMessage(gameID, body);

        } catch (ServiceException | InvalidMoveException e) {
            String body = MessageFormatter.prepareBodyServer(username, gameID, ServerMessage.ServerMessageType.ERROR, "Error: invalid move");
            connectionManager.sendMessage(username, gameID, body);
        }
    }

    public WebSocketHandler(GameDataService gameDataService) {
        this.gameDataService = gameDataService;
    }
}
