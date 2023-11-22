package DAO;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {
    /**
     * The status code of the error
     */
    public int statusCode;
    /**
     * The status message of the error
     */
    public String statusMessage;

    /**
     * Creates a new DataAccessException with the given message and status code.
     * @param StatusCode the status code
     * @param message the error message
     */
    public DataAccessException(int StatusCode, String message) {
        super(message);
        this.statusCode = StatusCode;
        this.statusMessage = message;
    }
}
