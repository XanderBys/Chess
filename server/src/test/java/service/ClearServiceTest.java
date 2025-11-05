package service;

import dataaccess.AuthTokenDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import dataaccess.memory.LocalAuthTokenDao;
import dataaccess.memory.LocalGameDao;
import dataaccess.memory.LocalUserDao;
import model.UserData;
import model.results.RegisterResult;
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