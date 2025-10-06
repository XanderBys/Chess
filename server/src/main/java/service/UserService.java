package service;

import dataaccess.AuthTokenDao;
import dataaccess.LocalAuthTokenDao;
import dataaccess.LocalUserDao;
import dataaccess.UserDao;
import handlers.RegisterResult;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    UserDao userDao = new LocalUserDao();
    AuthTokenDao authDao = new LocalAuthTokenDao();

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    RegisterResult register(UserData request) throws AlreadyTakenException {
        UserData userData = userDao.getUserData(request.username());
        if (userData != null) {
            throw new AlreadyTakenException("Username " + request.username() + " is already taken");
        }

        userDao.createUser(request);

        AuthData authData = new AuthData(UserService.generateAuthToken(), request.username());
        authDao.createAuth(authData);

        return new RegisterResult(request.username(), authData);
    }
}
