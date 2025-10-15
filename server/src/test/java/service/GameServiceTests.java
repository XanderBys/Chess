package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTests {
    private GameDao gameDao;
    private GameService gameService;
    private String authToken;

    @BeforeEach
    public void setUp() {
        AuthTokenDao authDao = new LocalAuthTokenDao();
        UserDao userDao = new LocalUserDao();
        gameDao = new LocalGameDao();

        gameService = new GameService(gameDao, authDao);
        UserService userService = new UserService(userDao, authDao);

        String username = "Xman";
        String password = "password";
        String email = "123@abc.com";

        authToken = userService.register(new UserData(username, password, email)).authToken();
    }

    @Test
    public void createGameNormal() {
        int gameId = gameService.createGame(authToken, "name");

        Assertions.assertNotNull(gameDao.getGameDataById(gameId));
    }

    // TODO: write negative tests for createGame and all tests for listGames
}
