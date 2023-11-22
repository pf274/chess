package APIHandlers;

import DAO.AuthDAO;
import DAO.DataAccessException;
import Models.AuthToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Request;
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
     * Extracts the auth token from the spark request
     * @param req the spark request
     * @param authDAO the authDAO to use to get the auth token
     * @return the auth token
     */
    public static AuthToken getAuthToken(Request req, AuthDAO authDAO) throws DataAccessException {
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
