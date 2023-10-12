package server.Responses;

/**
 * A response for when a game is created
 */
public class CreateGameResponse extends APIResponse {
    /**
     * Creates a new CreateGameResponse
     * @param gameID The ID of the game that was created
     */
    public CreateGameResponse(int gameID) {
        super(200, "{ \"gameID\": " + gameID + " }");
    }
}
