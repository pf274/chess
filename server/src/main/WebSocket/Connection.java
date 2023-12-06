package WebSocket;

import org.eclipse.jetty.websocket.api.Session;

public class Connection {

    private final Session session;

    private final String username;

    private final int gameID;

    public Connection(Session session, String username, int gameID) {
        this.session = session;
        this.username = username;
        this.gameID = gameID;
    }

    public String getUsername() {
        return username;
    }

    public int getGameID() {
        return gameID;
    }

    public Session getSession() {
        return session;
    }

}
