package WebSocket;

import com.google.gson.Gson;
import serverMessages.ServerMessage;
import userCommands.UserGameCommand;

import java.util.HashMap;

public class MessageFormatter {
    public static String prepareBodyClient(String username, int gameID, UserGameCommand.CommandType action, String details) {
        HashMap data = new HashMap();
        data.put("username", username);
        data.put("action", action.toString());
        data.put("details", details);
        data.put("gameID", gameID);
        String body = new Gson().toJson(data);
        return body;
    }

    public static String prepareBodyServer(String username, int gameID, ServerMessage.ServerMessageType action, String details) {
        HashMap data = new HashMap();
        data.put("username", username);
        data.put("action", action.toString());
        data.put("details", details);
        data.put("gameID", gameID);
        String body = new Gson().toJson(data);
        return body;
    }
}
