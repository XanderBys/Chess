package service;

import dataaccess.AuthTokenDao;
import dataaccess.UserDao;
import dataaccess.memory.LocalAuthTokenDao;
import dataaccess.memory.LocalUserDao;
import model.AuthData;
import model.UserData;
import model.requests.LoginRequest;
import model.results.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTest {
    private UserService userService;

    private final String username = "Xman";
    private final String password = "very secure";
    private final String email = "abc@gmail.com";
    private UserData newUser;

    private AuthTokenDao authDao;

    @BeforeEach
    public void setUp() {
        UserDao userDao = new LocalUserDao();
        authDao = new LocalAuthTokenDao();

        userService = new UserService(userDao, authDao);
        newUser = new UserData(username, password, email);
    }

    /**
     * Positive test for register service
     */
    @Test
    public void registerUser() {
        RegisterResult result = userService.register(newUser);

        Assertions.assertEquals(username, result.username());
    }

    /**
     * positive test for register service
     */
    @Test
    public void registerMultipleUsers() {
        for (int i = 0; i < 100; i++) {
            String newUsername = Integer.toString(i);
            UserData user = new UserData(newUsername, password, email);
            RegisterResult result = userService.register(user);

            Assertions.assertEquals(newUsername, result.username());
        }
    }

    /**
     * negative test for register service
     */
    @Test
    public void usernameTaken() {
        userService.register(newUser);

        UserData identicalUser = new UserData(username, password, email);
        UserData sameUsername = new UserData(username, "12345", "none");

        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(identicalUser));
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(sameUsername));
    }

    /**
     * positive test for login service
     */
    @Test
    public void normalLogin() {
        userService.register(newUser);

        AuthData loginData = userService.login(new LoginRequest(newUser.username(), newUser.password()));

        Assertions.assertEquals(loginData.username(), newUser.username());
        Assertions.assertNotNull(loginData.authToken());
    }


    /**
     * negative test for login service
     */
    @Test
    public void loginNoUsername() {
        userService.register(newUser);

        Assertions.assertThrows(BadRequestException.class, () -> userService.login(new LoginRequest("", newUser.password())));
    }

    /**
     * negative test for login service
     */
    @Test
    public void loginInvalidCredentials() {
        userService.register(newUser);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> userService.login(new LoginRequest(newUser.username(), "passssword")));
    }

    /**
     * positive test for logout service
     */
    @Test
    public void logoutNormal() {
        userService.register(newUser);
        AuthData authData = userService.login(new LoginRequest(username, password));

        userService.logout(authData.authToken());

        Assertions.assertThrows(UnauthorizedException.class, () -> authDao.validateAuthData(authData.authToken()));
    }

    /**
     * negative test for logout service
     */
    @Test
    public void logoutUnauthorized() {
        userService.register(newUser);
        userService.login(new LoginRequest(username, password));

        Assertions.assertThrows(UnauthorizedException.class, () -> userService.logout("notavalidauthtoken"));
    }
}
