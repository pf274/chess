package server.Services;

/**
 * Exception thrown when a service encounters an error
 */
public class ServiceException extends Exception {
    int statusCode;
    public ServiceException(int statusCode, String message) {
        super(statusCode + " " + message);
    }

    /**
     * Gets the status code of the error
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }
}
