package server.Responses;

import server.Models.AuthToken;
import server.Models.Game;
import spark.Response;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class for setting the spark response depending on the usage
 */
public class ResponseMapper {

    /**
     * Sets the response for a clear database request
     * @param res the Spark response object
     */
    public static void clearDatabaseResponse(Response res) {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("message", "Clear succeeded.");
        APIResponse apiResponse = new APIResponse(200, responseValues);
        res.status(apiResponse.statusCode);
        res.body(apiResponse.statusMessage);
    }

    /**
     * Sets the response for a create game request
     * @param gameID the game's ID
     * @param res the Spark response object
     */
    public static void createGameResponse(int gameID, Response res) {
        HashMap<String, Integer> responseValues = new HashMap<>();
        responseValues.put("gameID", gameID);
        APIResponse apiResponse = new APIResponse(200, responseValues);
        res.status(apiResponse.statusCode);
        res.body(apiResponse.statusMessage);
    }

    /**
     * Sets the response for an exception
     * @param statusCode the status code
     * @param message the message
     * @param res the Spark response object
     * @return the APIResponse
     */
    public static APIResponse exceptionResponse(int statusCode, String message, Response res) {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("message", "Error: " + message);
        APIResponse apiResponse = new APIResponse(statusCode, responseValues);
        res.status(apiResponse.statusCode);
        res.body(apiResponse.statusMessage);
        return apiResponse;
    }

    /**
     * Creates an APIResponse for an exception
     * @param statusCode the status code
     * @param message the message
     * @return the APIResponse
     */
    public static APIResponse exceptionResponse(int statusCode, String message) {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("message", "Error: " + message);
        return new APIResponse(statusCode, responseValues);
    }

    /**
     * Sets the response for a get game request
     * @param res the Spark response object
     */
    public static void joinGameResponse(Response res) {
        HashMap<String, Integer> responseValues = new HashMap<>();
        APIResponse apiResponse = new APIResponse(200, responseValues);
        res.status(apiResponse.statusCode);
        res.body(apiResponse.statusMessage);
    }

    /**
     * Sets the response for a list games request
     * @param games the list of games
     * @param res the Spark response object
     */
    public static void listGamesResponse(ArrayList<Game> games, Response res) {
        HashMap<String, ArrayList<Game>> responseValues = new HashMap<>();
        responseValues.put("games", games);
        APIResponse apiResponse = new APIResponse(200, responseValues);
        res.status(apiResponse.statusCode);
        res.body(apiResponse.statusMessage);
    }

    /**
     * Sets the response for a login request
     * @param authToken the auth token
     * @param res the Spark response object
     */
    public static void loginResponse(AuthToken authToken, Response res) {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("username", authToken.username);
        responseValues.put("authToken", authToken.authToken);
        APIResponse apiResponse = new APIResponse(200, responseValues);
        res.status(apiResponse.statusCode);
        res.body(apiResponse.statusMessage);
    }

    /**
     * Sets the response for a logout request
     * @param res the Spark response object
     */
    public static void logoutResponse(Response res) {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("message", "Logout succeeded.");
        APIResponse apiResponse = new APIResponse(200, responseValues);
        res.status(apiResponse.statusCode);
        res.body(apiResponse.statusMessage);
    }

    /**
     * Sets the response for a register request
     * @param authToken the auth token
     * @param res the Spark response object
     */
    public static void registerResponse(AuthToken authToken, Response res) {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("username", authToken.username);
        responseValues.put("authToken", authToken.authToken);
        APIResponse apiResponse = new APIResponse(200, responseValues);
        res.status(apiResponse.statusCode);
        res.body(apiResponse.statusMessage);
    }

    /**
     * Sets the response for a start game request
     * @param res the Spark response object
     */
    public static void unauthorizedResponse(Response res) {
        HashMap<String, String> responseValues = new HashMap<>();
        responseValues.put("message", "Error: Unauthorized");
        APIResponse apiResponse = new APIResponse(401, responseValues);
        res.status(apiResponse.statusCode);
        res.body(apiResponse.statusMessage);
    }
}
