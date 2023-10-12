package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Responses.APIResponse;
import server.Responses.JoinGameResponse;
import server.Responses.LeaveGameResponse;
import server.Services.GameDataService;
import server.Services.JoinGameService;
import server.Services.ServiceException;

/**
 * This class is used to handle the API requests that are related to joining and leaving games.
 */
public class JoinGameHandler extends HandlerBase {
    /**
     * The service instance that this handler will use to handle requests.
     */
    private final JoinGameService service;
    /**
     * Creates a new JoinGameHandler and defines its routes.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public JoinGameHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.service = new JoinGameService(authDAO, userDAO, gameDAO);

        // join game
        addRoute(new APIRoute(method.POST, "/game/join", parsedRequest -> {
            try {
                // get variables
                int GameID = Integer.parseInt(parsedRequest.getBody().get("GameID"));
                String username = parsedRequest.getBody().get("username");
                String playerColor = parsedRequest.getBody().get("playerColor");
                // run service
                this.service.joinGame(GameID, username, playerColor);
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
                this.service.leaveGame(GameID, username);
                // return successful response
                return new LeaveGameResponse();
            } catch (ServiceException e) {
                throw new APIException(e.getMessage());
            }
        }));
    }
}
