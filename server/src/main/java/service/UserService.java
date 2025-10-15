package service;

import dataaccess.AuthTokenDao;
import dataaccess.UserDao;
import dataaccess.UserNotFoundException;
import handlers.LoginRequest;
import handlers.RegisterResult;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private UserDao userDao;
    private AuthTokenDao authDao;

    public UserService(UserDao userDao, AuthTokenDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(UserData request) throws AlreadyTakenException, BadRequestException {
        validateString(request.username());
        validateString(request.password());
        validateString(request.email());

        UserData userData = userDao.getUserData(request.username());
        if (userData != null) {
            throw new AlreadyTakenException("Error: Username " + request.username() + " is already taken");
        }

        userDao.createUser(request);

        AuthData authData = createAuth(request.username());

        return new RegisterResult(request.username(), authData.authToken());
    }

    private void validateString(Object o) throws BadRequestException {
        if (o == null || !o.getClass().equals(String.class) || ((String) o).isEmpty()) {
            throw new BadRequestException("Error: non-empty string expected");
        }
    }

    private AuthData createAuth(String username) {
        AuthData authData = new AuthData(username, UserService.generateAuthToken());
        authDao.createAuth(authData);

        return authData;
    }

    public UserData getUser(String username) throws UserNotFoundException {
        UserData data = userDao.getUserData(username);
        if (data == null) {
            throw new UserNotFoundException(username);
        }

        return data;
    }

    public AuthData login(LoginRequest request) throws BadRequestException, UnauthorizedException {
        validateString(request.username());
        validateString(request.password());

        validateLoginData(request);

        return createAuth(request.username());
    }

    private void validateLoginData(LoginRequest userToValidate) throws UnauthorizedException {
        UserData storedUserData = userDao.getUserData(userToValidate.username());

        if (storedUserData == null || !storedUserData.password().equals(userToValidate.password())) {
            throw new UnauthorizedException("");
        }
    }

    public void logout(String authToken) throws UnauthorizedException {
        validateString(authToken);

        validateAuthData(authToken);

        authDao.deleteAuth(authToken);
    }

    public void validateAuthData(String authToken) throws UnauthorizedException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("");
        }
    }
}
