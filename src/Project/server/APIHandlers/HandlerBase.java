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

    /**
     * A list of routes that this handler will handle.
     */
    private final ArrayList<APIRoute> routes = new ArrayList<>();

    /**
     * Adds a route to the list of routes.
     * @param route the route to add
     */
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


    /**
     * Handles a request by finding the correct route and running the service.
     * @param request the request to handle
     * @return the response to the request
     * @throws APIException if the request could not be handled or if the route does not exist.
     */
    public APIResponse handleRequest(ParsedRequest request) throws APIException {
        for (APIRoute route : routes) {
            if (route.getMethod() == request.getMethod() && route.getPath().equals(request.getPath())) {
                return route.handle(request);
            }
        }
        return new ExceptionResponse(404, "Not found.");
    }

    /**
     * Checks if this handler has a route with the given method and path.
     * @param method the method to check
     * @param path the path to check
     * @return whether the handler has a route with the given method and path
     */
    public boolean hasRoute(method method, String path) {
        for (APIRoute route : routes) {
            if (route.getMethod() == method && route.getPath().equals(path)) {
                return true;
            }
        }
        return false;
    }
}
