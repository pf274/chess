package server.Responses;

public class ClearDatabaseResponse extends APIResponse {
    /**
     * Creates a new APIResponse
     */
    public ClearDatabaseResponse() {
        super(200, "Clear database succeeded.");
    }
}
