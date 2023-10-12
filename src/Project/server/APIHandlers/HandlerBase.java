package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Services.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A base class to define common API handler functionality.
 */
public class HandlerBase {

    public ServiceBase service = null;

    /**
     * Creates a new Handler with the given DAOs and service class name.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     * @param serviceClassName the fully qualified name of the service class
     * @throws ClassNotFoundException if the service class is not found
     */
    public HandlerBase(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO, String serviceClassName) throws ClassNotFoundException {
        try {
            // Get the class object for the specified service
            Class<?> serviceClass = Class.forName(serviceClassName);

            // Get the constructor that accepts the DAO instances
            Constructor<?> constructor = serviceClass.getConstructor(AuthDAO.class, UserDAO.class, GameDAO.class);

            // Create an instance of the service class
            this.service = (ServiceBase) constructor.newInstance(authDAO, userDAO, gameDAO);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
