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
}
