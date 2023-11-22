package Services;

import DAO.*;

/**
 * Base class for services
 */
public class ServiceBase {
    /**
     * The server's authDAO, passed to this service instance from the Server instance through a handler instance.
     */
    public AuthDAO authDAO = null;

    /**
     * The server's userDAO, passed to this service instance from the Server instance through a handler instance.
     */
    public UserDAO userDAO = null;

    /**
     * The server's gameDAO, passed to this service instance from the Server instance through a handler instance.
     */
    public GameDAO gameDAO = null;

    /**
     * Creates a new Service with the given DAOs.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public ServiceBase(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }
}
