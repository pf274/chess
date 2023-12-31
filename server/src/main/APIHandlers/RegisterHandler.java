package APIHandlers;

import DAO.AuthDAO;
import DAO.GameDAO;
import DAO.UserDAO;
import Models.AuthToken;
import Responses.ResponseMapper;
import Services.RegisterService;
import Services.ServiceException;
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

    /**
     * The route handler that handles the register request.
     */
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
            // return response
            ResponseMapper.registerResponse(authToken, res);
        } catch (ServiceException e) {
            ResponseMapper.exceptionResponse(e.statusCode, e.statusMessage, res);
        }
        return null;
    };
}
