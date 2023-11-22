package Services;

import DAO.*;
import Models.Game;

import java.util.ArrayList;

/**
 * Services dealing with game data
 */
public class GameDataService extends ServiceBase {

    /**
     * Creates a new Service with the given DAOs.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public GameDataService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        super(authDAO, userDAO, gameDAO);
    }

    /**
     * Clears all data from the database
     * @throws ServiceException If the data could not be cleared
     */
    public void clear() throws ServiceException {
        try {
            authDAO.clear();
            userDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw new ServiceException(500, e.getMessage());
        }
    }

    /**
     * Lists all the games in the database
     * @return The list of games
     * @throws ServiceException If the games could not be listed
     */
    public ArrayList<Game> listGames() throws ServiceException {
        try {
            return gameDAO.getAllGames();
        } catch (DataAccessException e) {
            throw new ServiceException(e.statusCode, e.statusMessage);
        }
    }

    /**
     * Creates a new game
     * @param gameName The name of the game
     * @return The game
     */
    public Game createGame(String gameName) throws ServiceException {
        try {
            if (gameDAO.getGameByGameName(gameName) != null) {
                throw new ServiceException(400, "bad request");
            }
            return gameDAO.addGame(gameName);
        } catch (DataAccessException e) {
            throw new ServiceException(e.statusCode, e.statusMessage);
        }
    }
}
