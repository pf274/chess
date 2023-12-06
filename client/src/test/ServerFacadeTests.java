import Models.AuthToken;
import Responses.APIResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.facades.ServerFacade;

import java.util.HashMap;

public class ServerFacadeTests {

    private String registeredUsername = "testUser";

    private String registeredPassword = "testPassword";

    private String registeredEmail = "test@gmail.com";

    private AuthToken registeredAuthToken = null;

    @BeforeEach
    public void setup() {
        // clear database
        ServerFacade serverFacade = ServerFacade.getInstance();
        serverFacade.clearDatabase();
        // register user
        APIResponse registerResponse = serverFacade.register(registeredUsername, registeredPassword, registeredEmail);
        HashMap registerResponseMap = new Gson().fromJson(registerResponse.statusMessage, HashMap.class);
        registeredAuthToken = new AuthToken(registeredUsername);
        registeredAuthToken.authToken = registerResponseMap.get("authToken").toString();
    }

    @Test
    @DisplayName("ServerFacade: login (success)")
    public void testLoginSuccess() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse loginResponse = serverFacade.login(registeredUsername, registeredPassword);
        HashMap loginResponseMap = new Gson().fromJson(loginResponse.statusMessage, HashMap.class);
        assert loginResponseMap.get("authToken") != null;
    }

    @Test
    @DisplayName("ServerFacade: login (failure)")
    public void testLoginFailure() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse loginResponse = serverFacade.login(registeredUsername, "wrongPassword");
        assert loginResponse.statusCode == 401;
    }

    @Test
    @DisplayName("ServerFacade: register (success)")
    public void testRegisterSuccess() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse registerResponse = serverFacade.register("testUser2", "testPassword2", "testemail@email.com");
        HashMap registerResponseMap = new Gson().fromJson(registerResponse.statusMessage, HashMap.class);
        assert registerResponseMap.get("authToken") != null;
    }

    @Test
    @DisplayName("ServerFacade: register (failure)")
    public void testRegisterFailure() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse registerResponse = serverFacade.register(registeredUsername, registeredPassword, registeredEmail);
        assert registerResponse.statusCode == 403;
    }

    @Test
    @DisplayName("ServerFacade: logout (success)")
    public void testLogoutSuccess() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse logoutResponse = serverFacade.logout(registeredAuthToken.authToken);
        assert logoutResponse.statusCode == 200;
    }

    @Test
    @DisplayName("ServerFacade: logout (failure)")
    public void testLogoutFailure() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse logoutResponse = serverFacade.logout("fakeAuthToken");
        assert logoutResponse.statusCode == 401;
    }

    @Test
    @DisplayName("ServerFacade: createGame (success)")
    public void testCreateGameSuccess() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse createGameResponse = serverFacade.createGame("testGame", registeredAuthToken.authToken);
        assert createGameResponse.statusCode == 200;
    }

    @Test
    @DisplayName("ServerFacade: createGame (failure)")
    public void testCreateGameFailure() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse createGameResponse = serverFacade.createGame("testGame", "fakeAuthToken");
        assert createGameResponse.statusCode == 401;
    }

    @Test
    @DisplayName("ServerFacade: listGames (success)")
    public void testListGamesSuccess() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse listGamesResponse = serverFacade.listGames(registeredAuthToken.authToken);
        assert listGamesResponse.statusCode == 200;
    }

    @Test
    @DisplayName("ServerFacade: listGames (failure)")
    public void testListGamesFailure() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse listGamesResponse = serverFacade.listGames("fakeAuthToken");
        assert listGamesResponse.statusCode == 401;
    }

    @Test
    @DisplayName("ServerFacade: joinGame (success)")
    public void testJoinGameSuccess() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse createGameResponse = serverFacade.createGame("testGame", registeredAuthToken.authToken);
        HashMap createGameResponseMap = new Gson().fromJson(createGameResponse.statusMessage, HashMap.class);
        String rawGameID = createGameResponseMap.get("gameID").toString();
        int gameID = (int) Float.parseFloat(rawGameID);
        APIResponse joinGameResponse = serverFacade.joinGame(registeredAuthToken.authToken, gameID, "white");
        assert joinGameResponse.statusCode == 200;
    }

    @Test
    @DisplayName("ServerFacade: joinGame (failure)")
    public void testJoinGameFailure() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse createGameResponse = serverFacade.createGame("testGame", registeredAuthToken.authToken);
        HashMap createGameResponseMap = new Gson().fromJson(createGameResponse.statusMessage, HashMap.class);
        String rawGameID = createGameResponseMap.get("gameID").toString();
        int gameID = (int) Float.parseFloat(rawGameID);
        APIResponse joinGameResponse = serverFacade.joinGame("fakeAuthToken", gameID, "white");
        assert joinGameResponse.statusCode == 401;
    }

    @Test
    @DisplayName("ServerFacade: clearDatabase (success)")
    public void testClearDatabaseSuccess() {
        ServerFacade serverFacade = ServerFacade.getInstance();
        APIResponse clearDatabaseResponse = serverFacade.clearDatabase();
        assert clearDatabaseResponse.statusCode == 200;
    }
}
