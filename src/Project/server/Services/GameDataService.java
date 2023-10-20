package server.Services;

import server.DAO.AuthDAO;
import server.DAO.DataAccessException;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.Game;
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
     * @param shouldFail If true, the service will throw an exception (to simulate failure in future phases)
     * @return The list of games
     * @throws ServiceException If the games could not be listed
     */
    public ArrayList<Game> listGames(boolean shouldFail) throws ServiceException {
        try {
            if (shouldFail) {
                throw new ServiceException(500, "internal server error");
            }
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
