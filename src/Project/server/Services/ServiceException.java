package server.Services;

/**
 * Exception thrown when a service encounters an error
 */
public class ServiceException extends Exception {
    /**
     * Creates a new ServiceException with the given message and status code.
     * @param statusCode the status code
     * @param message the error message
     */
    public ServiceException(int statusCode, String message) {
        super(statusCode + " " + message);
    }

    /**
     * Creates a new ServiceException with the given message.
     * @param message the error message
     */
    public ServiceException(String message) {
        super(message);
    }
}
