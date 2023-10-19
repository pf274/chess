package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.Game;
import server.Responses.*;
import server.Services.GameDataService;
import server.Services.ServiceException;
import spark.Route;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used to handle the API requests that are related to game data, such as resetting the database.
 */
public class GameDataHandler extends HandlerBase {
    /**
     * The service instance that this handler will use to handle requests.
     */
    private GameDataService service = null;

    /**
     * Creates a new GameDataHandler and defines its routes.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public GameDataHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.service = new GameDataService(authDAO, userDAO, gameDAO);
    }

    public Route clearDatabase = (req, res) -> {
        try {
            // run service
            this.service.clear();
            // return successful response
            var response = ResponseMaker.clearDatabaseResponse();
            res.status(response.statusCode);
            res.body(response.statusMessage);
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
        return null;
    };

    public Route getGameList = (req, res) -> {
        try {
            // run service
            ArrayList<Game> games = this.service.listGames();
            // return successful response
            var response = ResponseMaker.listGamesResponse(games);
            res.status(response.statusCode);
            res.body(response.statusMessage);
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
        return null;
    };

    public Route createGame = (req, res) -> {
        try {
            // get variables
            HashMap body = parseBodyToMap(req.body());
            String gameName = (String) body.get("gameName");
            if (gameName == null) {
                return ResponseMaker.exceptionResponse(400, "bad request");
            }
            // run service
            Game newGame = this.service.createGame(gameName);
            // return successful response
            var response = ResponseMaker.createGameResponse(newGame.gameID);
            res.status(response.statusCode);
            res.body(response.statusMessage);
        } catch (ServiceException e) {
            return ResponseMaker.exceptionResponse(e.statusCode, e.message);
        }
        return null;
    };
}
