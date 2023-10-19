package server.Models;

import java.util.UUID;

/**
 * Represents an auth token
 */
public class AuthToken {

    /**
     * The auth token string
     */
    public String authToken;

    /**
     * The username associated with this auth token
     */
    public String username;

    /**
     * Creates a new auth token
     * @param username the username associated with this auth token
     */
    public AuthToken(String username) {
        // TODO: generate unique auth tokens
        this.username = username;
        this.authToken = UUID.randomUUID().toString();
    }
}
