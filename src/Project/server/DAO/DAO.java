package server.DAO;

public interface DAO {

    /**
     * Clears the data access object.
     * @throws DataAccessException if clearing the data access object fails
     */
    void clear() throws DataAccessException;
}
