package passoffTests.serverTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.Game;
import server.Services.*;

// TODO: change these tests to test the handlers instead of the services directly
public class ServerUnitTests {

    private AuthDAO authDAO = new AuthDAO();

    private UserDAO userDAO = new UserDAO();

    private GameDAO gameDAO = new GameDAO();

    private GameDataService gameDataService = new GameDataService(authDAO, userDAO, gameDAO);

    private JoinGameService joinGameService = new JoinGameService(authDAO, userDAO, gameDAO);

    private LoginService loginService = new LoginService(authDAO, userDAO, gameDAO);

    private RegisterService registerService = new RegisterService(authDAO, userDAO, gameDAO);

    @BeforeEach
    public void setup() {
        authDAO = new AuthDAO();
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
        gameDataService = new GameDataService(authDAO, userDAO, gameDAO);
        joinGameService = new JoinGameService(authDAO, userDAO, gameDAO);
        loginService = new LoginService(authDAO, userDAO, gameDAO);
        registerService = new RegisterService(authDAO, userDAO, gameDAO);
    }
    @Test
    @Order(1)
    @DisplayName("GameDataService: clear data (success)")
    public void test1() {
        try {
            gameDataService.clear();
            assert true;
        } catch (ServiceException e) {
            assert false;
        }

    }
    @Test
    @Order(2)
    @DisplayName("GameDataService: create game (success)")
    public void test2() {
        try {
            gameDataService.createGame("test");
            assert true;
        } catch (ServiceException e) {
            assert false;
        }
    }

    @Test
    @Order(3)
    @DisplayName("GameDataService: create game (failure)")
    public void test3() {
        // the first time should succeed
        try {
            gameDataService.createGame("test");
        } catch (ServiceException e) {
            assert false;
        }
        // the second time should fail
        try {
            gameDataService.createGame("test");
            assert false;
        } catch (ServiceException e) {
            assert true;
        }
    }
    @Test
    @Order(4)
    @DisplayName("GameDataService: list games (success)")
    public void test4() {
        try {
            gameDataService.listGames();
            assert true;
        } catch (ServiceException e) {
            assert false;
        }
    }

    @Test
    @Order(5)
    @DisplayName("GameDataService: list games (failure)")
    public void test5() {
        try {
            gameDataService.listGames();
            assert true;
        } catch (ServiceException e) {
            assert false;
        }
    }
}
