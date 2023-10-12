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
     * Creates a new LoginHandler with the given DAOs.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     * @throws ClassNotFoundException if the service class is not found
     */
    public LoginHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) throws ClassNotFoundException {
        super(authDAO, userDAO, gameDAO, "LoginService");
    }

    /**
     * Logs in the user with the given username and password.
     * @param username the user's username
     * @param password the user's password
     * @return a success or failure message
     * @throws APIException if logging in fails for an unexpected reason
     */
    public APIResponse login(String username, String password) throws APIException {
        try {
            LoginService loginService = (LoginService) this.service;
            AuthToken authToken = loginService.login(username, password);
            if (authToken == null) {
                return new APIResponse(401, "Error: unauthorized");
            }
            return new LoginResponse(username, authToken);
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
    }

    /**
     * Logs out the user with the given username.
     * @param username the user's username
     * @return a success message
     * @throws APIException if logging out fails
     */
    public APIResponse logout(String username) throws APIException {
        try {
            LoginService loginService = (LoginService) this.service;
            loginService.logout(username);
            return new LogoutResponse();
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
    }
}
