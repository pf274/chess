package WebSocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionManager {
    HashMap<Integer, ArrayList<Connection>> gameConnections = new HashMap<>();
    public void addConnection(int gameID, Session session, String username) {
        Connection newConnection = new Connection(session, username, gameID);
        if (gameConnections.containsKey(gameID)) {
            gameConnections.get(gameID).add(newConnection);
        } else {
            ArrayList<Connection> connections = new ArrayList<>();
            connections.add(newConnection);
            gameConnections.put(gameID, connections);
        }
    }

    public void removeConnection(int gameID, String username) {
        if (gameConnections.containsKey(gameID)) {
            for (Connection connection : gameConnections.get(gameID)) {
                if (connection.getUsername().equals(username)) {
                    gameConnections.get(gameID).remove(connection);
                    break;
                }
            }
        }
    }

    public void broadcastMessage(int gameID, String message) throws IOException {
        if (gameConnections.containsKey(gameID)) {
            for (Connection connection : gameConnections.get(gameID)) {
                connection.getSession().getRemote().sendString(message);
            }
        }
    }

    public void broadcastMessageToOthers(String username, int gameID, String message) throws IOException {
        if (gameConnections.containsKey(gameID)) {
            for (Connection connection : gameConnections.get(gameID)) {
                if (!connection.getUsername().equals(username)) {
                    connection.getSession().getRemote().sendString(message);
                }
            }
        }
    }

    public void sendMessage(String username, int gameID, String message) throws IOException {
        if (gameConnections.containsKey(gameID)) {
            for (Connection connection: gameConnections.get(gameID)) {
                if (connection.getUsername().equals(username)) {
                    connection.getSession().getRemote().sendString(message);
                    return;
                }
            }
        }
    }
}
