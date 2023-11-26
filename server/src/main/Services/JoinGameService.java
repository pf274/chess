package Services;

import DAO.*;
import java.util.Objects;

/**
 * Services for joining and leaving games
 */
public class JoinGameService extends ServiceBase {
    /**
     * Creates a new Service with the given DAOs.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public JoinGameService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        super(authDAO, userDAO, gameDAO);
    }

    /**
     * Adds the user with the given username to the game with the given GameID.
     * @param gameID the ID of the game to join
     * @param username the username of the user to join
     * @param playerColor the color of the player
     * @throws ServiceException If the user could not join the game
     */
    public String joinGame(int gameID, String username, String playerColor) throws ServiceException {
        try {
            var foundGame = gameDAO.getGameByID(gameID);
            if (foundGame == null) {
                throw new ServiceException(400, "bad request");
            }
            if (foundGame.blackUsername != null && playerColor != null && Objects.equals(playerColor.toUpperCase(), "BLACK")) {
                throw new ServiceException(403, "black already taken");
            }
            if (foundGame.whiteUsername != null && playerColor != null && Objects.equals(playerColor.toUpperCase(), "WHITE")) {
                throw new ServiceException(403, "white already taken");
            }
            gameDAO.addUserToGame(gameID, username, playerColor);
            return foundGame.gameName;
        } catch (DataAccessException e) {
            throw new ServiceException(500, e.getMessage());
        }
    }
}
