package server.Responses;

import server.Models.AuthToken;

/**
 * A response for when a user logs in
 */
public class LoginResponse extends APIResponse {
    /**
     * Creates a new LoginResponse
     * @param username The username of the user who logged in
     * @param authToken The authToken of the user who logged in
     */
    public LoginResponse(String username, AuthToken authToken) {
        super(200, "blah blah");
    }
}
