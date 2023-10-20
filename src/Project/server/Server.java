package server;

import server.APIHandlers.*;
import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Responses.ResponseMapper;
import spark.Route;
import java.util.Objects;
import static spark.Spark.*;

/**
 * This class is used to route the API requests to the appropriate handler.
 * It also contains the DAOs that are used by the handlers.
 */
public class Server {

    /**
     * The default route handler, which is used to handle requests that don't match any of the other routes.
     */
    private static final Route defaultRouteHandler = (req, res) -> {
        ResponseMapper.exceptionResponse(404, "path not found.", res);
        return null;
    };

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
     * The server
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        var serverInstance = new Server();
        port(8080);

        // authorization
        before((request, response) -> {
            String requestPath = request.pathInfo();
            String requestMethod = request.requestMethod();
            // check authorization
            if ((Objects.equals(requestPath, "/session") || Objects.equals(requestPath, "/user")) && Objects.equals(requestMethod, "POST")) {
                return;
            }
            if ((Objects.equals(requestPath, "/db") && Objects.equals(requestMethod, "DELETE"))) {
                return;
            }
            String authToken = request.headers("Authorization");
            // get rid of "Bearer " prefix (for Postman debugging)
            if (authToken != null && authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }
            if (authToken == null || serverInstance.authDAO.getAuthToken(authToken) == null) {
                var res = ResponseMapper.exceptionResponse(401, "unauthorized", response);
                halt(res.statusCode, res.statusMessage);
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


        // default routes (most are caught by authorization)
        path("*", () -> {
            get("", defaultRouteHandler);
            post("", defaultRouteHandler);
            delete("", defaultRouteHandler);
            put("", defaultRouteHandler);
        });

        // set response type
        after((request, response) -> {
            response.type("application/json");
        });

        // handle uncaught exceptions
        exception(Exception.class, (exception, request, response) -> {
          response.status(500);
          response.body(exception.getMessage());
        });
    }
}
