package server.Responses;

public class ClearDatabaseResponse extends APIResponse {
    /**
     * Creates a new APIResponse
     *
     * @param statusCode    The status code
     * @param statusMessage The status message
     */
    public ClearDatabaseResponse(int statusCode, String statusMessage) {
        super(statusCode, statusMessage);
    }
}
