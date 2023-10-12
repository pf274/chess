package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.ParsedRequest;
import server.Responses.APIResponse;
import server.Responses.ExceptionResponse;
import server.Services.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * A base class to define common API handler functionality.
 */
public class HandlerBase {

    public ArrayList<APIRoute> routes = new ArrayList<>();

    public void addRoute(APIRoute route) {
        routes.add(route);
    }

    /**
     * An enum to define the HTTP methods.
     */
    public enum method {
        PUT,
        POST,
        GET,
        DELETE
    }
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

    public APIResponse handleRequest(ParsedRequest request) throws APIException {
        for (APIRoute route : routes) {
            if (route.getMethod() == request.getMethod() && route.getPath().equals(request.getPath())) {
                return route.handle(request);
            }
        }
        return new ExceptionResponse(404, "Not found.");
    }

    public boolean hasRoute(method method, String path) {
        for (APIRoute route : routes) {
            if (route.getMethod() == method && route.getPath().equals(path)) {
                return true;
            }
        }
        return false;
    }
}
