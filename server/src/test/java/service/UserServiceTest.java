package service;

import dataaccess.LocalAuthTokenDao;
import dataaccess.LocalUserDao;
import dataaccess.UserDao;
import handlers.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTest {
    private UserService userService;

    private final String username = "Xman";
    private final String password = "very secure";
    private final String email = "abc@gmail.com";
    private UserData newUser;

    @BeforeEach
    public void setUp() {
        UserDao userDao = new LocalUserDao();
        AuthDao authDao = new LocalAuthTokenDao();

        userService = new UserService(userDao, authDao);
        newUser = new UserData(username, password, email);
    }

    @Test
    public void registerUser() {
        RegisterResult result = userService.register(newUser);

        Assertions.assertEquals(username, result.username());
        Assertions.assertEquals(username, result.authData().username());
    }

    @Test
    public void registerMultipleUsers() {
        for (int i = 0; i < 100; i++) {
            String newUsername = Integer.toString(i);
            UserData user = new UserData(newUsername, password, email);
            RegisterResult result = userService.register(user);

            Assertions.assertEquals(newUsername, result.username());
            Assertions.assertEquals(newUsername, result.authData().username());
        }
    }

    @Test
    public void usernameTaken() {
        userService.register(newUser);

        UserData identicalUser = new UserData(username, password, email);
        UserData sameUsername = new UserData(username, "12345", "none");

        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(identicalUser));
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(sameUsername));
    }
}
