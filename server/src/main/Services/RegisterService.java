package Services;

import DAO.AuthDAO;
import DAO.DataAccessException;
import DAO.GameDAO;
import DAO.UserDAO;
import Models.AuthToken;

/**
 * Functionality for registering a new user
 */
public class RegisterService extends ServiceBase {
    /**
     * Creates a new Service with the given DAOs.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public RegisterService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        super(authDAO, userDAO, gameDAO);
    }

    /**
     * Registers a new user
     * @param username The username of the user
     * @param password The password of the user
     * @param email The email of the user
     * @throws ServiceException If the user could not be registered
     */
    public AuthToken register(String username, String password, String email) throws ServiceException {
        try {
            var foundUser = userDAO.getUser(username);
            if (foundUser != null) {
                throw new ServiceException(403, "username already taken");
            }
            userDAO.addUser(username, password, email);
            AuthToken newAuthToken = new AuthToken(username);
            authDAO.addAuthToken(newAuthToken);
            return newAuthToken;
        } catch (DataAccessException e) {
            throw new ServiceException(500, e.getMessage());
        }
    }
}
