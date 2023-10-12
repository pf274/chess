package server.Responses;

/**
 * A response for when a user logs out successfully
 */
public class LogoutResponse extends APIResponse {
    /**
     * Creates a new LogoutResponse
     */
    public LogoutResponse() {
        super(200, "");
    }
}
