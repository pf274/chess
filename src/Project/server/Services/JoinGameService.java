package server.Services;

import server.DAO.AuthDAO;
import server.DAO.DataAccessException;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.User;

import java.util.Objects;

/**
 * Functionality for joining and leaving games
 */
public class JoinGameService extends ServiceBase {
    /**
     * Creates a new Service with the given DAOs.
     *
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public JoinGameService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        super(authDAO, userDAO, gameDAO);
    }

    /**
     * Adds the user with the given username to the game with the given GameID.
     *
     * @param gameID the ID of the game to join
     * @param username the username of the user to join
     * @param playerColor the color of the player
     * @throws ServiceException If the user could not join the game
     */
    public void joinGame(int gameID, String username, String playerColor) throws ServiceException {
        try {
            User.Color color = Objects.equals(playerColor, "white") ? User.Color.WHITE : User.Color.BLACK;
            gameDAO.addUserToGame(gameID, username, color);
        } catch (DataAccessException e) {
            throw new ServiceException(500, e.getMessage());
        }
    }

    /**
     * Removes the user with the given username from the game with the given GameID.
     * @param gameID the ID of the game to leave
     * @param username the username of the user to leave
     * @throws ServiceException If the user could not leave the game
     */
    public void leaveGame(int gameID, String username) throws ServiceException {
        try {
            gameDAO.removeUserFromGame(gameID, username);
        } catch (DataAccessException e) {
            throw new ServiceException(500, e.getMessage());
        }
    }
}
