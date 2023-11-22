package APIHandlers;

import DAO.AuthDAO;
import DAO.GameDAO;
import DAO.UserDAO;
import Models.Game;
import Responses.ResponseMapper;
import Services.GameDataService;
import Services.ServiceException;
import spark.Route;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class is used to handle the API requests that are related to game data, such as resetting the database.
 */
public class GameDataHandler extends HandlerBase {
    /**
     * The service instance that this handler will use to handle requests.
     */
    private GameDataService service;

    /**
     * Creates a new GameDataHandler and defines its routes.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public GameDataHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.service = new GameDataService(authDAO, userDAO, gameDAO);
    }

    /**
     * The route handler that handles the clear database request.
     */
    public Route clearDatabase = (req, res) -> {
        try {
            // run service
            this.service.clear();
            // return successful response
            ResponseMapper.clearDatabaseResponse(res);
        } catch (ServiceException e) {
            ResponseMapper.exceptionResponse(e.statusCode, e.statusMessage, res);
        }
        return null;
    };

    /**
     * The route handler that handles the get game list request.
     */
    public Route getGameList = (req, res) -> {
        try {
            // run service
            ArrayList<Game> games = this.service.listGames();
            // return successful response
            ResponseMapper.listGamesResponse(games, res);
        } catch (ServiceException e) {
            ResponseMapper.exceptionResponse(e.statusCode, e.statusMessage, res);
        }
        return null;
    };

    /**
     * The route handler that handles the create game request.
     */
    public Route createGame = (req, res) -> {
        try {
            // get variables
            HashMap body = parseBodyToMap(req.body());
            String gameName = (String) body.get("gameName");
            if (gameName == null) {
                gameName = "Game " + UUID.randomUUID();
            }
            // run service
            Game newGame = this.service.createGame(gameName);
            // return successful response
            ResponseMapper.createGameResponse(newGame.gameID, res);
        } catch (ServiceException e) {
            ResponseMapper.exceptionResponse(e.statusCode, e.statusMessage, res);
        }
        return null;
    };
}
