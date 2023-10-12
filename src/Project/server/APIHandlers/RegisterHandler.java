package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.AuthToken;
import server.Responses.APIResponse;
import server.Responses.RegisterResponse;
import server.Services.RegisterService;
import server.Services.ServiceException;

/**
 * This class is used to handle the API requests that are related to registering.
 */
public class RegisterHandler extends HandlerBase {


    /**
     * Creates a new RegisterHandler and defines its routes.
     * @param authDAO - an instance of AuthDAO created in the Server class
     * @param userDAO - an instance of UserDAO created in the Server class
     * @param gameDAO - an instance of GameDAO created in the Server class
     * @throws ClassNotFoundException if the service class is not found
     */
    public RegisterHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) throws ClassNotFoundException {
        super(authDAO, userDAO, gameDAO, "RegisterService");

        // register
        addRoute(new APIRoute(method.POST, "/user/register", parsedRequest -> {
            try {
                // get variables
                String username = parsedRequest.getBody().get("username");
                String password = parsedRequest.getBody().get("password");
                String email = parsedRequest.getBody().get("email");
                // run service
                RegisterService registerService = (RegisterService) this.service;
                registerService.register(username, password, email);
                AuthToken authToken = new AuthToken(username);
                // return response
                return new RegisterResponse(username, authToken);
            } catch (ServiceException e) {
                throw new APIException(e.getMessage());
            }
        }));
    }
}
