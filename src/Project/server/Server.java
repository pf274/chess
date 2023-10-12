package server;

import server.APIHandlers.*;
import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Responses.APIResponse;
import server.Responses.ExceptionResponse;

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
    }

    /**
     * This method is used to route the API requests to the appropriate handler.
     * If an APIException is thrown, this method will catch it and return an ExceptionResponse, which is a child of APIResponse.
     * @param request the API request
     * @return an API response
     */
    public APIResponse apiRouter(String request) {
        try {
            // TODO: handle routes
        } catch (APIException e) {
            String message = e.getMessage();
            int firstSpace = message.indexOf(" ");
            int status = Integer.parseInt(message.substring(0, firstSpace));
            String description = message.substring(firstSpace + 1);
            return new ExceptionResponse(status, description);
        }
    }

}
