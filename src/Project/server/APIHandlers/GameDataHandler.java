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
     * Creates a new GameDataHandler with the given DAOs.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     * @throws ClassNotFoundException if the service class is not found
     */
    public GameDataHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) throws ClassNotFoundException {
        super(authDAO, userDAO, gameDAO, "GameDataService");
    }

    /**
     * Clears the database.
     * @throws APIException if clearing the database fails
     */
    public APIResponse clearDatabase() throws APIException {
        try {
            GameDataService gameDataService = (GameDataService) this.service;
            gameDataService.clear();
            return new ClearDatabaseResponse(200, "Clear succeeded.");
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
    }

    /**
     * Lists all the games in the database.
     * @return the list of games
     * @throws APIException if listing the games fails
     */
    public APIResponse listGames() throws APIException {
        try {
            GameDataService gameDataService = (GameDataService) this.service;
            ArrayList<Game> games = gameDataService.listGames();
            return new ListGamesResponse(games);
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
    }

    /**
     * Creates a new game
     * @return details about the created game
     * @throws APIException if creating the game fails
     */
    public APIResponse createGame(String gameName) throws APIException {
    	try {
            GameDataService gameDataService = (GameDataService) this.service;
            Game newGame = gameDataService.createGame(gameName);
            return new CreateGameResponse(newGame.gameID);
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
    }
}
