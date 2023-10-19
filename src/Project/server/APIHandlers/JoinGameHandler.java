package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.AuthToken;
import server.Responses.ResponseMapper;
import server.Services.JoinGameService;
import server.Services.ServiceException;
import spark.Route;
import java.util.HashMap;


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
            var gameID = body.get("gameID");
            if (gameID == null) {
                throw new ServiceException(403, "bad request");
            }
            String playerColor = (String) body.get("playerColor");
            int gameIDAsInt = (int) Math.floor((Double) gameID);
            if (this.service.gameDAO.getGameByID(gameIDAsInt) == null) {
                ResponseMapper.exceptionResponse(400, "game does not exist.", res);
                return null;
            }
            // run service
            this.service.joinGame(gameIDAsInt, username, playerColor);
            // return successful response
            ResponseMapper.joinGameResponse(res);
        } catch (ServiceException e) {
            throw new APIException(e.statusCode, e.statusMessage);
        }
        return null;
    };


    public Route leaveGame = (req, res) -> {
        try {
            // get variables
            String username = "laseredface";
            HashMap body = parseBodyToMap(req.body());
            int gameID = (int) body.get("gameID");
            // run service
            this.service.leaveGame(gameID, username);
            // return successful response
            ResponseMapper.leaveGameResponse(res);
        } catch (ServiceException e) {
            throw new APIException(e.statusCode, e.statusMessage);
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
