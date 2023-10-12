package server.Responses;

/**
 * A response for when a user is not authorized to perform an action
 */
public class UnauthorizedResponse extends APIResponse {
    /**
     * Creates a new UnauthorizedResponse
     */
    public UnauthorizedResponse() {
        super(401, "Error: Unauthorized.");
    }
}
