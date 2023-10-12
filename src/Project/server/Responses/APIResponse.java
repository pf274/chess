package server.Responses;

/**
 * A base class for API responses
 */
public class APIResponse {

    /**
     * The status code of the response
     */
    public int statusCode;

    /**
     * The status message of the response
     */
    public String statusMessage;

    /**
     * Creates a new APIResponse
     * @param statusCode The status code
     * @param statusMessage The status message
     */
    public APIResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

}
