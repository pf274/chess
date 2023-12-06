package WebSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.HashMap;

@WebSocket
public class WebSocketHandler {

    private static WebSocketHandler instance = new WebSocketHandler();

    public static WebSocketHandler getInstance() {
        if (instance == null) {
            instance = new WebSocketHandler();
        }
        return instance;
    }

    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        HashMap data = new Gson().fromJson(message, HashMap.class);
        String username = (String) data.get("username");
        String action = (String) data.get("action");
        String details = (String) data.get("details");
        int gameID = ((Double) data.get("gameID")).intValue();
        switch (ChessAction.valueOf(action.toUpperCase())) {
            case CONNECT:
                connect(username, gameID, session);
                break;
            case DISCONNECT:
                disconnect(username, gameID);
                break;
            case CHAT:
                chatMessage(username, gameID, details);
                break;
        }
    }

    public void connect(String username, int gameID, Session session) throws IOException {
        connectionManager.addConnection(gameID, session, username);
        String body = MessageFormatter.prepareBody(username, gameID, ChessAction.CONNECT, username + " has connected to the game.");
        connectionManager.broadcastMessage(username, gameID, body);
    }

    public void disconnect(String username, int gameID) throws IOException {
        connectionManager.removeConnection(gameID, username);
        String body = MessageFormatter.prepareBody(username, gameID, ChessAction.DISCONNECT, username + " has disconnected from the game.");
        connectionManager.broadcastMessage(username, gameID, body);
    }

    public void chatMessage(String username, int gameID, String message) throws IOException {
        String body = MessageFormatter.prepareBody(username, gameID, ChessAction.CHAT, message);
        connectionManager.broadcastMessage(username, gameID, body);
    }
}
