package WebSocket;

import com.google.gson.Gson;

import java.util.HashMap;

public class MessageFormatter {
    public static String prepareBody(String username, int gameID, ChessAction action, String details) {
        HashMap data = new HashMap();
        data.put("username", username);
        data.put("action", action.toString());
        data.put("details", details);
        data.put("gameID", gameID);
        String body = new Gson().toJson(data);
        return body;
    }
}
