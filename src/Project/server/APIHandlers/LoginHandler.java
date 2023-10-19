package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.AuthToken;
import server.Responses.ResponseMaker;
import server.Services.LoginService;
import server.Services.ServiceException;
import spark.Route;

import java.util.ArrayList;

/**
 * This class is used to handle the API requests that are related to logging in and out.
 */
public class LoginHandler extends HandlerBase {

    /**
     * The service instance that this handler will use to handle requests.
     */
    private LoginService service = null;
    public Route newSession = (req, res) -> {
        try {
            // get variables
            var body = parseBodyToMap(req.body());
            String username = (String) body.get("username");
            String password = (String) body.get("password");
            if (username == null || password == null) {
                throw new ServiceException(401, "username and password required");
            }
            // run service
            if (this.service.userDAO.getUser(username) == null) {
                throw new ServiceException(401, "username not found");
            }
            AuthToken authToken = this.service.login(username, password);
            // return response
            if (authToken == null) {
                return ResponseMaker.unauthorizedResponse();
            }
            var response = ResponseMaker.loginResponse(authToken);
            res.status(response.statusCode);
            res.body(response.statusMessage);
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
        return null;
    };

    public Route endSession = (req, res) -> {
        try {
            // get variables
            var body = parseBodyToMap(req.body());
            String username = (String) body.get("username");
            // run service
            this.service.logout(username);
            // return successful response
            var response = ResponseMaker.logoutResponse();
            res.status(response.statusCode);
            res.body(response.statusMessage);
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
        return null;
    };

    /**
     * Creates a new LoginHandler and defines its routes.
     * @param authDAO an instance of AuthDAO created in the Server class
     * @param userDAO an instance of UserDAO created in the Server class
     * @param gameDAO an instance of GameDAO created in the Server class
     */
    public LoginHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.service = new LoginService(authDAO, userDAO, gameDAO);
    }
}
