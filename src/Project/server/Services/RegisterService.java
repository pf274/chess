package server.Services;

import server.DAO.AuthDAO;
import server.DAO.DataAccessException;
import server.DAO.GameDAO;
import server.DAO.UserDAO;

/**
 * Functionality for registering a new user
 */
public class RegisterService extends ServiceBase {
    /**
     * Creates a new Service with the given DAOs.
     *
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public RegisterService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        super(authDAO, userDAO, gameDAO);
    }

    /**
     * Registers a new user
     *
     * @param username The username of the user
     * @param password The password of the user
     * @param email The email of the user
     * @throws ServiceException If the user could not be registered
     */
    public void register(String username, String password, String email) throws ServiceException {
        try {
            userDAO.addUser(username, password, email);
        } catch (DataAccessException e) {
            throw new ServiceException(500, e.getMessage());
        }
    }
}
