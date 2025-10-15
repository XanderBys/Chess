package service;

import dataaccess.AuthTokenDao;
import dataaccess.LocalAuthTokenDao;
import dataaccess.LocalUserDao;
import dataaccess.UserDao;
import handlers.LoginRequest;
import handlers.RegisterResult;
import model.AuthData;
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
        AuthTokenDao authDao = new LocalAuthTokenDao();

        userService = new UserService(userDao, authDao);
        newUser = new UserData(username, password, email);
    }

    @Test
    public void registerUser() {
        RegisterResult result = userService.register(newUser);

        Assertions.assertEquals(username, result.username());
    }

    @Test
    public void registerMultipleUsers() {
        for (int i = 0; i < 100; i++) {
            String newUsername = Integer.toString(i);
            UserData user = new UserData(newUsername, password, email);
            RegisterResult result = userService.register(user);

            Assertions.assertEquals(newUsername, result.username());
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

    @Test
    public void normalLogin() {
        userService.register(newUser);

        AuthData loginData = userService.login(new LoginRequest(newUser.username(), newUser.password()));

        Assertions.assertEquals(loginData.username(), newUser.username());
        Assertions.assertNotNull(loginData.authToken());
    }

    @Test
    public void loginNoUsername() {
        userService.register(newUser);

        Assertions.assertThrows(BadRequestException.class, () -> userService.login(new LoginRequest("", newUser.password())));
    }

    @Test
    public void loginInvalidCredentials() {
        userService.register(newUser);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> userService.login(new LoginRequest(newUser.username(), "passssword")));
    }

    @Test
    public void logoutNormal() {
        userService.register(newUser);
        AuthData authData = userService.login(new LoginRequest(username, password));

        userService.logout(authData.authToken());

        Assertions.assertThrows(UnauthorizedException.class, () -> userService.validateAuthData(authData.authToken()));
    }

    @Test
    public void logoutUnauthorized() {
        userService.register(newUser);
        AuthData authData = userService.login(new LoginRequest(username, password));

        Assertions.assertThrows(UnauthorizedException.class, () -> userService.logout("notavalidauthtoken"));
    }
}
