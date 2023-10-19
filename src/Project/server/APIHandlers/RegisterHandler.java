package server.APIHandlers;

import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.AuthToken;
import server.Responses.ResponseMaker;
import server.Services.RegisterService;
import server.Services.ServiceException;
import spark.Route;

/**
 * This class is used to handle the API requests that are related to registering.
 */
public class RegisterHandler extends HandlerBase {

    /**
     * The service instance that this handler will use to handle requests.
     */
    private RegisterService service = null;

    /**
     * Creates a new RegisterHandler and defines its routes.
     * @param authDAO - an instance of AuthDAO created in the Server class
     * @param userDAO - an instance of UserDAO created in the Server class
     * @param gameDAO - an instance of GameDAO created in the Server class
     */
    public RegisterHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.service = new RegisterService(authDAO, userDAO, gameDAO);
    }

    public Route register = (req, res) -> {
        try {
            // get variables
            var body = parseBodyToMap(req.body());
            String username = (String) body.get("username");
            String password = (String) body.get("password");
            String email = (String) body.get("email");
            if (username == null || password == null || email == null) {
                throw new ServiceException(400, "username, password, and email required");
            }
            // run service
            AuthToken authToken = this.service.register(username, password, email);
            System.out.println(authToken.authToken);
            // return response
            var response = ResponseMaker.registerResponse(authToken);
            res.status(response.statusCode);
            res.body(response.statusMessage);
        } catch (ServiceException e) {
            throw new APIException(e.getMessage());
        }
        return null;
    };
}
