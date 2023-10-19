package server.Responses;

import com.google.gson.Gson;
import server.Models.AuthToken;
import server.Models.Game;

import java.util.ArrayList;
import java.util.HashMap;

public class ResponseMaker {

    public static APIResponse clearDatabaseResponse() {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("message", "Clear succeeded.");
        return new APIResponse(200, responseValues);
    }

    public static APIResponse createGameResponse(int gameID) {
        HashMap<String, Integer> responseValues = new HashMap<>();
        responseValues.put("gameID", gameID);
        return new APIResponse(200, responseValues);
    }

    public static APIResponse exceptionResponse(int statusCode, String message) {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("message", "Error: " + message);
        return new APIResponse(statusCode, responseValues);
    }

    public static APIResponse joinGameResponse() {
        HashMap<String, Integer> responseValues = new HashMap<>();
        return new APIResponse(200, responseValues);
    }

    public static APIResponse leaveGameResponse() {
        HashMap<String, Integer> responseValues = new HashMap<>();
        return new APIResponse(200, responseValues);
    }

    public static APIResponse listGamesResponse(ArrayList<Game> games) {
        HashMap<String, ArrayList<Game>> responseValues = new HashMap<>();
        responseValues.put("games", games);
        return new APIResponse(200, responseValues);
    }

    public static APIResponse loginResponse(AuthToken authToken) {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("username", authToken.username);
        responseValues.put("authToken", authToken.authToken);
        return new APIResponse(200, responseValues);
    }

    public static APIResponse logoutResponse() {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("message", "Logout succeeded.");
        return new APIResponse(200, responseValues);
    }

    public static APIResponse registerResponse(AuthToken authToken) {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("username", authToken.username);
        responseValues.put("authToken", authToken.authToken);
        return new APIResponse(200, responseValues);
    }

    public static APIResponse unauthorizedResponse() {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("message", "Error: Unauthorized");
        return new APIResponse(401, responseValues);
    }
}
