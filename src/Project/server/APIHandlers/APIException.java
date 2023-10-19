package server.APIHandlers;

/**
 * An exception that is thrown when an API call fails.
 */
public class APIException extends Exception {
    /**
     * The status code of the error
     */
    public int statusCode;
    /**
     * The status message of the error
     */
    public String statusMessage;
    /**
     * Creates a new APIException with the given message and status code.
     * @param statusCode the status code
     * @param message the error message
     */
    public APIException(int statusCode, String message) {
        super(statusCode + " " + message);
        this.statusCode = statusCode;
        this.statusMessage = message;
    }
}
