package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClearServiceTest {
    private ClearService clearService;
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        UserDao userDao = new LocalUserDao();
        AuthTokenDao authDao = new LocalAuthTokenDao();
        GameDao gameDao = new LocalGameDao();

        clearService = new ClearService(userDao, authDao, gameDao);
        userService = new UserService(userDao, authDao);
        gameService = new GameService(gameDao);
    }

    @Test
    public void clearUserData() {
        String username = "ABC";
        userService.register(new UserData(username, "secure", "abc@123.com"));

        clearService.clear();

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUser(username));
    }

    // TODO: write clearAuthData() test
}
