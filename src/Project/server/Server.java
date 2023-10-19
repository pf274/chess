package server;

import server.APIHandlers.*;
import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.AuthToken;
import server.Responses.APIResponse;
import server.Responses.ResponseMaker;
import spark.Request;

import java.util.Objects;

import static spark.Spark.*;

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

    public static void main(String[] args) {
        var serverInstance = new Server();
        port(8080);

        // authorization
        before((request, response) -> {
            String requestPath = request.pathInfo();
            // check authorization
            if ((Objects.equals(requestPath, "/session") || Objects.equals(requestPath, "/user")) && Objects.equals(request.requestMethod(), "POST")) {
                return;
            }
            if ((Objects.equals(requestPath, "/db") && Objects.equals(request.requestMethod(), "DELETE"))) {
                return;
            }
            String authToken = request.headers("Authorization");
            // get rid of "Bearer " prefix
            if (authToken != null && authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }
            if (authToken == null || serverInstance.authDAO.getAuthToken(authToken) == null) {
                halt(401, "Error: unauthorized");
            }
        });

        // gameDataHandler routes
        delete("/db", serverInstance.gameDataHandler.clearDatabase);
        get("/game", serverInstance.gameDataHandler.getGameList);
        post("/game", serverInstance.gameDataHandler.createGame);
        // loginHandler routes
        post("/session", serverInstance.loginHandler.newSession);
        delete("/session", serverInstance.loginHandler.endSession);
        // registerHandler routes
        post("/user", serverInstance.registerHandler.register);
        // joinGameHandler routes
        put("/game", serverInstance.joinGameHandler.joinGame);

        // set response type
        after((request, response) -> {
            response.type("application/json");
        });

        // handle exceptions
        exception(APIException.class, (exception, request, response) -> {
            APIResponse exceptionResponse = handleException(exception);
            response.status(exceptionResponse.statusCode);
            response.body(exceptionResponse.statusMessage);
        });
        exception(Exception.class, (exception, request, response) -> {
          response.status(500);
          response.body("Internal server error");
        });
    }

    /**
     * Converts an Exception into an APIResponse.
     * @param exception the exception
     * @return an APIResponse
     */
    private static APIResponse handleException(Exception exception) {
        try {
            // convert the error message into an API response
            String message = exception.getMessage();
            int firstSpace = message.indexOf(" ");
            int status = Integer.parseInt(message.substring(0, firstSpace));
            String description = message.substring(firstSpace + 1);
            return ResponseMaker.exceptionResponse(status, description);
        } catch (Exception e) {
            return ResponseMaker.exceptionResponse(500, "Internal server error");
        }

    }

}
