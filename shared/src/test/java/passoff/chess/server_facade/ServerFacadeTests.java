package passoff.chess.server_facade;

import model.AuthData;
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
}
