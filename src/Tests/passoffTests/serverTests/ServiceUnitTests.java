package passoffTests.serverTests;

import org.junit.jupiter.api.*;
import server.DAO.AuthDAO;
import server.DAO.GameDAO;
import server.DAO.UserDAO;
import server.Models.AuthToken;
import server.Models.Game;
import server.Services.*;

public class ServiceUnitTests {
    private GameDataService gameDataService = null;
    private JoinGameService joinGameService = null;
    private LoginService loginService = null;
    private RegisterService registerService = null;
    private AuthToken existingUser = null;
    private Game existingGame = null;

    @BeforeEach
    public void setup() {
        AuthDAO authDAO = new AuthDAO();
        UserDAO userDAO = new UserDAO();
        GameDAO gameDAO = new GameDAO();
        gameDataService = new GameDataService(authDAO, userDAO, gameDAO);
        joinGameService = new JoinGameService(authDAO, userDAO, gameDAO);
        loginService = new LoginService(authDAO, userDAO, gameDAO);
        registerService = new RegisterService(authDAO, userDAO, gameDAO);
        try {
            existingUser = registerService.register("testUser", "testPassword", "testEmail@gmail.com");
        } catch (ServiceException e) {
            assert false;
        }
        try {
            existingGame = gameDataService.createGame("testGame");
        } catch (ServiceException e) {
            assert false;
        }
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
            // simulates when there is a game, but it is not returned by listGames().
            gameDataService.clear();
            var games = gameDataService.listGames();
            assert games.isEmpty();
        } catch (ServiceException e) {
            assert true;
        }
    }

    @Test
    @Order(6)
    @DisplayName("JoinGameService: join game (success)")
    public void test6() {
        // registering should pass
        try {
            joinGameService.joinGame(existingGame.gameID, existingUser.username, "WHITE");
            assert true;
        } catch (ServiceException e) {
            assert false;
        }
    }

    @Test
    @Order(7)
    @DisplayName("JoinGameService: join game (failure)")
    public void test7() {
        try {
            joinGameService.joinGame(1234, existingUser.username, "WHITE");
            assert false;
        } catch (ServiceException e) {
            assert true;
        }
    }

    @Test
    @Order(8)
    @DisplayName("LoginService: login (success)")
    public void test8() {
        try {
            loginService.login("testUser", "testPassword");
            assert true;
        } catch (ServiceException e) {
            assert false;
        }
    }

    @Test
    @Order(9)
    @DisplayName("LoginService: login (failure)")
    public void test9() {
        try {
            loginService.login("fakeUser", "fakePassword");
            assert false;
        } catch (ServiceException e) {
            assert true;
        }
    }

    @Test
    @Order(10)
    @DisplayName("RegisterService: register (success)")
    public void test10() {
        try {
            registerService.register("testUser2", "testPassword2", "testEmail2@gmail.com");
            assert true;
        } catch (ServiceException e) {
            assert false;
        }
    }

    @Test
    @Order(11)
    @DisplayName("RegisterService: register (failure)")
    public void test11() {
        try {
            registerService.register("testUser", "testPassword", "testEmail@gmail.com");
            assert false;
        } catch (ServiceException e) {
            assert true;
        }
    }

    @Test
    @Order(12)
    @DisplayName("LoginService: Logout (success)")
    public void test12() {
        try {
            loginService.logout("testUser");
            assert true;
        } catch (ServiceException e) {
            assert false;
        }
    }

    @Test
    @Order(13)
    @DisplayName("LoginService: logout (failure)")
    public void test13() {
        try {
            loginService.logout("fakeUser");
            assert false;
        } catch (ServiceException e) {
            assert true;
        }
    }
}
