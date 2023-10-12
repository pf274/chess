package server.Responses;

/**
 * A response for when an exception is thrown
 */
public class ExceptionResponse extends APIResponse {
    /**
     * Creates a new ExceptionResponse
     * @param statusCode The status code
     * @param statusMessage The status message
     */
    public ExceptionResponse(int statusCode, String statusMessage) {
        super(statusCode, statusMessage);
    }
}
