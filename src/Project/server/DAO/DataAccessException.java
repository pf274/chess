package server.DAO;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    /**
     * Creates a new DataAccessException with the given message.
     * @param message the error message
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Creates a new DataAccessException with the given message and status code.
     * @param StatusCode the status code
     * @param message the error message
     */
    public DataAccessException(int StatusCode, String message) {
        super(StatusCode + " " + message);
    }
}
