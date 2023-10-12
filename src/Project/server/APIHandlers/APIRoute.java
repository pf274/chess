package server.APIHandlers;

import server.ParsedRequest;
import server.Responses.APIResponse;

/**
 * This class handles an API request given a ParsedRequest object, method, and path.
 */
public class APIRoute {
    /**
     * This route's HTTP method.
     */
    private final HandlerBase.method method;
    /**
     * This route's path.
     */
    private final String path;
    /**
     * This route's handler.
     */
    private final RouteHandler handler;

    /**
     * Creates a new APIRoute with the given method, path, and handler.
     * @param method the HTTP method
     * @param path the path
     * @param handler the handler
     */
    public APIRoute(HandlerBase.method method, String path, RouteHandler handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    /**
     * Gets the HTTP method.
     * @return the HTTP method
     */
    public HandlerBase.method getMethod() {
        return method;
    }

    /**
     * Gets the path.
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Handles the API request.
     * @param request The Parsed API request
     * @return an API Response
     * @throws APIException if the API failed
     */
    public APIResponse handle(ParsedRequest request) throws APIException {
        return handler.handle(request);
    }
}
