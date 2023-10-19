package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.AuthToken;
import server.Responses.ResponseMaker;
import server.Services.JoinGameService;
import server.Services.ServiceException;
import spark.Request;
import spark.Route;


/**
 * This class is used to handle the API requests that are related to joining and leaving games.
 */
public class JoinGameHandler extends HandlerBase {
    /**
     * The service instance that this handler will use to handle requests.
     */
    private JoinGameService service = null;
    public Route joinGame = (req, res) -> {
        try {
            // get authorization
            AuthToken authToken = getAuthToken(req, service.authDAO);
            assert authToken != null;
            String username = authToken.username;
            // get body variables
            var body = parseBodyToMap(req.body());
            String rawGameID = (String) body.get("GameID");
            if (rawGameID == null) {
                throw new ServiceException(403, "bad request");
            }
            int GameID = Integer.parseInt(rawGameID);
            String playerColor = (String) body.get("playerColor");
            // run service
            this.service.joinGame(GameID, username, playerColor);
            // return successful response
            var response = ResponseMaker.joinGameResponse();
            res.status(response.statusCode);
            res.body(response.statusMessage);
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
        return null;
    };


    public Route leaveGame = (req, res) -> {
        try {
            // get variables
            int GameID = 1234;
            String username = "laseredface";
//                int GameID = Integer.parseInt(sparkRequest.getBody().get("GameID"));
//                String username = sparkRequest.getBody().get("username");
            // run service
            this.service.leaveGame(GameID, username);
            // return successful response
            var response = ResponseMaker.leaveGameResponse();
            res.status(response.statusCode);
            res.body(response.statusMessage);
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
        return null;
    };

    /**
     * Creates a new JoinGameHandler and defines its routes.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public JoinGameHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.service = new JoinGameService(authDAO, userDAO, gameDAO);
    }
}
