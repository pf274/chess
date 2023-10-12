package server.Responses;

/**
 * A response for when a user leaves a game successfully
 */
public class LeaveGameResponse extends APIResponse {
    /**
     * Creates a new LeaveGameResponse
     */
    public LeaveGameResponse() {
        super(200, "");
    }
}
