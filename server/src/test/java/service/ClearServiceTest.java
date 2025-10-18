package service;

import dataaccess.*;
import handlers.results.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClearServiceTest {
    private ClearService clearService;
    private UserService userService;

    private UserDao userDao;
    private AuthTokenDao authDao;

    @BeforeEach
    public void setUp() {
        userDao = new LocalUserDao();
        authDao = new LocalAuthTokenDao();
        GameDao gameDao = new LocalGameDao();

        clearService = new ClearService(userDao, authDao, gameDao);
        userService = new UserService(userDao, authDao);
        GameService gameService = new GameService(gameDao, authDao);
    }

    @Test
    public void clearUserAndAuthData() {
        String username = "ABC";
        RegisterResult result = userService.register(new UserData(username, "secure", "abc@123.com"));

        clearService.clear();

        Assertions.assertNull(userDao.getUserData(username));
        Assertions.assertNull(authDao.getAuth(result.authToken()));
    }
}