package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.AuthToken;
import server.Responses.APIResponse;
import server.Responses.LoginResponse;
import server.Responses.LogoutResponse;
import server.Services.LoginService;
import server.Services.ServiceException;

/**
 * This class is used to handle the API requests that are related to logging in and out.
 */
public class LoginHandler extends HandlerBase {


    /**
     * Creates a new LoginHandler and defines its routes.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     * @throws ClassNotFoundException if the service class is not found
     */
    public LoginHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) throws ClassNotFoundException {
        super(authDAO, userDAO, gameDAO, "LoginService");

        // login
        addRoute(new APIRoute(method.POST, "/user/login", parsedRequest -> {
            try {
                // get variables
                String username = parsedRequest.getBody().get("username");
                String password = parsedRequest.getBody().get("password");
                // run service
                LoginService loginService = (LoginService) this.service;
                AuthToken authToken = loginService.login(username, password);
                // return response
                if (authToken == null) {
                    return new APIResponse(401, "Error: unauthorized");
                }
                return new LoginResponse(username, authToken);
            } catch (ServiceException e) {
                throw new APIException(e.getMessage());
            }
        }));

        // logout
        addRoute(new APIRoute(method.POST, "/user/logout", parsedRequest -> {
            try {
                // get variables
                String username = parsedRequest.getBody().get("username");
                // run service
                LoginService loginService = (LoginService) this.service;
                loginService.logout(username);
                // return successful response
                return new LogoutResponse();
            } catch (ServiceException e) {
                throw new APIException(e.getMessage());
            }
        }));
    }
}
