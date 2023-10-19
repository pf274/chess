package server.Services;

/**
 * Exception thrown when a service encounters an error
 */
public class ServiceException extends Exception {
    /**
     * The status code of the error
     */
    public int statusCode;
    /**
     * The status message of the error
     */
    public String statusMessage;
    /**
     * Creates a new ServiceException with the given message and status code.
     * @param statusCode the status code
     * @param message the error message
     */
    public ServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.statusMessage = message;
    }
}
