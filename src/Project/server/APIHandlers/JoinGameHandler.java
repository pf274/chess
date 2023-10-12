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
     * Creates a new JoinGameHandler and defines its routes.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     * @throws ClassNotFoundException if the service class is not found
     */
    public JoinGameHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) throws ClassNotFoundException {
        super(authDAO, userDAO, gameDAO, "JoinGameService");

        // join game
        addRoute(new APIRoute(method.POST, "/game/join", parsedRequest -> {
            try {
                // get variables
                int GameID = Integer.parseInt(parsedRequest.getBody().get("GameID"));
                String username = parsedRequest.getBody().get("username");
                String playerColor = parsedRequest.getBody().get("playerColor");
                // run service
                JoinGameService joinGameService = (JoinGameService) this.service;
                joinGameService.joinGame(GameID, username, playerColor);
                // return successful response
                return new JoinGameResponse();
            } catch (ServiceException e) {
                throw new APIException(e.getMessage());
            }
        }));

        // leave game
        addRoute(new APIRoute(method.POST, "/game/leave", parsedRequest -> {
            try {
                // get variables
                int GameID = Integer.parseInt(parsedRequest.getBody().get("GameID"));
                String username = parsedRequest.getBody().get("username");
                // run service
                JoinGameService joinGameService = (JoinGameService) this.service;
                joinGameService.leaveGame(GameID, username);
                // return successful response
                return new LeaveGameResponse();
            } catch (ServiceException e) {
                throw new APIException(e.getMessage());
            }
        }));
    }
}
