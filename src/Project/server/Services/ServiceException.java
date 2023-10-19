package server.Services;

/**
 * Exception thrown when a service encounters an error
 */
public class ServiceException extends Exception {
    public int statusCode;
    public String message;
    /**
     * Creates a new ServiceException with the given message and status code.
     * @param statusCode the status code
     * @param message the error message
     */
    public ServiceException(int statusCode, String message) {
        super(statusCode + " " + message);
        this.statusCode = statusCode;
        this.message = message;
    }
}
