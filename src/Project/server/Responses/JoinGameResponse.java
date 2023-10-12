package server.Responses;

/**
 * A response for when a user joins a game successfully
 */
public class JoinGameResponse extends APIResponse {
    /**
     * Creates a new JoinGameResponse
     */
    public JoinGameResponse() {
        super(200, "");
    }
}
