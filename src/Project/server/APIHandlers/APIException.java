package server.APIHandlers;

/**
 * An exception that is thrown when an API call fails.
 */
public class APIException extends Exception {
    /**
     * Creates a new APIException with the given message.
     * @param message the error message
     */
    public APIException(String message) {
        super(message);
    }

    /**
     * Creates a new APIException with the given message and status code.
     * @param statusCode the status code
     * @param message the error message
     */
    public APIException(int statusCode, String message) {
        super(statusCode + " " + message);
    }
}
