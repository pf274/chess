package server.APIHandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.DAO.AuthDAO;
import server.Models.AuthToken;
import spark.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * A base class to define common API handler functionality.
 */
public class HandlerBase {

    /**
     * Uses Gson to parse the request body
     * @param rawBody the body to parse
     * @return the parsed body
     */
    public HashMap parseBodyToMap(String rawBody) {
        var type = HashMap.class;
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        var result = gson.fromJson(rawBody, type);
        return Objects.requireNonNullElseGet(result, HashMap::new);
    }

    /**
     * Uses Gson to parse the request body
     * @param rawBody the body to parse
     * @return the parsed body
     */
    public ArrayList parseBodyToArray(String rawBody) {
        var type = ArrayList.class;
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        var result = gson.fromJson(rawBody, type);
        return Objects.requireNonNullElseGet(result, ArrayList::new);
    }

    public static AuthToken getAuthToken(Request req, AuthDAO authDAO) {
        String authTokenString = req.headers("Authorization");
        if (authTokenString == null) {
            return null;
        }
        // remove bearer prefix
        if (authTokenString.startsWith("Bearer ")) {
            authTokenString = authTokenString.substring(7);
        }
        return authDAO.getAuthToken(authTokenString);
    }
}
