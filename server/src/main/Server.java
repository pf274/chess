import APIHandlers.*;
import DAO.AuthDAO;
import DAO.GameDAO;
import DAO.UserDAO;
import Responses.ResponseMapper;
import Services.GameDataService;
import Services.LoginService;
import WebSocket.WebSocketHandler;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.Route;
import spark.Spark;
import java.util.Objects;

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

    private final GameDataService webSocketGameDataService = new GameDataService(authDAO, userDAO, gameDAO);

    private final LoginService webSocketLoginService = new LoginService(authDAO, userDAO, gameDAO);
    /**
     * The server
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        var serverInstance = new Server();
        Spark.port(8080);
        WebSocketHandler webSocketHandler = new WebSocketHandler(serverInstance.webSocketGameDataService, serverInstance.webSocketLoginService);
        Spark.webSocket("/connect", webSocketHandler);

        // enable CORS
        Spark.options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // authorization
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            String requestPath = request.pathInfo();
            String requestMethod = request.requestMethod();
            System.out.println("----------");
            System.out.println("Request:");
            System.out.println(requestMethod + " " + requestPath);
            System.out.println(request.body());
            System.out.println("\nLog:");
            if (Objects.equals(requestMethod, "OPTIONS")) {
                return;
            }
            // check authorization
            if ((Objects.equals(requestPath, "/session") || Objects.equals(requestPath, "/user")) && Objects.equals(requestMethod, "POST")) {
                return;
            }
            if ((Objects.equals(requestPath, "/db") && Objects.equals(requestMethod, "DELETE"))) {
                return;
            }
            if (Objects.equals(requestPath, "/connect") && Objects.equals(requestMethod, "GET")) {
                return;
            }
            String authToken = request.headers("Authorization");
            // get rid of "Bearer " prefix (for Postman debugging)
            if (authToken != null && authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }
            if (authToken == null || serverInstance.authDAO.getAuthToken(authToken) == null) {
                var res = ResponseMapper.exceptionResponse(401, "unauthorized", response);
                Spark.halt(res.statusCode, res.statusMessage);
            }
        });
        // gameDataHandler routes
        Spark.delete("/db", serverInstance.gameDataHandler.clearDatabase);
        Spark.get("/game", serverInstance.gameDataHandler.getGameList);
        Spark.post("/game", serverInstance.gameDataHandler.createGame);
        // loginHandler routes
        Spark.post("/session", serverInstance.loginHandler.newSession);
        Spark.delete("/session", serverInstance.loginHandler.endSession);
        // registerHandler routes
        Spark.post("/user", serverInstance.registerHandler.register);
        // joinGameHandler routes
        Spark.put("/game", serverInstance.joinGameHandler.joinGame);
        Spark.delete("/game", serverInstance.joinGameHandler.leaveGame);

        // default routes
//        Spark.path("*", () -> {
//            Spark.get("", defaultRouteHandler);
//            Spark.post("", defaultRouteHandler);
//            Spark.delete("", defaultRouteHandler);
//            Spark.put("", defaultRouteHandler);
//        });

        // set response type
        Spark.after((request, response) -> {
            response.type("application/json");
            System.out.println("\nResponse:");
            System.out.println(response.status() + " " + response.body());
            System.out.println("----------");
        });

        // handle uncaught exceptions
        Spark.exception(Exception.class, (exception, request, response) -> {
            System.out.println("Exception: " + exception.getMessage());
          response.status(500);
          response.body(exception.getMessage());
          System.out.println("----------");
        });
    }
}
