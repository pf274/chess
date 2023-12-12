package WebSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import serverMessages.ServerMessage;

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

    public void broadcastMessage(int gameID, ServerMessage message) throws IOException {
        String messageString = new Gson().toJson(message);
        System.out.println("Broadcasting message to all participants of game " + gameID + ": " + messageString);
        if (gameConnections.containsKey(gameID)) {
            for (Connection connection : gameConnections.get(gameID)) {
                System.out.println("(Sending message to " + connection.getUsername() + ": " + messageString + ")");
                connection.getSession().getRemote().sendString(messageString);
            }
        }
    }

    public void broadcastMessageToOthers(String username, int gameID, ServerMessage message) {
        try {
            String messageString = new Gson().toJson(message);
            System.out.println("Broadcasting message to all participants of game " + gameID + " except " + username + ": " + messageString);
            if (gameConnections.containsKey(gameID)) {
                for (Connection connection : gameConnections.get(gameID)) {
                    if (!connection.getUsername().equals(username)) {
                        connection.getSession().getRemote().sendString(messageString);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(String username, int gameID, ServerMessage message) {
        try {
            String messageString = new Gson().toJson(message);
            System.out.println("Broadcasting message to " + username + ": " + messageString);
            if (gameConnections.containsKey(gameID)) {
                for (Connection connection: gameConnections.get(gameID)) {
                    if (connection.getUsername().equals(username)) {
                        connection.getSession().getRemote().sendString(messageString);
                        return;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessageToSession(Session session, ServerMessage message) {
        try {
            String messageString = new Gson().toJson(message);
            System.out.println("Broadcasting message to " + session + ": " + messageString);
            session.getRemote().sendString(messageString);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
