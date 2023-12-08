package APIHandlers;

import DAO.AuthDAO;
import DAO.GameDAO;
import DAO.UserDAO;
import Models.AuthToken;
import Responses.ResponseMapper;
import Services.JoinGameService;
import Services.ServiceException;
import spark.Route;


/**
 * This class is used to handle the API requests that are related to joining and leaving games.
 */
public class JoinGameHandler extends HandlerBase {
    /**
     * The service instance that this handler will use to handle requests.
     */
    private JoinGameService service;

    /**
     * The route handler that handles the join game request.
     */
    public Route joinGame = (req, res) -> {
        try {
            // get authorization
            AuthToken authToken = getAuthToken(req, service.authDAO);
            assert authToken != null;
            String username = authToken.username;
            // get body variables
            var body = parseBodyToMap(req.body());
            var gameID = body.get("gameID");
            if (gameID == null) {
                throw new ServiceException(403, "bad request");
            }
            String playerColor = null;
            if (body.containsKey("playerColor")) {
                playerColor = (String) body.get("playerColor");
            }
            int gameIDAsInt = gameID instanceof Double ? (int) Math.floor((Double) gameID) : Integer.parseInt((String) gameID);
            if (this.service.gameDAO.getGameByID(gameIDAsInt) == null) {
                ResponseMapper.exceptionResponse(400, "game does not exist.", res);
                return null;
            }
            // run service
            String gameName = this.service.joinGame(gameIDAsInt, username, playerColor);
            // return successful response
            ResponseMapper.joinGameResponse(res, gameName);
        } catch (ServiceException e) {
            ResponseMapper.exceptionResponse(e.statusCode, e.statusMessage, res);
        }
        return null;
    };

    public Route leaveGame = (req, res) -> {
        try {
            // get authorization
            AuthToken authToken = getAuthToken(req, service.authDAO);
            assert authToken != null;
            String username = authToken.username;
            // get body variables
            var body = parseBodyToMap(req.body());
            var gameID = body.get("gameID");
            if (gameID == null) {
                throw new ServiceException(403, "bad request");
            }
            int gameIDAsInt = gameID instanceof Double ? (int) Math.floor((Double) gameID) : Integer.parseInt((String) gameID);
            if (this.service.gameDAO.getGameByID(gameIDAsInt) == null) {
                ResponseMapper.exceptionResponse(400, "game does not exist.", res);
                return null;
            }
            // run service
            this.service.leaveGame(gameIDAsInt, username);
            // return successful response
            ResponseMapper.leaveGameResponse(res, gameIDAsInt);
        } catch (ServiceException e) {
            ResponseMapper.exceptionResponse(e.statusCode, e.statusMessage, res);
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
