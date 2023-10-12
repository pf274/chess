package server;

import com.sun.net.httpserver.HttpExchange;
import server.APIHandlers.*;
import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Responses.APIResponse;
import server.Responses.ExceptionResponse;

import java.net.http.HttpRequest;

/**
 * This class is used to route the API requests to the appropriate handler.
 * It also contains the DAOs that are used by the handlers.
 */
public class Server {
    /**
     * The server's gameDAO
     */
    private final GameDAO gameDAO = new GameDAO();

    /**
     * The server's authDAO
     */
    private final AuthDAO authDAO = new AuthDAO();

    /**
     * The server's userDAO
     */
    private final UserDAO userDAO = new UserDAO();

    /**
     * The server's LoginHandler, which is used to handle login and logout requests
     */
    private final LoginHandler loginHandler = new LoginHandler(authDAO, userDAO, gameDAO);

    /**
     * The server's JoinGameHandler, which is used to handle join game and leave game requests
     */
    private final JoinGameHandler joinGameHandler = new JoinGameHandler(authDAO, userDAO, gameDAO);

    /**
     * The server's RegisterHandler, which is used to handle register requests
     */
    private final RegisterHandler registerHandler = new RegisterHandler(authDAO, userDAO, gameDAO);

    /**
     * The server's GameDataHandler, which is used to handle game data-related requests
     */
    private final GameDataHandler gameDataHandler = new GameDataHandler(authDAO, userDAO, gameDAO);

    /**
     * Creates a new Server.
     * @throws ClassNotFoundException if a service class is not found (should never happen)
     */
    public Server() throws ClassNotFoundException {
        // TODO: start the server
    }

    /**
     * This method is used to route the API requests to the appropriate handler.
     * If an APIException is thrown, this method will catch it and return an ExceptionResponse, which is a child of APIResponse.
     * @param exchange the http exchange
     * @return an API response
     */
    public APIResponse apiRouter(HttpExchange exchange) {
        try {
            ParsedRequest parsedRequest = new ParsedRequest(exchange);
            String path = parsedRequest.getPath();
            HandlerBase.method method = parsedRequest.getMethod();
            if (gameDataHandler.hasRoute(method, path)) {
                return gameDataHandler.handleRequest(parsedRequest);
            }
            if (loginHandler.hasRoute(method, path)) {
                return loginHandler.handleRequest(parsedRequest);
            }
            if (joinGameHandler.hasRoute(method, path)) {
                return joinGameHandler.handleRequest(parsedRequest);
            }
            if (registerHandler.hasRoute(method, path)) {
                return registerHandler.handleRequest(parsedRequest);
            }
            return new ExceptionResponse(404, "Not found.");
        } catch (APIException e) {
            return handleException(e);
        }
    }

    /**
     * Converts an Exception into an ExceptionResponse.
     * @param exception the exception
     * @return an ExceptionResponse
     */
    private static ExceptionResponse handleException(Exception exception) {
        // convert the error message into an API response
        String message = exception.getMessage();
        int firstSpace = message.indexOf(" ");
        int status = Integer.parseInt(message.substring(0, firstSpace));
        String description = message.substring(firstSpace + 1);
        return new ExceptionResponse(status, description);
    }

}
