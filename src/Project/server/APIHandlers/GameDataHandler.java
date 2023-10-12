package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.Game;
import server.Responses.*;
import server.Services.GameDataService;
import server.Services.ServiceException;

import java.util.ArrayList;

/**
 * This class is used to handle the API requests that are related to game data, such as resetting the database.
 */
public class GameDataHandler extends HandlerBase {
    /**
     * The service instance that this handler will use to handle requests.
     */
    private final GameDataService service;

    /**
     * Creates a new GameDataHandler and defines its routes.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public GameDataHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.service = new GameDataService(authDAO, userDAO, gameDAO);

        // list games
        addRoute(new APIRoute(method.GET, "/game/list", parsedRequest -> {
            try {
                // run service
                ArrayList<Game> games = this.service.listGames();
                // return successful response
                return new ListGamesResponse(games);
            } catch (ServiceException e) {
                throw new APIException(e.getMessage());
            }
        }));

        // clear database
        addRoute(new APIRoute(method.DELETE, "/game/clear", parsedRequest -> {
            try {
                // run service
                this.service.clear();
                // return successful response
                return new ClearDatabaseResponse();
            } catch (ServiceException e) {
                throw new APIException(e.getMessage());
            }
        }));

        // create game
        addRoute(new APIRoute(method.POST, "/game/create", parsedRequest -> {
            try {
                // get variables
                String gameName = parsedRequest.getBody().get("gameName");
                // run service
                Game newGame = this.service.createGame(gameName);
                // return successful response
                return new CreateGameResponse(newGame.gameID);
            } catch (ServiceException e) {
                throw new APIException(e.getMessage());
            }
        }));
    }
}
