package service;

import dataaccess.*;
import handlers.RegisterResult;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    UserDao userDao;
    AuthTokenDao authDao;

    public UserService(boolean useLocalData) {
        if (useLocalData) {
            userDao = new LocalUserDao();
            authDao = new LocalAuthTokenDao();
        }
    }

    public UserService() {
        this(true);
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(UserData request) throws AlreadyTakenException {
        UserData userData = userDao.getUserData(request.username());
        if (userData != null) {
            throw new AlreadyTakenException("Username " + request.username() + " is already taken");
        }

        userDao.createUser(request);

        AuthData authData = new AuthData(UserService.generateAuthToken(), request.username());
        authDao.createAuth(authData);

        return new RegisterResult(request.username(), authData);
    }

    public UserData getUser(String username) throws UserNotFoundException {
        UserData data = userDao.getUserData(username);
        if (data == null) {
            throw new UserNotFoundException(username);
        }
        return data;
    }
}
