package server.Responses;

/**
 * A response for when the database is cleared successfully
 */
public class ClearDatabaseResponse extends APIResponse {
    /**
     * Creates a new APIResponse
     */
    public ClearDatabaseResponse() {
        super(200, "Clear database succeeded.");
    }
}
