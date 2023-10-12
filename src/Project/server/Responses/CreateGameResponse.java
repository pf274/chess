package server.Responses;

/**
 * A response for when a game is created
 */
public class CreateGameResponse extends APIResponse {
    /**
     * Creates a new CreateGameResponse
     * @param statusCode The status code
     * @param statusMessage The status message
     */
    public CreateGameResponse(int gameID) {
        super(200, "{ \"gameID\": " + gameID + " }");
    }
}
