package Models;

import java.util.UUID;

/**
 * Represents an auth token
 */
public class AuthToken {

    /**
     * The auth token string
     */
    public String authString;

    /**
     * The username associated with this auth token
     */
    public String username;

    /**
     * Creates a new auth token
     * @param username the username associated with this auth token
     */
    public AuthToken(String username) {
        this.username = username;
        this.authString = UUID.randomUUID().toString();
    }
}
