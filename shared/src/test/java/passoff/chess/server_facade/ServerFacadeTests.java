package passoff.chess.server_facade;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.requests.LoginRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ResponseException;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

public class ServerFacadeTests {
    private static final int port = 8080;
    private static final String serverUrl = "http://localhost:" + port;

    private ServerFacade sf;

    private final String username = "xman";
    private final String password = "abc123";
    private final String email = "test@test.com";
    private final UserData newUser = new UserData(username, password, email);
    private final LoginRequest loginRequest = new LoginRequest(newUser.username(), newUser.password());

    @BeforeEach
    public void setUp() throws URISyntaxException, IOException, InterruptedException {
        sf = new ServerFacade(serverUrl);
        sf.register(newUser);
    }

    @AfterEach
    public void takeDown() throws URISyntaxException, IOException, InterruptedException {
        sf.clear();
    }

    @Test
    public void testRegister() throws URISyntaxException, IOException, InterruptedException {
        AuthData ad = sf.register(new UserData("abc", "ehgklsag", "qrstuv"));
        Assertions.assertNotNull(ad);
    }

    @Test
    public void negativeTestRegister() throws URISyntaxException, IOException, InterruptedException {
        UserData testUser = new UserData("abc", "ehgklsag", "qrstuv");
        sf.register(testUser);
        Assertions.assertThrows(ResponseException.class, () -> sf.register(testUser));
    }

    @Test
    public void testLogin() throws URISyntaxException, IOException, InterruptedException {
        AuthData ad = sf.login(loginRequest);
        Assertions.assertNotNull(ad);
    }

    @Test
    public void negativeTestLogin() {
        Assertions.assertThrows(ResponseException.class,
                () -> sf.login(new LoginRequest(username, "wrong password")));

    }

    //TODO: add ServerFacade.createGame so I can use this test
    @Test
    public void testLogout() throws URISyntaxException, IOException, InterruptedException {
        AuthData ad = sf.login(loginRequest);
        sf.logout(ad);

        Assertions.assertThrows(ResponseException.class, () -> sf.createGame("testGame", ad));
    }

    @Test
    public void createGame() throws URISyntaxException, IOException, InterruptedException {
        AuthData ad = sf.login(loginRequest);
        int gameID = sf.createGame("testing", ad);

        Assertions.assertEquals(1, gameID);
    }

    @Test
    public void createDuplicateGameFails() throws URISyntaxException, IOException, InterruptedException {
        AuthData ad = sf.login(loginRequest);
        sf.createGame("game1", ad);
        Assertions.assertThrows(ResponseException.class, () -> sf.createGame("game1", ad));
    }

    @Test
    public void listGames() throws URISyntaxException, IOException, InterruptedException {
        AuthData ad = sf.login(loginRequest);
        sf.createGame("game1", ad);
        sf.createGame("game2", ad);
        sf.createGame("game3", ad);
        Collection<GameData> gameList = sf.getGameList(ad);

        Assertions.assertEquals(3, gameList.size());
    }

    @Test
    public void listGamesUnauthorized() throws URISyntaxException, IOException, InterruptedException {
        AuthData ad = sf.login(loginRequest);
        sf.createGame("game1", ad);

        Assertions.assertThrows(ResponseException.class,
                () -> sf.getGameList(new AuthData(username, "not an auth token")));
    }

    @Test
    public void joinGameTest() throws URISyntaxException, IOException, InterruptedException {
        AuthData ad = sf.login(loginRequest);
        int id = sf.createGame("game1", ad);
        sf.joinGame(ChessGame.TeamColor.WHITE, id, ad);

        Collection<GameData> gamelist = sf.getGameList(ad);
        GameData gameData = null;
        for (GameData game : gamelist) {
            gameData = game;
        }

        Assertions.assertNotNull(gameData);
        Assertions.assertEquals(username, gameData.whiteUsername());
    }

    @Test
    public void joinNonexistentGameTest() throws URISyntaxException, IOException, InterruptedException {
        AuthData ad = sf.login(loginRequest);

        Assertions.assertThrows(ResponseException.class,
                () -> sf.joinGame(ChessGame.TeamColor.WHITE, 1, ad));
    }
}
