package server.DAO;

import server.Models.AuthToken;
import java.util.ArrayList;

/**
 * The AuthDAO class is responsible for storing and retrieving auth tokens.
 */
public class AuthDAO implements DAO {
    /**
     * The list of auth tokens.
     */
    private final ArrayList<AuthToken> authTokens = new ArrayList<>();

    /**
     * Adds an auth token to the list of auth tokens.
     * @param authToken the auth token to add
     * @throws DataAccessException if the auth token already exists
     */
    public void addAuthToken(AuthToken authToken) throws DataAccessException {
        for (AuthToken token : authTokens) {
            if (token.authToken.equals(authToken.authToken)) {
                throw new DataAccessException(500, "Auth token " + authToken.authToken + " already exists");
            }
        }
        authTokens.add(authToken);
    }

    /**
     * Clears the list of auth tokens.
     * @throws DataAccessException if clearing the list of auth tokens fails
     */
    public void clear() throws DataAccessException {
        authTokens.clear();
    }

    /**
     * Gets an auth token from the list of auth tokens, given the auth token string.
     * @param authToken the auth token string
     * @return the auth token
     */
    public AuthToken getAuthToken(String authToken) {
        for (AuthToken token : authTokens) {
            if (token.authToken.equals(authToken)) {
                return token;
            }
        }
        return null;
    }

    /**
     * Gets an auth token from the list of auth tokens, given the username.
     * @param username the username
     * @return the auth token
     * @throws DataAccessException if the auth token does not exist
     */
    public AuthToken getAuthTokenByUsername(String username) throws DataAccessException {
        for (AuthToken token : authTokens) {
            if (token.username.equals(username)) {
                return token;
            }
        }
        throw new DataAccessException(500, "Auth token for user " + username + " not found");
    }

    /**
     * Deletes an auth token from the list of auth tokens, given the auth token string.
     * @param authToken the auth token string
     * @throws DataAccessException if the auth token does not exist
     */
    public void deleteAuthToken(String authToken) throws DataAccessException {
        for (int i = 0; i < authTokens.size(); i++) {
            if (authTokens.get(i).authToken.equals(authToken)) {
                authTokens.remove(i);
                return;
            }
        }
        throw new DataAccessException(500, "Auth token " + authToken + " not found");
    }
}
