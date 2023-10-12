package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Responses.APIResponse;
import server.Responses.JoinGameResponse;
import server.Responses.LeaveGameResponse;
import server.Services.JoinGameService;
import server.Services.ServiceException;

/**
 * This class is used to handle the API requests that are related to joining and leaving games.
 */
public class JoinGameHandler extends HandlerBase {

    /**
     * Creates a new JoinGameHandler with the given DAOs.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     * @throws ClassNotFoundException if the service class is not found
     */
    public JoinGameHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) throws ClassNotFoundException {
        super(authDAO, userDAO, gameDAO, "JoinGameService");
    }

    /**
     * Joins the user with the given username to the game with the given GameID.
     * @param GameID the ID of the game to join
     * @param username the username of the user to join
     * @param playerColor the color of the player
     * @return information about the game
     * @throws APIException if joining the game fails
     */
    public APIResponse joinGame(int GameID, String username, String playerColor) throws APIException {
        try {
            JoinGameService joinGameService = (JoinGameService) this.service;
            joinGameService.joinGame(GameID, username, playerColor);
            return new JoinGameResponse();
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
    }

    /**
     * Removes the user with the given username from the game with the given GameID.
     * @param GameID the ID of the game to leave
     * @return a success message
     * @throws APIException if leaving the game fails
     */
    public APIResponse leaveGame(int GameID, String username) throws APIException {
        try {
            JoinGameService joinGameService = (JoinGameService) this.service;
            joinGameService.leaveGame(GameID, username);
            return new LeaveGameResponse();
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
    }
}
