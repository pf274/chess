package server.Models;

/**
 * Represents an auth token
 */
public class AuthToken {
    /**
     * Creates a new auth token
     * @param username the username associated with this auth token
     */
    public AuthToken(String username) {
        // TODO: generate unique auth tokens
        this.username = username;
        this.authToken = "asdfasdfasdfasdf";
    }
    /**
     * The auth token string
     */
    public String authToken;
    /**
     * The username associated with this auth token
     */
    public String username;
}
