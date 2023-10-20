package server.Services;

import server.DAO.AuthDAO;
import server.DAO.DataAccessException;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.AuthToken;
import server.Models.User;

/**
 * Functionality for logging in and out
 */
public class LoginService extends ServiceBase {
    /**
     * Creates a new Service with the given DAOs.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public LoginService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        super(authDAO, userDAO, gameDAO);
    }

    /**
     * Logs in a user
     * @param username The username of the user
     * @param password The password of the user
     * @return Whether the user was logged in
     * @throws ServiceException If the user could not be logged in
     */
    public AuthToken login(String username, String password) throws ServiceException {
        try {
            User userInfo = userDAO.getUser(username);
            if (userInfo == null) {
                throw new ServiceException(400, "User does not exist");
            }
            if (!userInfo.password.equals(password)) {
                throw new ServiceException(401, "Password is incorrect");
            }
            AuthToken authToken = new AuthToken(username);
            authDAO.addAuthToken(authToken);
            return authToken;
        } catch (DataAccessException e) {
            throw new ServiceException(500, e.getMessage());
        }
    }

    /**
     * Logs out a user
     * @param username The username of the user
     * @throws ServiceException If the user could not be logged out
     */
    public void logout(String username) throws ServiceException {
        try {
            AuthToken matchingAuthToken = authDAO.getAuthTokenByUsername(username);
            authDAO.deleteAuthToken(matchingAuthToken.authToken);
        } catch (DataAccessException e) {
            throw new ServiceException(500, e.getMessage());
        }
    }
}
