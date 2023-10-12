package server.Responses;

import server.Models.AuthToken;

/**
 * A response for when a user registers
 */
public class RegisterResponse extends APIResponse {
    /**
     * Creates a new RegisterResponse
     * @param username The user's username
     * @param authToken The user's authToken
     */
    public RegisterResponse(String username, AuthToken authToken) {
        // TODO: format this correctly
        super(200, "username and authToken");
    }
}
